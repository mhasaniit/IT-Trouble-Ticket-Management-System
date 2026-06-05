# IT Trouble Ticket Management System

## Overview

This project is a Java-based IT Trouble Ticket Management System designed to simulate real-world help desk operations. The application allows users to create, manage, assign, and resolve support tickets while demonstrating core Object-Oriented Programming (OOP) principles and database integration.

A MySQL database was used to store and manage ticket information, providing persistent data storage and a more realistic enterprise-style ticketing workflow.

## Features

* Create and manage IT support tickets
* Assign tickets to technicians or users
* Track ticket status (Open, In Progress, Resolved)
* Update and resolve tickets through a structured workflow
* Store and retrieve ticket information using MySQL
* Console-based interaction for user input and system output

## OOP Concepts Applied

### Encapsulation

Organized data and functionality within classes while controlling access through methods.

### Inheritance

Extended base class functionality to promote code reuse and maintainability.

### Polymorphism

Implemented flexible method behavior to support different ticket management operations.

## Technologies Used

* Java
* MySQL
* JDBC (Java Database Connectivity)
* Eclipse IDE

## Database Integration

A MySQL database was implemented to manage ticket records and maintain persistent data storage. The database stores information such as:

* Ticket ID
* Issue Description
* Assigned Technician
* Status
* Resolution Details

JDBC was used to establish communication between the Java application and the MySQL database, enabling ticket creation, updates, retrieval, and resolution tracking.

## System Functionality

The system simulates a help desk environment where support tickets move through a complete lifecycle:

1. Ticket Creation
2. Assignment to a Technician
3. Status Updates During Processing
4. Resolution and Closure
5. Database Storage and Retrieval

## Testing & Debugging

* Tested application functionality using Eclipse IDE
* Verified database connectivity and CRUD operations
* Debugged logical and database-related issues
* Ensured accurate ticket status tracking and data persistence

## Project Structure

```text
src/
├── Java source files
├── Ticket classes
├── Database connection classes
└── Main application class
```

## How to Run

1. Import the project into Eclipse IDE.
2. Create and configure the MySQL database.
3. Update database connection settings if necessary.
4. Run the main application class.
5. Follow the console prompts to interact with the ticket management system.

## Purpose

The goal of this project was to apply Java Object-Oriented Programming concepts while integrating a relational database to create a functional IT support ticketing system. The project provided hands-on experience with software design, database management, JDBC connectivity, and real-world help desk workflows.

## Test Run Image
<img width="623" height="434" alt="Screenshot 2026-05-07 171653" src="https://github.com/user-attachments/assets/69fa093b-89ce-443f-85b1-fb1cf5e612cf" />
