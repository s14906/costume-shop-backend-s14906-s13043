# Costume Shop Backend

This project is the backend for an online costume shop, done as part of my engineer's thesis at PJATK university. It is built using Spring Boot coupled with Angular frontend and MySQL database. It provides RESTful APIs to manage costumes, orders, users, and other related functionalities.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
- [Usage](#usage)

## Features

- CRUD operations for managing costumes, orders, and users
- Authentication and authorization using JWT (JSON Web Tokens)
- Integration with a MySQL database
- Sending email notifications regarding user actions and processes within the application
- Security measures such as input validation and password encryption
- Error handling and logging

## Technologies Used

- Spring Boot
- Spring Security
- Spring Boot Mail
- Hibernate
- JWT (JSON Web Tokens)
- MySQL
- Maven for dependency management
- Lombok
- Jackson Databind (for ORM mapping)
- Git

## Getting Started

### Prerequisites

- JDK (Java Development Kit) 11 or later
- Maven 
- MySQL

### Installation

1. Clone the repository:

```bash
git clone https://github.com/s14906/costume-shop-backend-s14906-s13043.git
```

2. Navigate to the project directory:

```bash 
cd costume-shop-backend-s14906-s13043
```

3. Build the project:
```bash
mvn clean install
```
# Usage
1. Configure your database connection properties in application.properties.

2. Run the application:

```bash
mvn spring-boot:run
```
3. Once the application is running, you can access the API endpoints using tools like Postman or by integrating with the dedicated frontend application.