package edu.campus.registry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;

/** Minimal REST API (JDK HttpServer + JDBC) for the student_college_management database. */
public final class App {

    private static final Gson GSON = new Gson();
    private static String url, user, password;

    public static void main(String[] args) throws Exception {
        loadConfig();
        int port = Integer.parseInt(System.getProperty("port", env("PORT", "8080")));

        try (Connection c = connect()) {
            System.out.println("Connected to " + url);
        } catch (SQLException e) {
            System.err.println("Could not connect to MySQL: " + e.getMessage());
            System.err.println("Set DB_URL / DB_USER / DB_PASSWORD or edit backend/db.properties");
            System.exit(1);
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api", App::handle);
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
        System.out.println("API listening on http://localhost:" + port + "/api");
    }

    // ---------- configuration ----------

    private static void loadConfig() throws IOException {
        Properties p = new Properties();
        Path file = Path.of("db.properties");
        if (Files.exists(file)) {
            try (InputStream in = Files.newInputStream(file)) { p.load(in); }
        }
        url = env("DB_URL", p.getProperty("url", "jdbc:mysql://localhost:3306/student_college_management"));
        user = env("DB_USER", p.getProperty("user", "root"));
        password = env("DB_PASSWORD", p.getProperty("password", ""));
    }

    private static String env(String key, String fallback) {
        String v = System.getenv(key);
        return v == null || v.isBlank() ? fallback : v;
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    // ---------- routing ----------

    private static void handle(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,DELETE,OPTIONS");
        ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        try {
            String method = ex.getRequestMethod();
            if (method.equals("OPTIONS")) { send(ex, 204, null); return; }

            String[] parts = ex.getRequestURI().getPath().replaceFirst("^/api/?", "").split("/");
            String head = parts[0];

            if (head.equals("meta") && method.equals("GET")) { send(ex, 200, meta()); return; }
            if (head.equals("options") && parts.length == 2 && method.equals("GET")) {
                send(ex, 200, options(table(parts[1]))); return;
            }
            if (head.equals("tables") && parts.length == 2) {
                Schema.Table t = table(parts[1]);
                switch (method) {
                    case "GET" -> send(ex, 200, list(t));
                    case "POST" -> { insert(t, body(ex)); send(ex, 201, Map.of("ok", true)); }
                    case "DELETE" -> {
                        int n = delete(t, query(ex));
                        send(ex, n > 0 ? 200 : 404, Map.of("deleted", n));
                    }
                    default -> send(ex, 405, Map.of("error", "Method not allowed"));
                }
                return;
            }
            send(ex, 404, Map.of("error", "Not found"));
        } catch (ApiError e) {
            send(ex, e.status, Map.of("error", e.getMessage()));
        } catch (SQLException e) {
            send(ex, sqlStatus(e), Map.of("error", friendly(e)));
        } catch (Exception e) {
            e.printStackTrace();
            send(ex, 500, Map.of("error", "Unexpected server error"));
        }
    }

    private static Schema.Table table(String key) {
        Schema.Table t = Schema.TABLES.get(key);
        if (t == null) throw new ApiError(404, "Unknown table: " + key);
        return t;
    }

    // ---------- operations ----------

    private static Object meta() throws SQLException {
        Map<String, Object> out = new LinkedHashMap<>();
        try (Connection c = connect()) {
            for (Schema.Table t : Schema.TABLES.values()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("key", t.key());
                m.put("label", t.label());
                m.put("singular", t.singular());
                m.put("pk", t.pk());
                m.put("fields", t.fields());
                m.put("columns", t.columns());
                try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM `" + t.key() + "`");
                     ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    m.put("count", rs.getLong(1));
                }
                out.put(t.key(), m);
            }
        }
        return out;
    }

    private static List<Map<String, Object>> options(Schema.Table t) throws SQLException {
        if (t.optionSql() == null) throw new ApiError(404, "No options for " + t.key());
        return query(t.optionSql());
    }

    private static List<Map<String, Object>> list(Schema.Table t) throws SQLException {
        return query(t.listSql());
    }

    private static List<Map<String, Object>> query(String sql) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= md.getColumnCount(); i++) row.put(md.getColumnLabel(i), rs.getObject(i));
                rows.add(row);
            }
        }
        return rows;
    }

    private static void insert(Schema.Table t, JsonObject body) throws SQLException {
        List<String> cols = new ArrayList<>();
        List<String> vals = new ArrayList<>();
        for (Schema.Field f : t.fields()) {
            JsonElement el = body.get(f.name());
            String v = (el == null || el.isJsonNull()) ? "" : el.getAsString().trim();
            if (v.isEmpty()) {
                if (f.required()) throw new ApiError(400, f.label() + " is required");
                continue;
            }
            cols.add("`" + f.name() + "`");
            vals.add(v);
        }
        String sql = "INSERT INTO `" + t.key() + "` (" + String.join(",", cols) + ") VALUES ("
                + "?,".repeat(cols.size()).replaceAll(",$", "") + ")";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < vals.size(); i++) ps.setString(i + 1, vals.get(i));
            ps.executeUpdate();
        }
    }

    private static int delete(Schema.Table t, Map<String, String> params) throws SQLException {
        List<String> where = new ArrayList<>();
        List<String> vals = new ArrayList<>();
        for (String k : t.pk()) {
            String v = params.get(k);
            if (v == null) throw new ApiError(400, "Missing key: " + k);
            where.add("`" + k + "` = ?");
            vals.add(v);
        }
        String sql = "DELETE FROM `" + t.key() + "` WHERE " + String.join(" AND ", where);
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < vals.size(); i++) ps.setString(i + 1, vals.get(i));
            return ps.executeUpdate();
        }
    }

    // ---------- helpers ----------

    private static JsonObject body(HttpExchange ex) throws IOException {
        String s = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            JsonObject o = GSON.fromJson(s, JsonObject.class);
            if (o == null) throw new ApiError(400, "Empty body");
            return o;
        } catch (RuntimeException e) {
            if (e instanceof ApiError) throw e;
            throw new ApiError(400, "Invalid JSON");
        }
    }

    private static Map<String, String> query(HttpExchange ex) {
        Map<String, String> m = new HashMap<>();
        String q = ex.getRequestURI().getRawQuery();
        if (q == null) return m;
        for (String pair : q.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                m.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return m;
    }

    private static void send(HttpExchange ex, int status, Object payload) throws IOException {
        if (payload == null) { ex.sendResponseHeaders(status, -1); ex.close(); return; }
        byte[] bytes = GSON.toJson(payload).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }

    private static int sqlStatus(SQLException e) {
        int code = e.getErrorCode();
        return (code == 1062 || code == 1451 || code == 1452 || code == 3819) ? 409 : 500;
    }

    private static String friendly(SQLException e) {
        return switch (e.getErrorCode()) {
            case 1062 -> "A record with the same unique value already exists.";
            case 1451 -> "Cannot delete: other records still depend on this one. Remove those first.";
            case 1452 -> "Invalid reference: the selected related record does not exist.";
            case 3819 -> "A value violates a database check (e.g. credits must be 1-6).";
            case 1366, 1265 -> "One of the values has the wrong format.";
            default -> "Database error: " + e.getMessage();
        };
    }

    private static final class ApiError extends RuntimeException {
        final int status;
        ApiError(int status, String msg) { super(msg); this.status = status; }
    }
}
