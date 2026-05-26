# Technical Documentation - Touristics Project

## Architecture Overview

The application follows a traditional layered architecture for desktop applications:
- **Presentation Layer**: Java Swing components (Windows/Frames) located in `MyApp`, `userWindows`, and various `US*` packages.
- **Service Layer**: Business logic handles complex operations like `HotelDeletionService`.
- **Data Access Layer**: Hibernate ORM handles persistence to MS SQL Server.
- **Domain Model**: POJOs annotated with Jakarta Persistence (JPA) in `hotels`, `occupancies`, and `users`.

## Data Model

### Hotel (`hotels.Hotel`)
Represents the master data of a tourist facility.
- Fields: `id`, `name`, `category`, `address`, `city`, `noRooms`, `noBeds`, etc.
- Relationships: One-to-Many with `Occupancy`.

### Occupancy (`occupancies.Occupancy`)
Represents monthly transactional data for a hotel.
- Composite Key: `hotel_id`, `year`, `month` (implemented via `OccupancyPK`).
- Metrics: `rooms`, `usedRooms`, `beds`, `usedBeds`.

### User (`users.User`)
System users with varying access levels.
- Fields: `username`, `password_hash` (BCrypt), `role`, `hotelID` (optional restriction), `canDelete`.

## Security and Permissions

Permissions are checked globally via the `MyApp.Session` state, which is populated upon successful login in `LoginWindow`.

### User Roles
1. **Senior**: Full access to hotel overview, management, and deletion (if `canDelete` is true).
2. **Senior_Admin**: Administrative access including user management.
3. **Hotel Representative**: Restricted access to data belonging to a specific `hotelID`.
4. **Head**: Broad overview access, typically read-only or high-level.

### Cascade Deletion Logic
Implemented in `HotelDeletionService.deleteHotelWithCascade(int hotelId)`:
1. **Unlink Users**: Sets `hotelID` to `null` for all users assigned to the hotel.
2. **Delete Transactions**: Removes all `Occupancy` records for the hotel.
3. **Delete Hotel**: Removes the `Hotel` record itself.
All steps are performed within a single Hibernate transaction to ensure atomicity.

## Key Modules

- **US11 (Delete Hotel)**: A specialized window that shows the "impact" (linked transactions/users) before confirming a deletion.
- **US16 (Data Import)**: Handles importing transactional data from external sources.
- **US3 (Add Hotel)**: Includes validation logic (`HotelValidator`) for new hotel entries.
- **User Management (us12)**: Interface for managing system users and their permissions.

## Database Integration

- **HibernateUtil**: Singleton-style provider for the `SessionFactory`.
- **Configuration**: Managed via `src/main/resources/hibernate.cfg.xml`.
- **Connection**: Uses `com.microsoft.sqlserver.jdbc.SQLServerDriver`.
