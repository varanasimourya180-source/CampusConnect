package com.vityarthi.campus.service;

import com.vityarthi.campus.model.Booking;
import com.vityarthi.campus.model.Event;
import com.vityarthi.campus.model.Student;

import java.util.List;

public class ReportService {
    public String generateStudentReport(List<Student> students) {
        StringBuilder builder = new StringBuilder();
        builder.append("Student Report\n");
        builder.append("Total students: ").append(students.size()).append("\n");
        for (Student student : students) {
            builder.append("- ").append(student.getStudentId())
                    .append(" | ")
                    .append(student.getName())
                    .append(" | ")
                    .append(student.getDepartment())
                    .append(" | ")
                    .append(student.getYear())
                    .append("\n");
        }
        return builder.toString();
    }

    public String generateEventReport(List<Event> events, List<Booking> bookings) {
        StringBuilder builder = new StringBuilder();
        builder.append("Event Report\n");
        builder.append("Total events: ").append(events.size()).append("\n");
        builder.append("Total bookings: ").append(bookings.size()).append("\n");

        for (Event event : events) {
            int bookedCount = (int) bookings.stream()
                    .filter(booking -> booking.getEventId().equals(event.getEventId()) && "BOOKED".equalsIgnoreCase(booking.getStatus()))
                    .count();

            builder.append("- ")
                    .append(event.getEventId())
                    .append(" | ")
                    .append(event.getTitle())
                    .append(" | Seats left: ")
                    .append(event.getAvailableSeats())
                    .append(" | Booked: ")
                    .append(bookedCount)
                    .append("\n");
        }
        return builder.toString();
    }
}
