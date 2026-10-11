# Campus Connect

Campus Connect is a Java-based student and event management system designed for a college environment. It allows administrators to manage student records, create campus events, manage seat bookings, and generate simple operational reports.

## Project Overview
This project is built as a practical Java application to demonstrate modular software design, object-oriented programming, and data handling. The system includes three major functional modules:

1. Student management
2. Event management
3. Booking and analytics reporting

## Features
- Register students with department, email, and academic year
- Create campus events with venue, date, and capacity
- Book and cancel event seats
- Track booking history for each student
- Display summary reports for students and events
- Validate inputs and handle common user errors
- Persist students, events, bookings, cancellation status, and seat availability across restarts

## Problem Statement
Colleges often face difficulty in tracking student data and organizing events with limited seating. A centralized system is required to maintain records, reduce manual errors, and provide a structured workflow for event booking.

## Target Users
- College administration
- Event coordinators
- Student affairs staff
- Students who need event participation access

## Technologies Used
- Java 17
- Maven
- JUnit 5 for testing

## Project Structure

```text
CampusConnect/
├── docs/
│   └── system-design.md
├── src/
│   ├── main/java/com/vityarthi/campus/
│   │   ├── model/
│   │   ├── service/
│   │   └── ui/
│   └── test/java/com/vityarthi/campus/
├── README.md
├── statement.md
├── pom.xml
└── .gitignore
```

## Installation and Run

1. Open a terminal in the project root.
2. Run:

```bash
mvn clean install
```

3. Run the application:

```bash
mvn exec:java -Dexec.mainClass="com.vityarthi.campus.ui.CampusConsole"
```

If `exec-maven-plugin` is not configured, use:

```bash
mvn package
java -cp target/classes com.vityarthi.campus.ui.CampusConsole
```

## Testing

Run the unit tests with:

```bash
mvn test
```

Application data is stored in `campus-connect-data/` beside the application/project rather than under Maven's `target/` directory. The location is resolved from the application classes or JAR, so launching the JAR from another working directory uses the same saved records. Keep this directory to retain your local data; it is excluded from version control.

## Functional Modules
### 1. Student Module
- Add student details
- View student records
- Search by student ID

### 2. Event Module
- Create events
- View current event list
- Check seat availability

### 3. Reporting Module
- Show total registration count
- Show booked and available seats
- Generate event summaries

## Non-Functional Requirements
- Performance: quick response for student and event lookups
- Security: validation for invalid IDs and duplicate bookings
- Usability: menu-driven console interface
- Reliability: robust input validation and exception handling
- Maintainability: modular class structure and service-based design

## Screenshots
Screenshots can be added later when the project is connected to a GUI or web interface.

## Future Enhancements
- Add database persistence with MySQL
- Add login and admin roles
- Add QR-based event check-in
- Add graphical dashboard

## Author
Vityarthi / Java Course Project
