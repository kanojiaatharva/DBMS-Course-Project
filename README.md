# Student and College Management System

**Name:** Atharva Kanojia  
**Roll No:** 25WU0101019  
**Course:** Database Management Systems (DBMS)  

---

## Tech Stack

- **Database:** MySQL 8.0+ (InnoDB)
- **Backend:** Java 21+ (JDK HttpServer, JDBC, Gson)
- **Frontend:** React 18, Vite, Vanilla CSS (JetBrains Mono font)

---

## Database Setup

1. Open MySQL and run the schema file:
   ```bash
   mysql -u root -p < database/student_college_management.sql
   ```
2. Configure credentials in `backend/db.properties`:
   ```properties
   url=jdbc:mysql://localhost:3306/student_college_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   user=YOUR_MYSQL_USER
   password=YOUR_MYSQL_PASSWORD
   ```

---

## Running the Application

### 1. Start Backend (Java API)
```bash
cd backend
mvn compile exec:java
```
API runs on `http://localhost:8080/api`.

### 2. Start Frontend (React UI)
```bash
cd frontend
npm install
npm run dev
```
Dashboard runs on `http://localhost:5173`.

---

## Features

- **View Records:** Schema-driven data grid for all 7 tables (`student`, `faculty`, `course`, `department`, `college`, `enrolled_in`, `teaches`).
- **Insert Records:** Drawer form with relational dropdown selectors and input validation.
- **Delete Records:** Inline deletion with two-step confirmation safeguards.
- **Data Integrity:** Graceful handling of foreign key restrictions and unique constraint errors.
- **Search:** Instant multi-column filtering.
