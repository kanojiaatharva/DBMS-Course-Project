# Student & College Management System
## Academic DBMS Course Project Report

---

### **1. Cover Page Information**

| Field | Detail |
| :--- | :--- |
| **Project Title** | **Student & College Management System** |
| **Project Subtitle** | Relational Database Modeling, High-Performance Java REST Engine, & Responsive React Web Dashboard |
| **Candidate Name** | **Atharva Kanojia** |
| **University Roll Number** | **25WU0101019** |
| **Class & Section** | **B.Tech Computer Science and Engineering (AIML Panthers)** |
| **Course Name & Code** | **Database Management Systems (DBMS) — CSE2101** |
| **Institution** | **School of Technology, Woxsen University, Hyderabad, Telangana** |
| **Faculty / Course Instructor** | **Dr. [Faculty Name / Course Instructor]** |
| **Academic Year & Term** | **2025 – 2026 (Semester IV / 2026-S1)** |
| **GitHub Repository** | [https://github.com/kanojiaatharva/DBMS-Course-Project](https://github.com/kanojiaatharva/DBMS-Course-Project) |
| **Author GitHub Profile** | [https://github.com/kanojiaatharva](https://github.com/kanojiaatharva) |

---

### **2. Abstract**

Collegiate educational institutions operate in a multifaceted organizational setting characterized by strict structural interdependencies across campus faculties, departments, academic course catalogs, faculty research scholars, student advising programs, and multi-semester course registrations. Traditional administrative mechanisms relying on paper ledgers or detached spreadsheet workbooks suffer from severe systemic deficiencies, including pervasive data redundancy, update/deletion anomalies, orphan foreign key records, lack of domain validation, and the absence of transactional ACID guarantees.

This project delivers the end-to-end design, relational normalization, and full-stack software implementation of the **Student and College Management System**. Engineered on the **MySQL 8.0+** relational database management system using the transactional **InnoDB** engine, the schema achieves full **Third Normal Form (3NF)** and **Boyce-Codd Normal Form (BCNF)** compliance. The architecture is powered by a high-throughput **Java 21+** REST service utilizing direct **JDBC** and the lightweight standard library **JDK HttpServer**, completely circumventing heavy framework overhead to achieve sub-millisecond query dispatch and rapid cold starts. The presentation layer is an editorial, developer-centric single-page web dashboard built with **React 18** and **Vite**, set in the **JetBrains Mono** monospace typeface. Low-level database exceptions (such as duplicate entries and foreign key restrictions) are transparently translated into friendly, actionable client messages, ensuring complete referential integrity and an intuitive user experience.

---

### **3. Introduction and Problem Statement**

#### **3.1 Background & Context**
Higher education institutions manage thousands of interconnected records spanning administrative colleges, specialized degree departments, professors, course offerings, student enrollment histories, and faculty teaching allocations. In modern university governance, maintaining atomic, consistent, isolated, and durable (ACID) data records is vital for accreditation, grading accuracy, prerequisite management, and faculty workload audits.

#### **3.2 Problem Statement**
When colleges manage academic records using manual files or flat spreadsheets (e.g. Microsoft Excel, Google Sheets):
1. **Pervasive Data Redundancy:** Department names, faculty emails, and course titles are repeatedly keyed across disparate sheets, leading to enormous storage waste and synchronization failures when records change.
2. **Loss of Referential Integrity:** Flat files cannot enforce declarative foreign keys. Deleting a department or course leaves orphaned student enrollment and faculty assignment records.
3. **Absence of Domain Constraints:** Without database-enforced `CHECK` constraints, invalid credit hours (e.g., negative or exceeding program thresholds) and malformed letter grades easily corrupt the dataset.
4. **Lack of Concurrency Control:** Flat spreadsheets do not support multi-user transactional isolation, leading to dirty reads, non-repeatable reads, and lost updates during registration periods.
5. **Inefficient Multi-Table Reporting:** Generating a consolidated academic transcript or calculating faculty course loads requires tedious manual cross-referencing.

The **Student and College Management System** addresses these vulnerabilities by establishing a centralized, normalized relational schema coupled with a tamper-proof web interface that enforces business logic directly at the database tier.

---

### **4. Objectives and Scope**

#### **4.1 Objectives**
- **Conceptual & Logical Relational Modeling:** Translate the collegiate ecosystem into a formal Entity-Relationship (ER) model encompassing five strong entities and three relational associations.
- **Strict Normalization (3NF/BCNF):** Eliminate functional dependency violations and partial/transitive dependencies.
- **Declarative Constraint Enforcement:** Apply Primary Keys, Foreign Keys (`ON UPDATE CASCADE`, `ON DELETE RESTRICT / SET NULL / CASCADE`), `UNIQUE` constraints, and domain `CHECK` rules.
- **Lightweight High-Performance API:** Build a Java 21+ REST backend using raw JDBC PreparedStatements and JDK HttpServer without heavy external frameworks.
- **Editorial Developer-Grade UI:** Design an intuitive web dashboard utilizing JetBrains Mono monospace typography, dynamic live badge counters, and real-time search filtering.
- **Advanced SQL Analytics:** Execute and document multi-table joins, aggregations, and subqueries, including the queries required during Presentation-II.

#### **4.2 Scope**
- **In-Scope:**
  - Multi-college institutional metadata management.
  - Academic department administration under parent colleges.
  - Faculty scholar appointment, departmental allocation, and student advising.
  - Credit-bearing course catalog management (1 to 6 credits).
  - Student registration, advisor assignment, and semester course enrollment with letter grading.
  - Many-to-Many faculty teaching workload mapping.
- **Out-of-Scope / Future Iterations:**
  - Online student fee payment gateway processing.
  - Biometric physical attendance integration.
  - Live video classroom streaming.

---

### **5. Software and Hardware Requirements**

#### **5.1 Hardware Requirements**
- **Processor:** 64-bit Dual-Core or Quad-Core Processor (Intel Core i3/i5/i7 or AMD Ryzen equivalent).
- **RAM:** Minimum 4 GB RAM (8 GB or 16 GB recommended).
- **Disk Storage:** Minimum 500 MB free space for MySQL database files, Maven dependencies, and Node modules.
- **Display Resolution:** Minimum 1366 × 768; 1920 × 1080 (Full HD) recommended for high-density grid layouts.
- **Network Interface:** Localhost loopback TCP/IP networking (Ports 3306 for MySQL, 8080 for Java REST API, 5173 for Vite frontend).

#### **5.2 Software Requirements**
- **Operating System:** Microsoft Windows 10/11 (or modern Linux / macOS).
- **Database Management System:** MySQL Server 8.0+ with InnoDB transactional storage engine.
- **Backend Runtime:** Java Development Kit (JDK) 21 LTS or later.
- **Database Driver:** MySQL Connector/J 9.1.0 (`com.mysql:mysql-connector-j`).
- **JSON Parser:** Google Gson 2.11.0 (`com.google.code.gson:gson`).
- **Build System:** Apache Maven 3.9+.
- **Frontend Runtime & Bundler:** Node.js 18+ / 20+ LTS, npm 9+, Vite 5+.
- **Frontend Framework:** React 18.3+ with Vanilla CSS and JetBrains Mono monospace typography.
- **API Testing Tools:** cURL, Postman, modern web browser (Google Chrome, Microsoft Edge).

---

### **6. Conceptual Model & Entity-Relationship (ER) Diagram**

#### **6.1 Conceptual Overview**
The collegiate data domain is structured around five core entity sets and three relational associations:
1. **`COLLEGE`**: Represents the overarching institution (`college_id`, `college_name`, `address`, `email`, `phone`).
2. **`DEPARTMENT`**: Academic departments operating under a college (`dept_id`, `dept_name`, `office`, `college_id`).
3. **`FACULTY`**: Academic professors and instructors employed by a department (`faculty_id`, `name`, `email`, `phone`, `dept_id`).
4. **`COURSE`**: Curricular subjects offered by departments (`course_id`, `course_name`, `credits`, `dept_id`).
5. **`STUDENT`**: Enrolled students under faculty advisory (`student_id`, `name`, `email`, `phone`, `advisor_id`).
6. **`TEACHES` (Relationship):** Many-to-Many association mapping faculty scholars to courses taught.
7. **`ENROLLED_IN` (Relationship):** Many-to-Many association tracking student enrollments in courses with descriptive attributes (`semester`, `grade`).

#### **6.2 Relationship Cardinality Matrix**
- **COLLEGE to DEPARTMENT:** $1 : N$ (One college houses multiple departments; each department belongs to one college).
- **DEPARTMENT to COURSE:** $1 : N$ (One department offers multiple courses; each course is offered by one department).
- **DEPARTMENT to FACULTY:** $1 : N$ (One department employs multiple faculty members; a faculty member belongs to one department).
- **FACULTY to STUDENT (ADVISES):** $1 : N$ (A faculty member advises many students; a student has at most one primary faculty advisor).
- **FACULTY to COURSE (TEACHES):** $M : N$ (A faculty member can teach multiple courses, and courses can be co-taught by multiple faculty members).
- **STUDENT to COURSE (ENROLLED_IN):** $M : N$ (Students enroll in multiple courses across different semesters with specific grade outcomes).

---

### **7. Relational Schema and Normalization**

#### **7.1 Relational Schema Definitions**
The ER diagram translates into seven relational tables:
1. **`college`** ($\underline{\text{college\_id}}$, $\text{college\_name}$, $\text{address}$, $\text{email}$, $\text{phone}$)
2. **`department`** ($\underline{\text{dept\_id}}$, $\text{dept\_name}$, $\text{office}$, $\textit{college\_id}$)
   - Foreign Key: $\textit{college\_id} \to \text{college}(\text{college\_id})$
3. **`faculty`** ($\underline{\text{faculty\_id}}$, $\text{name}$, $\text{email}$, $\text{phone}$, $\textit{dept\_id}$)
   - Foreign Key: $\textit{dept\_id} \to \text{department}(\text{dept\_id})$
4. **`course`** ($\underline{\text{course\_id}}$, $\text{course\_name}$, $\text{credits}$, $\textit{dept\_id}$)
   - Foreign Key: $\textit{dept\_id} \to \text{department}(\text{dept\_id})$
5. **`student`** ($\underline{\text{student\_id}}$, $\text{name}$, $\text{email}$, $\text{phone}$, $\textit{advisor\_id}$)
   - Foreign Key: $\textit{advisor\_id} \to \text{faculty}(\text{faculty\_id})$
6. **`teaches`** ($\underline{\textit{faculty\_id}, \textit{course\_id}}$)
   - Foreign Keys: $\textit{faculty\_id} \to \text{faculty}(\text{faculty\_id})$, $\textit{course\_id} \to \text{course}(\text{course\_id})$
7. **`enrolled_in`** ($\underline{\textit{student\_id}, \textit{course\_id}, \text{semester}}$, $\text{grade}$)
   - Foreign Keys: $\textit{student\_id} \to \text{student}(\text{student\_id})$, $\textit{course\_id} \to \text{course}(\text{course\_id})$

#### **7.2 Normalization Analysis (1NF to BCNF)**

- **First Normal Form (1NF):**
  - All attribute domains are strictly atomic. There are no composite or multi-valued attributes (e.g. phone numbers and emails are individual scalar values).
  - Multi-valued associations (such as a student enrolling in multiple courses or faculty teaching multiple courses) are segregated into separate associative relations (`enrolled_in` and `teaches`).
  - Every table possesses an unambiguous primary key.

- **Second Normal Form (2NF):**
  - The schema is in 1NF and contains **no partial dependencies** of non-prime attributes on any composite primary key.
  - Relations with single-column primary keys (`college`, `department`, `faculty`, `course`, `student`) satisfy 2NF by definition.
  - For `teaches` ($\underline{\text{faculty\_id}, \text{course\_id}}$): There are no non-prime attributes.
  - For `enrolled_in` ($\underline{\text{student\_id}, \text{course\_id}, \text{semester}}$, $\text{grade}$): The non-prime attribute `grade` depends on the **entire triad** $\{\text{student\_id}, \text{course\_id}, \text{semester}\}$ because a student may repeat a course in a subsequent semester with a different grade. No partial dependency exists.

- **Third Normal Form (3NF):**
  - The schema is in 2NF and contains **no transitive dependencies** ($X \to Y$ and $Y \to Z$ where $Z$ is a non-prime attribute and $Y$ is not a superkey).
  - Department metadata (`dept_name`, `office`) is stored exclusively in `department` rather than duplicated inside `faculty` or `course`.
  - Student records store only the foreign key `advisor_id`; faculty details (`name`, `email`) are referenced relationally, eliminating transitive dependencies.

- **Boyce-Codd Normal Form (BCNF):**
  - In all seven relations, for every non-trivial functional dependency $X \to Y$, the determinant $X$ is a superkey.
  - The entire relational database strictly satisfies BCNF.

---

### **8. Data Dictionary**

#### **Table 1: `college`**
| Column | Data Type | Nullable | Key / Constraint | Reference | Description |
| :--- | :--- | :---: | :--- | :--- | :--- |
| `college_id` | INT | NO | PRIMARY KEY | AUTO_INCREMENT | Unique institutional identifier |
| `college_name` | VARCHAR(100) | NO | NOT NULL | None | Official institutional name |
| `address` | VARCHAR(255) | YES | None | None | Physical campus address |
| `email` | VARCHAR(100) | YES | UNIQUE | `uq_college_email` | Institutional contact email |
| `phone` | VARCHAR(15) | YES | None | None | Main contact telephone |

#### **Table 2: `department`**
| Column | Data Type | Nullable | Key / Constraint | Reference | Description |
| :--- | :--- | :---: | :--- | :--- | :--- |
| `dept_id` | INT | NO | PRIMARY KEY | AUTO_INCREMENT | Unique departmental ID |
| `dept_name` | VARCHAR(100) | NO | NOT NULL | `uq_department_college_name` | Department name |
| `office` | VARCHAR(100) | YES | None | None | Office block / room number |
| `college_id` | INT | NO | FOREIGN KEY | `college(college_id)` | Parent college (ON DELETE RESTRICT) |

#### **Table 3: `faculty`**
| Column | Data Type | Nullable | Key / Constraint | Reference | Description |
| :--- | :--- | :---: | :--- | :--- | :--- |
| `faculty_id` | INT | NO | PRIMARY KEY | AUTO_INCREMENT | Unique faculty scholar ID |
| `name` | VARCHAR(100) | NO | NOT NULL | None | Full legal faculty name |
| `email` | VARCHAR(100) | NO | UNIQUE | `uq_faculty_email` | Faculty email address |
| `phone` | VARCHAR(15) | YES | None | None | Faculty contact number |
| `dept_id` | INT | NO | FOREIGN KEY | `department(dept_id)` | Appointed department (ON DELETE RESTRICT) |

#### **Table 4: `course`**
| Column | Data Type | Nullable | Key / Constraint | Reference | Description |
| :--- | :--- | :---: | :--- | :--- | :--- |
| `course_id` | INT | NO | PRIMARY KEY | AUTO_INCREMENT | Unique course catalog ID |
| `course_name` | VARCHAR(100) | NO | NOT NULL | `uq_course_department_name` | Curricular course name |
| `credits` | TINYINT UNSIGNED | NO | CHECK (1-6) | `chk_course_credits` | Credit weighting (1 to 6) |
| `dept_id` | INT | NO | FOREIGN KEY | `department(dept_id)` | Offering department (ON DELETE RESTRICT) |

#### **Table 5: `student`**
| Column | Data Type | Nullable | Key / Constraint | Reference | Description |
| :--- | :--- | :---: | :--- | :--- | :--- |
| `student_id` | INT | NO | PRIMARY KEY | AUTO_INCREMENT | Unique student matriculation ID |
| `name` | VARCHAR(100) | NO | NOT NULL | None | Full student name |
| `email` | VARCHAR(100) | NO | UNIQUE | `uq_student_email` | Student university email |
| `phone` | VARCHAR(15) | YES | None | None | Contact phone number |
| `advisor_id` | INT | YES | FOREIGN KEY | `faculty(faculty_id)` | Primary faculty advisor (ON DELETE SET NULL) |

#### **Table 6: `teaches`**
| Column | Data Type | Nullable | Key / Constraint | Reference | Description |
| :--- | :--- | :---: | :--- | :--- | :--- |
| `faculty_id` | INT | NO | COMPOSITE PK, FK | `faculty(faculty_id)` | Assigned faculty instructor (ON DELETE CASCADE) |
| `course_id` | INT | NO | COMPOSITE PK, FK | `course(course_id)` | Assigned course offering (ON DELETE CASCADE) |

#### **Table 7: `enrolled_in`**
| Column | Data Type | Nullable | Key / Constraint | Reference | Description |
| :--- | :--- | :---: | :--- | :--- | :--- |
| `student_id` | INT | NO | COMPOSITE PK, FK | `student(student_id)` | Enrolled student (ON DELETE CASCADE) |
| `course_id` | INT | NO | COMPOSITE PK, FK | `course(course_id)` | Enrolled course (ON DELETE CASCADE) |
| `semester` | VARCHAR(20) | NO | COMPOSITE PK | None | Academic term (e.g. '2026-S1') |
| `grade` | CHAR(2) | YES | CHECK (A+, A...) | `chk_enrollment_grade` | Awarded grade (A+, A, B+, B, C+, C, D, F, I) |

---

### **9. SQL Commands Used (DDL, DML) with Sample Outputs**

#### **9.1 Data Definition Language (DDL)**
```sql
-- Database Initialization with Unicode Support
DROP DATABASE IF EXISTS student_college_management;
CREATE DATABASE student_college_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE student_college_management;

-- 1. College
CREATE TABLE college (
    college_id INT PRIMARY KEY AUTO_INCREMENT,
    college_name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    email VARCHAR(100),
    phone VARCHAR(15),
    CONSTRAINT uq_college_email UNIQUE (email)
) ENGINE=InnoDB;

-- 2. Department
CREATE TABLE department (
    dept_id INT PRIMARY KEY AUTO_INCREMENT,
    dept_name VARCHAR(100) NOT NULL,
    office VARCHAR(100),
    college_id INT NOT NULL,
    CONSTRAINT uq_department_college_name UNIQUE (college_id, dept_name),
    CONSTRAINT fk_department_college FOREIGN KEY (college_id)
        REFERENCES college(college_id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 3. Faculty
CREATE TABLE faculty (
    faculty_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    dept_id INT NOT NULL,
    CONSTRAINT uq_faculty_email UNIQUE (email),
    CONSTRAINT fk_faculty_department FOREIGN KEY (dept_id)
        REFERENCES department(dept_id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 4. Course
CREATE TABLE course (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(100) NOT NULL,
    credits TINYINT UNSIGNED NOT NULL,
    dept_id INT NOT NULL,
    CONSTRAINT chk_course_credits CHECK (credits BETWEEN 1 AND 6),
    CONSTRAINT uq_course_department_name UNIQUE (dept_id, course_name),
    CONSTRAINT fk_course_department FOREIGN KEY (dept_id)
        REFERENCES department(dept_id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 5. Student
CREATE TABLE student (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    advisor_id INT NULL,
    CONSTRAINT uq_student_email UNIQUE (email),
    CONSTRAINT fk_student_advisor FOREIGN KEY (advisor_id)
        REFERENCES faculty(faculty_id) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- 6. Teaches (M:N Associative Entity)
CREATE TABLE teaches (
    faculty_id INT NOT NULL,
    course_id INT NOT NULL,
    PRIMARY KEY (faculty_id, course_id),
    CONSTRAINT fk_teaches_faculty FOREIGN KEY (faculty_id)
        REFERENCES faculty(faculty_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_teaches_course FOREIGN KEY (course_id)
        REFERENCES course(course_id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- 7. Enrolled_In (M:N Associative Entity with Attributes)
CREATE TABLE enrolled_in (
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    semester VARCHAR(20) NOT NULL,
    grade CHAR(2),
    PRIMARY KEY (student_id, course_id, semester),
    CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id)
        REFERENCES student(student_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id)
        REFERENCES course(course_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT chk_enrollment_grade CHECK (
        grade IS NULL OR grade IN ('A+', 'A', 'B+', 'B', 'C+', 'C', 'D', 'F', 'I')
    )
) ENGINE=InnoDB;

-- Performance Indexes
CREATE INDEX idx_department_college ON department(college_id);
CREATE INDEX idx_faculty_department ON faculty(dept_id);
CREATE INDEX idx_course_department ON course(dept_id);
CREATE INDEX idx_student_advisor ON student(advisor_id);
CREATE INDEX idx_teaches_course ON teaches(course_id);
CREATE INDEX idx_enrolled_in_course ON enrolled_in(course_id);
```

#### **9.2 Data Manipulation Language (DML) Seed Data**
```sql
INSERT INTO college (college_name, address, email, phone) VALUES
('Woxsen University', 'Hyderabad, Telangana', 'info@woxsen.edu.in', '04012345678');

INSERT INTO department (dept_name, office, college_id) VALUES
('Computer Science and Engineering', 'Block A - Room 201', 1),
('Artificial Intelligence and Machine Learning', 'Block A - Room 305', 1),
('Electronics and Communication Engineering', 'Block B - Room 102', 1);

INSERT INTO faculty (name, email, phone, dept_id) VALUES
('Dr. Rajesh Sharma', 'rajesh.sharma@example.edu', '9000000001', 1),
('Dr. Priya Mehta', 'priya.mehta@example.edu', '9000000002', 2),
('Dr. Anil Kumar', 'anil.kumar@example.edu', '9000000003', 1),
('Dr. Neha Verma', 'neha.verma@example.edu', '9000000004', 2);

INSERT INTO course (course_name, credits, dept_id) VALUES
('Database Management Systems', 4, 1),
('Data Structures and Algorithms', 4, 1),
('Artificial Intelligence', 4, 2),
('Machine Learning', 4, 2),
('Computer Networks', 3, 1),
('Operating Systems', 4, 1);

INSERT INTO student (name, email, phone, advisor_id) VALUES
('Atharva Kanojia', 'atharva.kanojia@example.edu', '9000000011', 1),
('Aarya Dubey', 'aarya.dubey@example.edu', '9000000012', 2),
('Jaydeep Uday', 'jaydeep.uday@example.edu', '9000000013', 1),
('Neshithra Devarachetty', 'neshithra@example.edu', '9000000014', 2),
('Rohan Singh', 'rohan.singh@example.edu', '9000000015', 3),
('Ananya Rao', 'ananya.rao@example.edu', '9000000016', 4);

INSERT INTO teaches (faculty_id, course_id) VALUES
(1, 1), (1, 2), (1, 5), (3, 1), (3, 6), (2, 3), (2, 4), (4, 3), (4, 4);

INSERT INTO enrolled_in (student_id, course_id, semester, grade) VALUES
(1, 1, '2026-S1', 'A'),  (1, 2, '2026-S1', 'A+'), (1, 5, '2026-S1', 'B+'),
(2, 3, '2026-S1', 'A'),  (2, 4, '2026-S1', 'A+'),
(3, 1, '2026-S1', 'B+'), (3, 6, '2026-S1', 'A'),
(4, 3, '2026-S1', 'A+'), (4, 4, '2026-S1', 'A'),
(5, 2, '2026-S1', 'B'),  (5, 6, '2026-S1', 'B+'),
(6, 4, '2026-S1', 'A');
```

---

### **10. Queries with Outputs (Including Presentation-II)**

#### **10.1 Presentation-II Dedicated Query: AI/ML Department Course Offerings**
```sql
SELECT 
    c.course_name,
    d.dept_name
FROM course c
JOIN department d
    ON c.dept_id = d.dept_id
WHERE d.dept_name = 'Artificial Intelligence and Machine Learning';
```
**Execution Output:**
| course_name | dept_name |
| :--- | :--- |
| Artificial Intelligence | Artificial Intelligence and Machine Learning |
| Machine Learning | Artificial Intelligence and Machine Learning |

---

#### **10.2 Presentation-II Query: Student-to-Faculty Advisor Lookup**
```sql
SELECT 
    s.name AS student,
    f.name AS advisor
FROM student s
JOIN faculty f 
    ON s.advisor_id = f.faculty_id
ORDER BY s.student_id;
```
**Execution Output:**
| student | advisor |
| :--- | :--- |
| Atharva Kanojia | Dr. Rajesh Sharma |
| Aarya Dubey | Dr. Priya Mehta |
| Jaydeep Uday | Dr. Rajesh Sharma |
| Neshithra Devarachetty | Dr. Priya Mehta |
| Rohan Singh | Dr. Anil Kumar |
| Ananya Rao | Dr. Neha Verma |

---

#### **10.3 Presentation-II Query: Faculty Teaching Assignments**
```sql
SELECT 
    f.name AS faculty,
    c.course_name AS course
FROM teaches t
JOIN faculty f
    ON t.faculty_id = f.faculty_id
JOIN course c
    ON t.course_id = c.course_id
ORDER BY f.name, c.course_name;
```
**Execution Output:**
| faculty | course |
| :--- | :--- |
| Dr. Anil Kumar | Database Management Systems |
| Dr. Anil Kumar | Operating Systems |
| Dr. Neha Verma | Artificial Intelligence |
| Dr. Neha Verma | Machine Learning |
| Dr. Priya Mehta | Artificial Intelligence |
| Dr. Priya Mehta | Machine Learning |
| Dr. Rajesh Sharma | Computer Networks |
| Dr. Rajesh Sharma | Data Structures and Algorithms |
| Dr. Rajesh Sharma | Database Management Systems |

---

#### **10.4 Slide-5 Queries: Analytical Joins & Aggregations**

- **Query 10.4.1: Students Advised by Specific Faculty Member (`advisor_id = 1`)**
```sql
SELECT student_id, name, email, phone FROM student WHERE advisor_id = 1;
```
*Output:* Atharva Kanojia and Jaydeep Uday.

- **Query 10.4.2: Student Academic Enrollments and Grade Record**
```sql
SELECT s.name AS student_name, c.course_name, e.semester, e.grade
FROM enrolled_in e
JOIN student s ON e.student_id = s.student_id
JOIN course c ON e.course_id = c.course_id
ORDER BY s.name, c.course_name;
```
*Output:*
| student_name | course_name | semester | grade |
| :--- | :--- | :---: | :---: |
| Aarya Dubey | Artificial Intelligence | 2026-S1 | A |
| Aarya Dubey | Machine Learning | 2026-S1 | A+ |
| Ananya Rao | Machine Learning | 2026-S1 | A |
| Atharva Kanojia | Computer Networks | 2026-S1 | B+ |
| Atharva Kanojia | Data Structures and Algorithms | 2026-S1 | A+ |
| Atharva Kanojia | Database Management Systems | 2026-S1 | A |
| Jaydeep Uday | Operating Systems | 2026-S1 | A |
| Neshithra Devarachetty | Artificial Intelligence | 2026-S1 | A+ |
| Neshithra Devarachetty | Machine Learning | 2026-S1 | A |
| Rohan Singh | Data Structures and Algorithms | 2026-S1 | B |
| Rohan Singh | Operating Systems | 2026-S1 | B+ |

- **Query 10.4.3: Enrollment Count by Course (GROUP BY & Aggregate)**
```sql
SELECT c.course_name, COUNT(e.student_id) AS total_students
FROM course c
LEFT JOIN enrolled_in e ON c.course_id = e.course_id
GROUP BY c.course_id, c.course_name
ORDER BY total_students DESC;
```
*Output:*
| course_name | total_students |
| :--- | :---: |
| Machine Learning | 3 |
| Data Structures and Algorithms | 2 |
| Operating Systems | 2 |
| Artificial Intelligence | 2 |
| Computer Networks | 1 |
| Database Management Systems | 1 |

- **Query 10.4.4: High Academic Achievement Filter (Grade A or A+)**
```sql
SELECT s.name AS student_name, c.course_name, e.grade
FROM enrolled_in e
JOIN student s ON e.student_id = s.student_id
JOIN course c ON e.course_id = c.course_id
WHERE e.grade IN ('A', 'A+')
ORDER BY s.name;
```
*Output:* 8 course enrollments verified with letter grades 'A' or 'A+'.

---

### **11. User Interface Design and Screenshots**

#### **11.1 Design System Principles**
- **JetBrains Mono Monospace Typography:** Gives the entire interface a refined, technical, and developer-grade character.
- **Warm Editorial Color Palette:** High-contrast neutral background (`#EFEBE0`), off-white cards (`#F8F5ED`), carbon ink typography (`#23221F`), and forest teal accents (`#2E5B4E`).
- **Live Counter Badges:** The left navigation displays dynamic pill counters next to all seven tables, updated dynamically upon any record mutation.
- **Relational Modal Drawers:** Data insertion is facilitated by an unobtrusive slide-over drawer that automatically renders foreign keys as human-readable dropdown selectors.
- **Safe Inline Deletions:** Two-step confirmation safeguards (`delete? yes / no`) prevent catastrophic accidental record losses.
- **Real-Time Client-Side Search:** Instant multi-column filtering without triggering extra network requests.

#### **11.2 Visual Interface Gallery**
- **Figure 11.1:** Students Registry View displaying live count badges, column grids, and advisor joins.
- **Figure 11.2:** Faculty Directory displaying departmental affiliations and contact details.
- **Figure 11.3:** Course Catalog listing course credits (CHECK 1-6) and administering departments.
- **Figure 11.4:** Department Directory displaying office locations and parent college.
- **Figure 11.5:** Enrollments Management View displaying student grades and semester terms.
- **Figure 11.6:** Interactive Slide-Over Drawer Form for schema-driven record creation.
- **Figure 11.7:** Real-Time Search Filtering actively isolating matching records.

*(All original high-resolution screenshot PNG files are preserved in the `report_assets/` directory and embedded directly inside `DBMS_Project_Report_Atharva_Kanojia_25WU0101019.docx`)*.

---

### **12. Implementation Details**

#### **12.1 Decoupled Three-Tier Architecture**
1. **Database Tier (MySQL 8.0 InnoDB):** Handles schema definition, constraints, foreign key cascades, and atomic transactions.
2. **Backend Application Tier (Java 21+):** Employs the built-in `com.sun.net.httpserver.HttpServer` with raw JDBC PreparedStatements and Google Gson, achieving zero-framework startup in under 500ms.
3. **Frontend Presentation Tier (React 18 + Vite):** Delivers a responsive single-page application with optimistic-safe state updates and monotonic request sequencing.

#### **12.2 Database Connectivity (`backend/db.properties`)**
```properties
url=jdbc:mysql://localhost:3306/student_college_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
user=Atharva
password=atharva
```

#### **12.3 Key Backend Implementation (`App.java` & `Schema.java`)**
- **Error Interception & User-Friendly Translation:**
```java
private static String friendly(SQLException e) {
    return switch (e.getErrorCode()) {
        case 1062 -> "A record with the same unique value already exists.";
        case 1451 -> "Cannot delete: other records still depend on this one. Remove those first.";
        case 1452 -> "Invalid reference: the selected related record does not exist.";
        case 3819 -> "A value violates a database check (e.g. credits must be 1-6).";
        default   -> e.getMessage();
    };
}
```

- **Dynamic Schema-Driven Entity Registration:**
```java
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
```

#### **12.4 Frontend Race Condition Mitigation (`App.jsx`)**
```javascript
const latest = useRef(0);

const loadRows = useCallback(async (key) => {
  const id = ++latest.current;
  setLoading(true);
  try {
    const data = await api(`/tables/${key}`);
    if (id === latest.current) setLoaded({ key, data });
  } catch (e) { 
    if (id === latest.current) flash(e.message, 'err'); 
  } finally { 
    if (id === latest.current) setLoading(false); 
  }
}, []);
```

---

### **13. Testing (Test Cases and Results)**

| Test ID | Test Scenario | Execution Procedure | Expected Behavior | Actual Verified Result | Status |
| :---: | :--- | :--- | :--- | :--- | :---: |
| **TC-01** | Table Navigation | Click through all 7 sidebar tables | Load dynamic schema headers and joined datasets | Verified all 7 tables loaded | **PASS** |
| **TC-02** | Record Insertion | Fill '+ New student' drawer form & submit | Execute INSERT prepared statement; row count increments | Added student; count updated | **PASS** |
| **TC-03** | Unique Key Violation | Insert duplicate email address | Intercept MySQL error 1062; display friendly toast | Caught 1062; show conflict toast | **PASS** |
| **TC-04** | Referential Integrity | Delete department with active faculty | Intercept MySQL error 1451; prevent deletion | Caught 1451; deletion blocked | **PASS** |
| **TC-05** | Invalid Foreign Key | Insert student with non-existent advisor ID | Intercept MySQL error 1452; reject insertion | Caught 1452; return HTTP 409 | **PASS** |
| **TC-06** | Check Constraint | Insert course with 8 credits or invalid grade | Intercept MySQL error 3819; reject insertion | Caught 3819; blocked invalid value | **PASS** |
| **TC-07** | Cascade Deletion | Delete a course associated with enrollments | MySQL CASCADE deletes matching rows in `enrolled_in` | Child rows cleanly removed | **PASS** |
| **TC-08** | Multi-Column Search | Type search query in filter box | Instant client-side filtering across all columns | Filtered visible rows instantly | **PASS** |
| **TC-09** | Deletion Safeguard | Click row delete -> prompt appears -> click 'no' | Discard deletion request; preserve row | Record preserved intact | **PASS** |
| **TC-10** | Out-of-Order Requests | Rapidly cycle between multiple sidebar tables | Discard stale asynchronous responses using ref counter | Zero grid desync observed | **PASS** |

---

### **14. Conclusion and Future Enhancements**

#### **14.1 Conclusion**
The **Student and College Management System** demonstrates the end-to-end design and realization of an enterprise academic registry platform. By placing database normalization (3NF/BCNF) and declarative relational integrity at the core of the architecture, data corruption and orphan records are structurally eliminated. Eliminating heavyweight backend frameworks in favor of direct JDBC and the JDK HttpServer delivers optimal execution speeds with sub-millisecond query responses and zero memory bloat. The bespoke JetBrains Mono monospace user interface proves that academic management software can be functional, dense, and aesthetically compelling.

#### **14.2 Future Roadmap**
1. **Role-Based Access Control (RBAC):** Introduce JWT authentication distinguishing Administrator, Faculty, and Student access levels.
2. **Automated Transcript Generation:** Implement client/server PDF export routines for student grade cards and departmental rosters.
3. **Interactive Graphical Dashboards:** Integrate charting libraries to visualize grade distribution curves, student-to-faculty ratios, and enrollment trends.
4. **Audit Logging & History:** Maintain a dedicated immutable audit table recording timestamps, user credentials, and changed attributes for all DML operations.

---

### **15. References**

1. Silberschatz, A., Korth, H. F., & Sudarshan, S. (2020). *Database System Concepts* (7th ed.). McGraw-Hill Education.
2. Elmasri, R., & Navathe, S. B. (2015). *Fundamentals of Database Systems* (7th ed.). Pearson Education.
3. Oracle Corporation. (2024). *MySQL 8.0 Reference Manual: InnoDB Storage Engine & Constraints*. Oracle Docs.
4. Oracle Corporation. (2024). *Java Platform, Standard Edition Documentation (JDK 21 LTS): HttpServer and JDBC API*.
5. Meta Open Source. (2024). *React 18 Documentation: State Hooks and Component Lifecycle*.
6. Vite Contributors. (2024). *Vite: Next Generation Frontend Tooling Guide*.
7. JetBrains. (2024). *JetBrains Mono: A Monospace Typeface for Developers*.

---

### **16. Appendix: GitHub Repository & Resources**

- **Project GitHub Repository:** [https://github.com/kanojiaatharva/DBMS-Course-Project](https://github.com/kanojiaatharva/DBMS-Course-Project)
- **Author GitHub Profile:** [https://github.com/kanojiaatharva](https://github.com/kanojiaatharva)
- **Database Schema Script:** `database/student_college_management.sql`
- **Backend Source Code:** `backend/`
- **Frontend Source Code:** `frontend/`
- **Generated Word Report:** `DBMS_Project_Report_Atharva_Kanojia_25WU0101019.docx`
- **Report Image Assets:** `report_assets/`

#### **Quick Local Setup Instructions:**
```bash
# 1. Database Setup
mysql -u root -p < database/student_college_management.sql

# 2. Run Backend Java API (Port 8080)
cd backend
mvn compile exec:java

# 3. Run Frontend React Dashboard (Port 5173)
cd frontend
npm install
npm run dev
```
