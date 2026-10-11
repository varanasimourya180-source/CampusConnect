# Project Statement — CampusConnect

## Problem Statement
Colleges may track student details, campus events, seat capacity, and event participation in separate spreadsheets or manually. This can lead to duplicate records, inconsistent seat counts, difficult cancellation tracking, and slow reporting. CampusConnect brings these activities into one structured console-based Java workflow.

## Objectives
1. Register students and assign unique IDs.
2. Create and list campus events.
3. Book and cancel seats for registered students.
4. View booking records and event availability.
5. Generate student and event reports.
6. Demonstrate modular Java design, validation, testing, and Maven packaging.

## Scope
**Included:** student registration/listing; event creation/listing; seat booking/cancellation; booking lookup; reports; console UI; Maven/JUnit; local persistence support pending final manual restart verification.

**Out of scope:** web/mobile UI, single sign-on, payments, email/SMS, multi-user network deployment, enterprise database integration.

## Target Users
- Campus or student-affairs staff
- Event coordinators
- Students or demonstrators using the console workflow
- Academic evaluators

## High-Level Features
- Student management
- Event management
- Booking management
- Reports and seat availability
- Input validation and error messages
- Local data save/load support (verify after restart before claiming complete)

## Inputs and Outputs
| Input | Output |
|---|---|
| Student details | Generated student ID and confirmation |
| Event title/date/venue/capacity | Generated event ID and confirmation |
| Student ID + event ID | Booking ID or validation message |
| Booking ID | Cancellation result |
| Report selection | Student/event summary |
