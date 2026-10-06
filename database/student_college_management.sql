-- ============================================================
-- Student and College Management System
-- MySQL Database Script
-- ============================================================
-- Designed for a small DBMS/ER-model demonstration.
-- MySQL 8.0+
--
-- Core relationships:
-- COLLEGE     1 : N DEPARTMENT
-- DEPARTMENT  1 : N FACULTY
-- DEPARTMENT  1 : N COURSE
-- FACULTY     1 : N STUDENT (ADVISES)
-- FACULTY     M : N COURSE (TEACHES)
-- STUDENT     M : N COURSE (ENROLLED_IN)
-- ============================================================

DROP DATABASE IF EXISTS student_college_management;
CREATE DATABASE student_college_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE student_college_management;

-- ============================================================
-- 1. COLLEGE
-- ============================================================
CREATE TABLE college (
    college_id INT PRIMARY KEY AUTO_INCREMENT,
    college_name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    email VARCHAR(100),
    phone VARCHAR(15),
    CONSTRAINT uq_college_email UNIQUE (email)
) ENGINE=InnoDB;

-- ============================================================
-- 2. DEPARTMENT
-- One college can have many departments.
-- ============================================================
CREATE TABLE department (
    dept_id INT PRIMARY KEY AUTO_INCREMENT,
    dept_name VARCHAR(100) NOT NULL,
    office VARCHAR(100),
    college_id INT NOT NULL,

    CONSTRAINT uq_department_college_name
        UNIQUE (college_id, dept_name),

    CONSTRAINT fk_department_college
        FOREIGN KEY (college_id)
        REFERENCES college(college_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 3. FACULTY
-- One department can have many faculty members.
-- ============================================================
CREATE TABLE faculty (
    faculty_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    dept_id INT NOT NULL,

    CONSTRAINT uq_faculty_email UNIQUE (email),

    CONSTRAINT fk_faculty_department
        FOREIGN KEY (dept_id)
        REFERENCES department(dept_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 4. COURSE
-- One department can offer many courses.
-- ============================================================
CREATE TABLE course (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(100) NOT NULL,
    credits TINYINT UNSIGNED NOT NULL,
    dept_id INT NOT NULL,

    CONSTRAINT chk_course_credits
        CHECK (credits BETWEEN 1 AND 6),

    CONSTRAINT uq_course_department_name
        UNIQUE (dept_id, course_name),

    CONSTRAINT fk_course_department
        FOREIGN KEY (dept_id)
        REFERENCES department(dept_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 5. STUDENT
-- A faculty member can advise many students.
-- ============================================================
CREATE TABLE student (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    advisor_id INT NULL,

    CONSTRAINT uq_student_email UNIQUE (email),

    CONSTRAINT fk_student_advisor
        FOREIGN KEY (advisor_id)
        REFERENCES faculty(faculty_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- 6. TEACHES
-- M : N relationship between FACULTY and COURSE.
-- Composite primary key prevents duplicate assignments.
-- ============================================================
CREATE TABLE teaches (
    faculty_id INT NOT NULL,
    course_id INT NOT NULL,

    PRIMARY KEY (faculty_id, course_id),

    CONSTRAINT fk_teaches_faculty
        FOREIGN KEY (faculty_id)
        REFERENCES faculty(faculty_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_teaches_course
        FOREIGN KEY (course_id)
        REFERENCES course(course_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 7. ENROLLED_IN
-- M : N relationship between STUDENT and COURSE.
-- semester and grade are attributes of the relationship.
-- ============================================================
CREATE TABLE enrolled_in (
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    semester VARCHAR(20) NOT NULL,
    grade CHAR(2),

    PRIMARY KEY (student_id, course_id, semester),

    CONSTRAINT fk_enrollment_student
        FOREIGN KEY (student_id)
        REFERENCES student(student_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_enrollment_course
        FOREIGN KEY (course_id)
        REFERENCES course(course_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT chk_enrollment_grade
        CHECK (
            grade IS NULL OR
            grade IN ('A+', 'A', 'B+', 'B', 'C+', 'C', 'D', 'F', 'I')
        )
) ENGINE=InnoDB;

-- ============================================================
-- INDEXING STRATEGY
-- Primary keys are indexed automatically.
-- These indexes support common joins/searches.
-- ============================================================

CREATE INDEX idx_department_college
    ON department(college_id);

CREATE INDEX idx_faculty_department
    ON faculty(dept_id);

CREATE INDEX idx_course_department
    ON course(dept_id);

CREATE INDEX idx_student_advisor
    ON student(advisor_id);

CREATE INDEX idx_teaches_course
    ON teaches(course_id);

CREATE INDEX idx_enrolled_in_course
    ON enrolled_in(course_id);

-- ============================================================
-- SAMPLE DATA
-- ============================================================

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

-- Faculty can teach multiple courses, and a course can be taught
-- by multiple faculty members.
INSERT INTO teaches (faculty_id, course_id) VALUES
(1, 1),
(1, 2),
(1, 5),
(3, 1),
(3, 6),
(2, 3),
(2, 4),
(4, 3),
(4, 4);

-- Students can enroll in multiple courses.
INSERT INTO enrolled_in (student_id, course_id, semester, grade) VALUES
(1, 1, '2026-S1', 'A'),
(1, 2, '2026-S1', 'A+'),
(1, 5, '2026-S1', 'B+'),
(2, 3, '2026-S1', 'A'),
(2, 4, '2026-S1', 'A+'),
(3, 1, '2026-S1', 'B+'),
(3, 6, '2026-S1', 'A'),
(4, 3, '2026-S1', 'A+'),
(4, 4, '2026-S1', 'A'),
(5, 2, '2026-S1', 'B'),
(5, 6, '2026-S1', 'B+'),
(6, 4, '2026-S1', 'A');

-- ============================================================
-- USEFUL DEMONSTRATION QUERIES
-- ============================================================

-- Q1. Show all students with their advisors.
SELECT
    s.student_id,
    s.name AS student_name,
    f.name AS advisor_name
FROM student s
LEFT JOIN faculty f
    ON s.advisor_id = f.faculty_id
ORDER BY s.student_id;

-- Q2. Show all courses offered by each department.
SELECT
    d.dept_name,
    c.course_id,
    c.course_name,
    c.credits
FROM department d
JOIN course c
    ON d.dept_id = c.dept_id
ORDER BY d.dept_name, c.course_id;

-- Q3. Show courses taught by each faculty member.
SELECT
    f.name AS faculty_name,
    c.course_name
FROM teaches t
JOIN faculty f
    ON t.faculty_id = f.faculty_id
JOIN course c
    ON t.course_id = c.course_id
ORDER BY f.name, c.course_name;

-- Q4. Show student enrollment details.
SELECT
    s.name AS student_name,
    c.course_name,
    e.semester,
    e.grade
FROM enrolled_in e
JOIN student s
    ON e.student_id = s.student_id
JOIN course c
    ON e.course_id = c.course_id
ORDER BY s.name, c.course_name;

-- Q5. Count students in each course.
SELECT
    c.course_name,
    COUNT(e.student_id) AS total_students
FROM course c
LEFT JOIN enrolled_in e
    ON c.course_id = e.course_id
GROUP BY c.course_id, c.course_name
ORDER BY total_students DESC;

-- Q6. Count courses offered by each department.
SELECT
    d.dept_name,
    COUNT(c.course_id) AS total_courses
FROM department d
LEFT JOIN course c
    ON d.dept_id = c.dept_id
GROUP BY d.dept_id, d.dept_name
ORDER BY total_courses DESC;

-- Q7. Find students who received grade A or A+.
SELECT
    s.name AS student_name,
    c.course_name,
    e.grade
FROM enrolled_in e
JOIN student s
    ON e.student_id = s.student_id
JOIN course c
    ON e.course_id = c.course_id
WHERE e.grade IN ('A', 'A+')
ORDER BY s.name;

-- Q8. Show all faculty members in the AI/ML department.
SELECT
    f.faculty_id,
    f.name,
    f.email,
    f.phone
FROM faculty f
JOIN department d
    ON f.dept_id = d.dept_id
WHERE d.dept_name = 'Artificial Intelligence and Machine Learning';

-- Q9. Find the number of courses taught by each faculty member.
SELECT
    f.name AS faculty_name,
    COUNT(t.course_id) AS courses_taught
FROM faculty f
LEFT JOIN teaches t
    ON f.faculty_id = t.faculty_id
GROUP BY f.faculty_id, f.name
ORDER BY courses_taught DESC;

-- Q10. Show a complete student academic report.
SELECT
    s.student_id,
    s.name AS student_name,
    f.name AS advisor_name,
    c.course_name,
    e.semester,
    e.grade
FROM student s
LEFT JOIN faculty f
    ON s.advisor_id = f.faculty_id
LEFT JOIN enrolled_in e
    ON s.student_id = e.student_id
LEFT JOIN course c
    ON e.course_id = c.course_id
ORDER BY s.student_id, e.semester, c.course_name;

-- ============================================================
-- OPTIONAL CRUD EXAMPLES
-- ============================================================

-- CREATE
-- INSERT INTO student (name, email, phone, advisor_id)
-- VALUES ('New Student', 'new.student@example.edu', '9000000099', 1);

-- READ
-- SELECT * FROM student;

-- UPDATE
-- UPDATE student
-- SET phone = '9111111111'
-- WHERE student_id = 1;

-- DELETE
-- DELETE FROM student
-- WHERE student_id = 6;

-- ============================================================
-- END OF DATABASE SCRIPT
-- ============================================================


SELECT * FROM student;
SELECT * FROM faculty;
SELECT * FROM department;
SELECT * FROM course;
SELECT * FROM teaches;
SELECT * FROM enrolled_in;

SELECT 
    s.name AS student,
    f.name AS advisor
FROM student s
JOIN faculty f 
    ON s.advisor_id = f.faculty_id;
    
    
SELECT 
    c.course_name,
    d.dept_name
FROM course c
JOIN department d
    ON c.dept_id = d.dept_id;    
WHERE d.dept_name  = "Artificial Intelligence and Machine Learning";    
    
SELECT 
    f.name AS faculty,
    c.course_name AS course
FROM teaches t
JOIN faculty f
    ON t.faculty_id = f.faculty_id
JOIN course c
    ON t.course_id = c.course_id;    