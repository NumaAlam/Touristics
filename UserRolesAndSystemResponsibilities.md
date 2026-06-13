# User Roles and System Responsibilities - TouristOffice

This document outlines the various user roles and their associated permissions within the **TouristOffice** management system, as well as the core responsibilities of the system itself.

---

## 1. User Roles and Permissions

The system uses a Role-Based Access Control (RBAC) model to ensure that users can only access features relevant to their tasks.

### 1.1 Senior
The **Senior** role is a high-level administrative role with broad access to the system's management features.
*   **Hotel Management:** Full access to view, add, edit, and delete hotel master data.
*   **Occupancy Data:** View and manage transactional occupancy data (room/bed occupancy).
*   **Data Operations:** Import transaction data from external sources and view transaction lists per hotel.
*   **System Tools:** Access to help documentation and the ability to create system backups.
*   **User Management:** Basic access to user management features.

### 1.2 Senior Admin
The **Senior Admin** role inherits all permissions from the Senior role but focuses more on system-wide configuration and maintenance.
*   **Extended Permissions:** Often has the `canDelete` flag set to true by default, allowing for the deletion of critical records.
*   **Infrastructure:** Oversees database integrity and global system settings.

### 1.3 Hotel Representative
The **Hotel Representative** is a restricted role assigned to a specific hotel.
*   **Scoped Access:** Can only view and edit data for their assigned hotel (identified by `hotelID`).
*   **Data Entry:** Responsible for adding and viewing transaction data (occupancy) for their specific hotel.
*   **Restricted UI:** Accesses a specialized portal that hides management features for other hotels.

### 1.4 Head / Management
The **Head** role is primarily for reporting and high-level oversight.
*   **Read-Only Access:** Generally focuses on viewing master data and summaries without the permission to modify core infrastructure unless explicitly granted.
*   **Reporting:** Access to data tables for analysis.

### 1.5 Junior (Optional/Internal)
The **Junior** role is a restricted entry-level role, typically with limited write access or limited to viewing specific modules.

---

## 2. Permissions Matrix

| Feature | Senior | Senior Admin | Hotel Representative | Head |
| :--- | :---: | :---: | :---: | :---: |
| View Hotel Overview | Yes | Yes | Own Hotel Only | Yes |
| Add/Edit Hotels | Yes | Yes | No | No |
| Delete Hotels | Yes* | Yes | No | No |
| View Transaction Data | Yes | Yes | Own Hotel Only | Yes |
| Add Transaction Data | Yes | Yes | Own Hotel Only | No |
| Import Data | Yes | Yes | No | No |
| Create Backups | Yes | Yes | No | No |
| User Management | Yes | Yes | No | No |

*\*Subject to the `canDelete` flag in the user profile.*

---

## 3. System Responsibilities

The **TouristOffice** system is responsible for several core automated processes and security measures.

### 3.1 Authentication and Security
*   **Password Hashing:** The system uses `BCrypt` to hash passwords before storage, ensuring that plain-text passwords are never stored in the database.
*   **Session Management:** A global `Session` class tracks the currently logged-in user's role and assigned hotel ID to enforce access control across different windows.

### 3.2 Data Management (ORM)
*   **Object-Relational Mapping:** Hibernate handles the mapping between Java entities (`Hotel`, `User`, `Occupancy`) and the MS SQL Server database.
*   **Schema Evolution:** The system is configured to automatically update the database schema based on the entity definitions (`hbm2ddl.auto=update`).
*   **Cascade Deletion:** The system ensures data integrity by safely deleting linked transactions and user assignments when a hotel is removed.

### 3.3 ETL Processes
*   **Data Import:** The system processes external data files (e.g., `hotels.txt`, `occupancies.txt`) and transforms them into database records.
*   **Validation:** Input data is validated against business rules (e.g., `HotelValidator`) before being persisted.

### 3.4 User Interface
*   **Dynamic UI:** The GUI adapts based on the user's role, showing or hiding buttons and menus to prevent unauthorized access.
*   **Concurrency:** Uses `SwingWorker` for long-running tasks like backups to keep the user interface responsive.
