package com.vityarthi.campus;

import com.vityarthi.campus.model.Booking;
import com.vityarthi.campus.model.Event;
import com.vityarthi.campus.model.Student;
import com.vityarthi.campus.service.AppDataStore;
import com.vityarthi.campus.service.EventService;
import com.vityarthi.campus.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StudentServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void registerStudentShouldCreateStudentWithGeneratedId() {
        AppDataStore appDataStore = new AppDataStore(tempDir);
        StudentService studentService = new StudentService(appDataStore);

        Student student = studentService.registerStudent("Ayesha Khan", "IT", "ayesha@example.com", "2027");

        assertNotNull(student);
        assertEquals("STU-001", student.getStudentId());
        assertEquals("Ayesha Khan", student.getName());
    }

    @Test
    void eventBookingShouldReduceSeatCountAndReload() {
        AppDataStore appDataStore = new AppDataStore(tempDir);
        StudentService studentService = new StudentService(appDataStore);
        EventService eventService = new EventService(appDataStore, studentService);

        Student student = studentService.registerStudent("Ayesha Khan", "IT", "ayesha@example.com", "2027");
        Event event = eventService.createEvent("AI Workshop", LocalDate.now().plusDays(4), "Lab 1", 10);

        Booking booking = eventService.bookSeat(student.getStudentId(), event.getEventId());

        assertEquals(9, event.getAvailableSeats());
        assertEquals("BOOKED", booking.getStatus());
        assertTrue(Files.exists(tempDir.resolve("students.ser")));
        assertTrue(Files.exists(tempDir.resolve("events.ser")));
        assertTrue(Files.exists(tempDir.resolve("bookings.ser")));

        AppDataStore restartedStore = new AppDataStore(tempDir);
        StudentService reloadedStudents = new StudentService(restartedStore);
        EventService reloadedEvents = new EventService(restartedStore, reloadedStudents);

        assertEquals(1, reloadedStudents.getAllStudents().size());
        assertEquals(1, reloadedEvents.getAllEvents().size());
        assertEquals(1, reloadedEvents.getAllBookings().size());
        assertEquals(9, reloadedEvents.getAllEvents().get(0).getAvailableSeats());
    }

    @Test
    void cancellationAndSeatAvailabilityPersistAcrossReload() {
        AppDataStore appDataStore = new AppDataStore(tempDir);
        StudentService studentService = new StudentService(appDataStore);
        EventService eventService = new EventService(appDataStore, studentService);

        Student student = studentService.registerStudent("Rahul Sharma", "ECE", "rahul@example.com", "2025");
        Event event = eventService.createEvent("Campus Expo", LocalDate.now().plusDays(6), "Main Hall", 2);

        Booking booking = eventService.bookSeat(student.getStudentId(), event.getEventId());
        eventService.cancelBooking(booking.getBookingId());

        assertEquals("CANCELLED", booking.getStatus());
        assertEquals(2, event.getAvailableSeats());

        AppDataStore restartedStore = new AppDataStore(tempDir);
        StudentService reloadedStudents = new StudentService(restartedStore);
        EventService reloadedEvents = new EventService(restartedStore, reloadedStudents);
        Event reloadedEvent = reloadedEvents.getAllEvents().get(0);
        Booking reloadedBooking = reloadedEvents.getAllBookings().get(0);

        assertEquals(1, reloadedStudents.getAllStudents().size());
        assertEquals(2, reloadedEvent.getAvailableSeats());
        assertEquals("CANCELLED", reloadedBooking.getStatus());
        assertTrue(reloadedBooking.getBookingId().startsWith("BKG-"));
    }
}
