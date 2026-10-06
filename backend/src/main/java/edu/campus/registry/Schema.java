package edu.campus.registry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Describes every table exposed by the API; mirrors student_college_management.sql. */
final class Schema {

    record Field(String name, String label, String type, boolean required, String ref, List<String> choices) {
        static Field text(String n, String l, boolean req) { return new Field(n, l, "text", req, null, null); }
        static Field email(String n, String l, boolean req) { return new Field(n, l, "email", req, null, null); }
        static Field number(String n, String l, boolean req) { return new Field(n, l, "number", req, null, null); }
        static Field ref(String n, String l, String table, boolean req) { return new Field(n, l, "ref", req, table, null); }
        static Field choice(String n, String l, boolean req, List<String> c) { return new Field(n, l, "choice", req, null, c); }
    }

    record Column(String key, String label) {}

    record Table(String key, String label, String singular, List<String> pk, List<Field> fields,
                 List<Column> columns, String listSql, String optionSql) {}

    static final Map<String, Table> TABLES = new LinkedHashMap<>();

    private static void add(Table t) { TABLES.put(t.key(), t); }

    static {
        add(new Table("student", "Students", "student", List.of("student_id"),
            List.of(Field.text("name", "Full name", true),
                    Field.email("email", "Email", true),
                    Field.text("phone", "Phone", false),
                    Field.ref("advisor_id", "Advisor", "faculty", false)),
            List.of(new Column("student_id", "ID"), new Column("name", "Name"), new Column("email", "Email"),
                    new Column("phone", "Phone"), new Column("advisor", "Advisor")),
            "SELECT s.student_id, s.name, s.email, s.phone, f.name AS advisor "
                + "FROM student s LEFT JOIN faculty f ON f.faculty_id = s.advisor_id ORDER BY s.student_id",
            "SELECT student_id AS id, name AS label FROM student ORDER BY name"));

        add(new Table("faculty", "Faculty", "faculty member", List.of("faculty_id"),
            List.of(Field.text("name", "Full name", true),
                    Field.email("email", "Email", true),
                    Field.text("phone", "Phone", false),
                    Field.ref("dept_id", "Department", "department", true)),
            List.of(new Column("faculty_id", "ID"), new Column("name", "Name"), new Column("email", "Email"),
                    new Column("phone", "Phone"), new Column("department", "Department")),
            "SELECT f.faculty_id, f.name, f.email, f.phone, d.dept_name AS department "
                + "FROM faculty f JOIN department d ON d.dept_id = f.dept_id ORDER BY f.faculty_id",
            "SELECT faculty_id AS id, name AS label FROM faculty ORDER BY name"));

        add(new Table("course", "Courses", "course", List.of("course_id"),
            List.of(Field.text("course_name", "Course name", true),
                    Field.number("credits", "Credits (1-6)", true),
                    Field.ref("dept_id", "Department", "department", true)),
            List.of(new Column("course_id", "ID"), new Column("course_name", "Course"),
                    new Column("credits", "Credits"), new Column("department", "Department")),
            "SELECT c.course_id, c.course_name, c.credits, d.dept_name AS department "
                + "FROM course c JOIN department d ON d.dept_id = c.dept_id ORDER BY c.course_id",
            "SELECT course_id AS id, course_name AS label FROM course ORDER BY course_name"));

        add(new Table("department", "Departments", "department", List.of("dept_id"),
            List.of(Field.text("dept_name", "Department name", true),
                    Field.text("office", "Office", false),
                    Field.ref("college_id", "College", "college", true)),
            List.of(new Column("dept_id", "ID"), new Column("dept_name", "Department"),
                    new Column("office", "Office"), new Column("college", "College")),
            "SELECT d.dept_id, d.dept_name, d.office, c.college_name AS college "
                + "FROM department d JOIN college c ON c.college_id = d.college_id ORDER BY d.dept_id",
            "SELECT dept_id AS id, dept_name AS label FROM department ORDER BY dept_name"));

        add(new Table("college", "Colleges", "college", List.of("college_id"),
            List.of(Field.text("college_name", "College name", true),
                    Field.text("address", "Address", false),
                    Field.email("email", "Email", false),
                    Field.text("phone", "Phone", false)),
            List.of(new Column("college_id", "ID"), new Column("college_name", "College"),
                    new Column("address", "Address"), new Column("email", "Email"), new Column("phone", "Phone")),
            "SELECT college_id, college_name, address, email, phone FROM college ORDER BY college_id",
            "SELECT college_id AS id, college_name AS label FROM college ORDER BY college_name"));

        add(new Table("enrolled_in", "Enrollments", "enrollment", List.of("student_id", "course_id", "semester"),
            List.of(Field.ref("student_id", "Student", "student", true),
                    Field.ref("course_id", "Course", "course", true),
                    Field.text("semester", "Semester (e.g. 2026-S1)", true),
                    Field.choice("grade", "Grade", false, List.of("A+", "A", "B+", "B", "C+", "C", "D", "F", "I"))),
            List.of(new Column("student", "Student"), new Column("course", "Course"),
                    new Column("semester", "Semester"), new Column("grade", "Grade")),
            "SELECT e.student_id, e.course_id, e.semester, e.grade, s.name AS student, c.course_name AS course "
                + "FROM enrolled_in e JOIN student s ON s.student_id = e.student_id "
                + "JOIN course c ON c.course_id = e.course_id ORDER BY s.name, c.course_name",
            null));

        add(new Table("teaches", "Teaching", "assignment", List.of("faculty_id", "course_id"),
            List.of(Field.ref("faculty_id", "Faculty", "faculty", true),
                    Field.ref("course_id", "Course", "course", true)),
            List.of(new Column("faculty", "Faculty"), new Column("course", "Course")),
            "SELECT t.faculty_id, t.course_id, f.name AS faculty, c.course_name AS course "
                + "FROM teaches t JOIN faculty f ON f.faculty_id = t.faculty_id "
                + "JOIN course c ON c.course_id = t.course_id ORDER BY f.name, c.course_name",
            null));
    }

    private Schema() {}
}
