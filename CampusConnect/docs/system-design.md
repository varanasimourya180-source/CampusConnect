# System Design

## 1. Architecture Overview
The application follows a layered architecture:

- Model layer: contains Student, Event, and Booking entities
- Service layer: contains business logic for registration, booking, and reporting
- UI layer: provides the console-based menu for interaction

## 2. Functional Requirements
- Register students
- Add events
- View list of events and students
- Book event seats
- Cancel bookings
- Generate reports

## 3. Non-Functional Requirements
- Performance: operations should complete quickly on small to medium data sets
- Security: invalid and duplicate entries should be rejected
- Usability: clear menu-driven interface
- Reliability: proper exceptions for invalid state changes
- Maintainability: separate classes for model, service, and user interface

## 4. Workflow Diagram

```text
Start
  |
  v
Display Main Menu
  |
  +--> Add Student
  +--> Add Event
  +--> List Students
  +--> List Events
  +--> Book Seat
  +--> Show Reports
  +--> Exit
```

## 5. Use Case Diagram

```text
+-------------------+
|     Admin/User    |
+-------------------+
          |
          v
+--------------------------+
| Campus Connect System    |
|--------------------------|
| Register Student          |
| Create Event             |
| View Student Records     |
| Book Event Seat          |
| Cancel Booking           |
| Generate Reports         |
+--------------------------+
```

## 6. Class Diagram

```text
+----------------+       +----------------+       +----------------+
| Student        |       | Event          |       | Booking        |
+----------------+       +----------------+       +----------------+
| - studentId    |       | - eventId      |       | - bookingId    |
| - name         |       | - title        |       | - studentId    |
| - department   |       | - date         |       | - eventId      |
| - email        |       | - venue        |       | - bookedAt     |
| - year         |       | - capacity     |       | - status       |
+----------------+       | - available    |       +----------------+
          |                    +----------------+
          |                              |
          +------------------------------+
                       |
                    +----------------------+
                    | StudentService        |
                    +----------------------+
                    | registerStudent()     |
                    | getStudents()         |
                    +----------------------+

                    +----------------------+
                    | EventService         |
                    +----------------------+
                    | createEvent()         |
                    | bookSeat()            |
                    | cancelBooking()      |
                    +----------------------+
```

## 7. Sequence Diagram

```text
Admin -> CampusConsole : Select Book Seat
CampusConsole -> EventService : bookSeat(studentId, eventId)
EventService -> StudentService : validate student exists
StudentService --> EventService : valid student
EventService -> Event : decrement availableSeats
EventService -> Booking : create booking record
EventService --> CampusConsole : booking confirmation
CampusConsole --> Admin : display result
```

## 8. ER Diagram

```text
Student
------
studentId (PK)
name
department
email
year

Event
-----
eventId (PK)
title
date
venue
capacity
availableSeats

Booking
-------
bookingId (PK)
studentId (FK)
eventId (FK)
bookedAt
status
```

## 9. Design Decisions
- Use a menu-driven console approach to keep the project easy to run and evaluate
- Separate services from model classes to maintain clear responsibilities
- Use Java collections for fast in-memory data handling
- Keep validation logic centralized to reduce repeated code

## 10. Future Database Extension
The system can later be upgraded to use MySQL or SQLite for persistent storage with the same model and service design.
