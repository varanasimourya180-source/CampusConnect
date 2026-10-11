package com.vityarthi.campus.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String bookingId;
    private final String studentId;
    private final String eventId;
    private final LocalDateTime bookedAt;
    private String status;

    public Booking(String bookingId, String studentId, String eventId, LocalDateTime bookedAt, String status) {
        this.bookingId = bookingId;
        this.studentId = studentId;
        this.eventId = eventId;
        this.bookedAt = bookedAt;
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getEventId() {
        return eventId;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void cancel() {
        this.status = "CANCELLED";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Booking booking = (Booking) object;
        return Objects.equals(bookingId, booking.bookingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", eventId='" + eventId + '\'' +
                ", bookedAt=" + bookedAt +
                ", status='" + status + '\'' +
                '}';
    }
}
