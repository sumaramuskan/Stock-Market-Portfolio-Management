# Stock-Market-Portfolio-Management

This project demonstrates a basic setup of a Java application connecting to a MySQL database using JDBC, and running on XAMPP as the local server.

## Prerequisites

Before setting up and running this project, make sure you have the following installed:

1. **Java JDK** (at least version 8)
2. **XAMPP** (with Apache and MySQL enabled)
3. **MySQL JDBC Driver** (`mysql-connector-java.jar`)
4. **IDE** (VSCode , Eclipse, IntelliJ IDEA, etc.)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/Stock-Market-Portfolio-Management.git
cd Stock-Market-Portfolio-Management
```

### 2. Install XAMPP and Start Services

- Download and install XAMPP from [https://www.apachefriends.org/download.html](https://www.apachefriends.org/download.html).
- Launch XAMPP and start **Apache** and **MySQL**.

### 3. Create the MySQL Database

1. Open **phpMyAdmin** via `http://localhost/phpmyadmin`.
2. Create a new database called `stockmarket,user,userportfolio`.
3. Import the SQL schema from `schema.sql` (if provided in the repo) or create the necessary tables manually in phpMyAdmin.

### 4. Configure Database Connection in the Project

1. Open the project in your IDE.
2. Locate the database configuration file (e.g., `DBConnection.java` or similar).
3. Set the following configuration to match your local XAMPP setup:

```java
String url = "jdbc:mysql://localhost:3306/your_database_name";
String username = "root";  // Default XAMPP MySQL username
String password = "";      // Default XAMPP MySQL password (leave empty)
```

4. Ensure you have added the MySQL JDBC driver (`mysql-connector-java.jar`) to your project’s classpath.

### 5. Running the Project

- Build and run the project using your IDE or the terminal.
- If the connection is successful, the application should interact with your MySQL database hosted on XAMPP.

## Notes

- The default XAMPP MySQL username is `root` and the password is empty. If you've changed the default settings, update the `DBConnection` configuration accordingly.
- Make sure your `mysql-connector-java.jar` is correctly added to your classpath.

## Troubleshooting

1. **Connection Refused**: Ensure that XAMPP's MySQL server is running.
2. **Incorrect Database Credentials**: Verify that the database username and password are correct.
3. **ClassNotFoundException for MySQL Driver**: Ensure that `mysql-connector-java.jar` is in your classpath.

---

You can customize this based on your specific project details.
