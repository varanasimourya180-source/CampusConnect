package com.vityarthi.campus.ui;

import com.vityarthi.campus.model.Booking;
import com.vityarthi.campus.model.Event;
import com.vityarthi.campus.model.Student;
import com.vityarthi.campus.service.AppDataStore;
import com.vityarthi.campus.service.EventService;
import com.vityarthi.campus.service.ReportService;
import com.vityarthi.campus.service.StudentService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class CampusConsole {
    private final Scanner scanner = new Scanner(System.in);
    private final AppDataStore appDataStore = new AppDataStore();
    private final StudentService studentService = new StudentService(appDataStore);
    private final EventService eventService = new EventService(appDataStore, studentService);
    private final ReportService reportService = new ReportService();

    public static void main(String[] args) {
        CampusConsole app = new CampusConsole();
        app.run();
    }

    public void run() {
        System.out.println("Welcome to Campus Connect");

        while (true) {
            System.out.println();
            System.out.println("=== Campus Connect Menu ===");
            System.out.println("1. Register student");
            System.out.println("2. Create event");
            System.out.println("3. List students");
            System.out.println("4. List events");
            System.out.println("5. Book seat");
            System.out.println("6. Cancel booking");
            System.out.println("7. View my bookings");
            System.out.println("8. Generate reports");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int option = readInt();
            switch (option) {
                case 1 -> registerStudent();
                case 2 -> createEvent();
                case 3 -> listStudents();
                case 4 -> listEvents();
                case 5 -> bookSeat();
                case 6 -> cancelBooking();
                case 7 -> viewBookings();
                case 8 -> showReports();
                case 0 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void registerStudent() {
        try {
            System.out.print("Enter name: ");
            String name = scanner.nextLine();
            System.out.print("Enter department: ");
            String department = scanner.nextLine();
            System.out.print("Enter email: ");
            String email = scanner.nextLine();
            System.out.print("Enter academic year: ");
            String year = scanner.nextLine();

            Student student = studentService.registerStudent(name, department, email, year);
            System.out.println("Student registered successfully: " + student.getStudentId());
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void createEvent() {
        try {
            System.out.print("Enter event title: ");
            String title = scanner.nextLine();
            System.out.print("Enter date (yyyy-MM-dd): ");
            String dateText = scanner.nextLine();
            LocalDate date = LocalDate.parse(dateText);
            System.out.print("Enter venue: ");
            String venue = scanner.nextLine();
            System.out.print("Enter capacity: ");
            int capacity = readInt();

            Event event = eventService.createEvent(title, date, venue, capacity);
            System.out.println("Event created successfully: " + event.getEventId());
        } catch (DateTimeParseException ex) {
            System.out.println("Error: Please enter date in yyyy-MM-dd format.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void listStudents() {
        List<Student> students = studentService.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students available.");
            return;
        }

        System.out.println("Student List:");
        for (Student student : students) {
            System.out.println(student);
        }
    }

    private void listEvents() {
        List<Event> events = eventService.getAllEvents();
        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }

        System.out.println("Event List:");
        for (Event event : events) {
            System.out.println(event);
        }
    }

    private void bookSeat() {
        try {
            System.out.print("Enter student ID: ");
            String studentId = scanner.nextLine();
            System.out.print("Enter event ID: ");
            String eventId = scanner.nextLine();
            Booking booking = eventService.bookSeat(studentId, eventId);
            System.out.println("Booking successful: " + booking.getBookingId());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void cancelBooking() {
        try {
            System.out.print("Enter booking ID to cancel: ");
            String bookingId = scanner.nextLine();
            eventService.cancelBooking(bookingId);
            System.out.println("Booking cancelled successfully.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void viewBookings() {
        System.out.print("Enter student ID: ");
        String studentId = scanner.nextLine();
        List<Booking> bookings = eventService.getBookingsForStudent(studentId);
        if (bookings.isEmpty()) {
            System.out.println("No bookings found for this student.");
            return;
        }

        for (Booking booking : bookings) {
            System.out.println(booking);
        }
    }

    private void showReports() {
        System.out.println(reportService.generateStudentReport(studentService.getAllStudents()));
        System.out.println(reportService.generateEventReport(eventService.getAllEvents(), eventService.getAllBookings()));
    }

    private int readInt() {
        String value = scanner.nextLine();
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}
