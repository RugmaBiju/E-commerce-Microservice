# E-commerce-Microservice
# Product Service

A Java-based **Product Management System** built using **Object-Oriented Programming (OOP)** concepts and **JDBC** for database connectivity.
This project demonstrates a simple layered architecture with clear separation between the user interface, business logic, and database access.

## Overview

The application allows users to manage products in an inventory or e-commerce style system.
Users can perform basic **CRUD operations** (Create, Read, Update, Delete) on products stored in a relational database.

The project is designed as an academic assignment to demonstrate the use of:

* Java OOP concepts
* JDBC database connectivity
* Layered software architecture
* Relational database design

## Features

### Product Management

* Add new product
* View all products
* Update product details
* Delete product
* Search product by ID or name

### User Management

* Register new user
* View user records
* Delete users
* Search users

### Database Operations

The system supports full **CRUD functionality**:

* Create records
* Read records
* Update records
* Delete records


## OOP Concepts Implemented

| Concept           | Implementation                                         |
| ----------------- | ------------------------------------------------------ |
| Classes & Objects | Product and User classes represent real-world entities |
| Encapsulation     | Private variables with getters and setters             |
| Inheritance       | Custom exception classes                               |
| Polymorphism      | DAO implementation classes                             |
| Abstraction       | DAO interfaces                                         |
| Collections       | ArrayList used to manage lists of data                 |


## System Architecture

The application follows a **layered architecture**:

```
Frontend (User Interface)
        ↓
Service Layer (Business Logic)
        ↓
DAO Layer (Database Operations)
        ↓
Database Layer
```

This separation improves code organization and maintainability.

---

## Project Structure

```
product-service
│
├── src
│   ├── frontend
│   │   └── MainMenu.java
│   │
│   ├── model
│   │   ├── Product.java
│   │   └── User.java
│   │
│   ├── service
│   │   ├── ProductService.java
│   │   └── UserService.java
│   │
│   ├── dao
│   │   ├── ProductDAO.java
│   │   ├── ProductDAOImpl.java
│   │   ├── UserDAO.java
│   │   └── UserDAOImpl.java
│   │
│   ├── util
│   │   └── DBConnection.java
│   │
│   └── exception
│       └── ProductNotFoundException.java
│
├── database
│   └── schema.sql
│
└── README.md
```

---

## Technologies Used

* **Java**
* **JDBC**
* **MySQL**
* **Visual Studio Code**
* **Git & GitHub**


## Database Schema

Example table used in the project:

### Products Table

```
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    price DOUBLE,
    quantity INT,
    category VARCHAR(50)
);
```

---

## Example Console Menu

```
===== Product Service =====

1. Register User
2. Add Product
3. View Products
4. Update Product
5. Delete Product
6. Search Product
7. Exit
```

---

## Learning Outcomes

Through this project the following skills are demonstrated:

* Implementation of **Object-Oriented Programming in Java**
* Using **JDBC for database connectivity**
* Designing **relational databases**
* Implementing **CRUD operations**
* Structuring applications using **layered architecture**

---

This project serves as a foundational example of building structured and maintainable **Java database-driven applications**.

