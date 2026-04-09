# Library Management System

A full-stack Library Management System built using Spring Boot and SQLite that simulates real-world library operations like managing books, members, borrowing, and fine tracking.

This project demonstrates how frontend, backend, and database interact through REST APIs in a real-world application.

---

## Features

- Book Management (Add, Update, Delete, Search)
-  Member Registration & Management
- Borrow & Return System
- Overdue Book Tracking
- Automatic Fine Calculation
- Dashboard with Live Statistics
- Search Functionality
- REST API Integration with UI

---

## Tech Stack

- Backend: Java, Spring Boot  
- Database: SQLite  
- ORM: Spring Data JPA (Hibernate)  
- Frontend: HTML, CSS, JavaScript  
- Build Tool: Maven  

---

## API Endpoints

### Books
- POST /api/books → Add book  
- GET /api/books → Get all books  
- GET /api/books/available → Available books  
- GET /api/books/search?q= → Search books  
- PUT /api/books/{id} → Update book  
- DELETE /api/books/{id} → Delete book  

### Members
- POST /api/members → Register member  
- GET /api/members → Get all members  
- PATCH /api/members/{id}/status → Update status  

### Borrow System
- POST /api/borrow → Borrow book  
- PUT /api/borrow/{id}/return → Return book  
- PUT /api/borrow/{id}/pay-fine → Pay fine  
- GET /api/borrow/overdue → Overdue records  
- GET /api/borrow/dashboard → Dashboard stats  

---

## How It Works

1. Backend APIs handle all business logic  
2. Frontend sends requests using fetch API  
3. Data stored in SQLite database  
4. Dashboard updates in real-time  

---

## Run Locally

### 1. Clone the repository
git clone https://github.com/sudhanshulodha265/library-management.git  
cd library-management  

### 2. Configure database
spring.datasource.url=jdbc:sqlite:library.db  

### 3. Build project
mvn clean package -DskipTests  

### 4. Run application
java -jar target/library-management-1.0.0.jar  

### 5. Open in browser
http://localhost:8080  

---

## 📸 Demo

Add screenshots or your LinkedIn demo video here  

---

## Key Highlights

- Built complete backend using Spring Boot  
- Designed REST APIs for real-world workflows  
- Integrated frontend with backend using fetch  
- Implemented business logic like fine calculation  

---

## Future Improvements

-  User Authentication (JWT)
-  Advanced Analytics Dashboard
-  Cloud Deployment (Render)
-  Responsive UI Design

---
