# Touristics Management System

A Java Swing application for managing hotel master data and transactional occupancy data.

## Features

- **Hotel Management**: Create, update, and delete hotel records.
- **Occupancy Tracking**: Manage room and bed occupancy data by year and month.
- **Role-Based Access Control**: Different permissions for Senior, Senior Admin, and Hotel Representatives.
- **Data Import/Export**: Support for importing transaction data and exporting reports.
- **Cascade Deletion**: Safely delete hotels along with their linked transactions and user assignments.

## Tech Stack

- **Language**: Java 25
- **UI Framework**: Java Swing
- **ORM**: Hibernate 6.6
- **Database**: Microsoft SQL Server (via MSSQL JDBC)
- **Utilities**: Lombok, BCrypt for password hashing, Apache POI for Excel/OOXML.
- **Build System**: Maven

## Getting Started

### Prerequisites

- Java 25 or higher
- Maven
- Microsoft SQL Server instance

### Configuration

1. Update database connection settings in `src/main/resources/hibernate.cfg.xml`.
2. Ensure the database schema matches the entities in the `hotels`, `occupancies`, and `users` packages.

### Running the Application

Execute the main class:
```bash
mvn exec:java -Dexec.mainClass="MyApp.AppStart"
```

## Project Structure

- `MyApp`: Core application logic, login, and main menu.
- `hotels`: Hotel entity and related utilities.
- `occupancies`: Transactional occupancy data models.
- `users`: User entity and security.
- `US*`: Feature-specific modules (User Stories).
- `database`: Hibernate configuration and tests.
- `userWindows`: Specialized dashboards for different user roles.
