package com.vityarthi.campus.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String eventId;
    private String title;
    private LocalDate date;
    private String venue;
    private int capacity;
    private int availableSeats;

    public Event(String eventId, String title, LocalDate date, String venue, int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
        this.eventId = eventId;
        this.title = title;
        this.date = date;
        this.venue = venue;
        this.capacity = capacity;
        this.availableSeats = capacity;
    }

    public String getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void decrementAvailableSeats() {
        if (availableSeats <= 0) {
            throw new IllegalStateException("No seats available for this event");
        }
        availableSeats--;
    }

    public void incrementAvailableSeats() {
        if (availableSeats >= capacity) {
            throw new IllegalStateException("Event is already full or seats are at maximum capacity");
        }
        availableSeats++;
    }

    public void setAvailableSeats(int availableSeats) {
        if (availableSeats < 0 || availableSeats > capacity) {
            throw new IllegalArgumentException("Available seat value is invalid");
        }
        this.availableSeats = availableSeats;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Event event = (Event) object;
        return Objects.equals(eventId, event.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", title='" + title + '\'' +
                ", date=" + date +
                ", venue='" + venue + '\'' +
                ", capacity=" + capacity +
                ", availableSeats=" + availableSeats +
                '}';
    }
}
