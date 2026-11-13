# Student Management System (Spring Boot REST API)

A simple REST API to manage student records using Spring Boot, Spring Data JPA and MySQL.

## Tech Stack
- Java 17
- Spring Boot 3.2.5
- Spring Data JPA (Hibernate)
- MySQL 8
- Maven

## Setup
1. Install Java 17, Maven and MySQL.
2. Create the database:
   ```sql
   CREATE DATABASE student_db;
   ```
3. Open `src/main/resources/application.properties` and change the MySQL username / password if needed.
4. Run the project:
   ```bash
   mvn spring-boot:run
   ```
5. The API will start on `http://localhost:8080`.

## API Endpoints

| Method | Endpoint                          | Description              | Success Code |
|--------|-----------------------------------|--------------------------|--------------|
| POST   | /api/students                     | Add a new student        | 201 Created  |
| GET    | /api/students                     | Get all students         | 200 OK       |
| GET    | /api/students/{id}                | Get student by id        | 200 OK       |
| PUT    | /api/students/{id}                | Update student           | 200 OK       |
| DELETE | /api/students/{id}                | Delete student           | 200 OK       |
| GET    | /api/students/course/{course}     | Get students by course   | 200 OK       |

## Sample Request Body
```json
{
  "name": "Rahul Sharma",
  "email": "rahul.sharma@gmail.com",
  "course": "Computer Science",
  "age": 21
}
```

## Error Codes
- `404 Not Found` - student id does not exist
- `400 Bad Request` - validation failed (blank name, invalid email, etc.)
- `409 Conflict` - email already registered
