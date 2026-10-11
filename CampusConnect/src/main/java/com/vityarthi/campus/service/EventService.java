package com.vityarthi.campus.service;

import com.vityarthi.campus.model.Booking;
import com.vityarthi.campus.model.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EventService {
    private final AppDataStore appDataStore;
    private final Map<String, Event> events;
    private final List<Booking> bookings;
    private final StudentService studentService;

    public EventService(StudentService studentService) {
        this(new AppDataStore(), studentService);
    }

    public EventService(AppDataStore appDataStore, StudentService studentService) {
        this.appDataStore = appDataStore;
        this.studentService = studentService;
        this.events = new LinkedHashMap<>(appDataStore.loadEvents());
        this.bookings = new ArrayList<>(appDataStore.loadBookings());
    }

    public Event createEvent(String title, LocalDate date, String venue, int capacity) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Event title is required");
        }
        if (venue == null || venue.isBlank()) {
            throw new IllegalArgumentException("Venue is required");
        }
        if (date == null) {
            throw new IllegalArgumentException("Event date is required");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }

        String eventId = generateNextEventId();
        Event event = new Event(eventId, title.trim(), date, venue.trim(), capacity);
        events.put(eventId, event);
        save();
        return event;
    }

    public List<Event> getAllEvents() {
        return new ArrayList<>(events.values());
    }

    public Booking bookSeat(String studentId, String eventId) {
        if (!studentService.hasStudent(studentId)) {
            throw new IllegalArgumentException("Student not found");
        }

        Event event = events.get(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }

        if (event.getAvailableSeats() <= 0) {
            throw new IllegalStateException("No seats are available for this event");
        }

        boolean alreadyBooked = bookings.stream()
                .anyMatch(booking -> booking.getStudentId().equals(studentId)
                        && booking.getEventId().equals(eventId)
                        && "BOOKED".equalsIgnoreCase(booking.getStatus()));

        if (alreadyBooked) {
            throw new IllegalStateException("Student already has a confirmed booking for this event");
        }

        String bookingId = generateNextBookingId();
        Booking booking = new Booking(bookingId, studentId, eventId, LocalDateTime.now(), "BOOKED");
        bookings.add(booking);
        event.decrementAvailableSeats();
        save();
        return booking;
    }

    public void cancelBooking(String bookingId) {
        Booking booking = bookings.stream()
                .filter(item -> item.getBookingId().equals(bookingId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!"BOOKED".equalsIgnoreCase(booking.getStatus())) {
            return;
        }

        Event event = events.get(booking.getEventId());
        if (event != null) {
            event.incrementAvailableSeats();
        }
        booking.cancel();
        save();
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public List<Booking> getBookingsForStudent(String studentId) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getStudentId().equals(studentId)) {
                result.add(booking);
            }
        }
        return result;
    }

    public void load() {
        events.clear();
        bookings.clear();
        events.putAll(appDataStore.loadEvents());
        bookings.addAll(appDataStore.loadBookings());
    }

    public void save() {
        appDataStore.saveEvents(events);
        appDataStore.saveBookings(bookings);
    }

    public Map<String, Event> getEventMap() {
        return new LinkedHashMap<>(events);
    }

    public List<Booking> getBookingsSnapshot() {
        return new ArrayList<>(bookings);
    }

    private String generateNextEventId() {
        int nextNumber = events.size() + 1;
        String maximumKey = events.keySet().stream()
                .filter(key -> key != null && key.startsWith("EVT-"))
                .max((left, right) -> Integer.compare(parseEventSequence(left), parseEventSequence(right)))
                .orElse(null);
        if (maximumKey != null) {
            nextNumber = parseEventSequence(maximumKey) + 1;
        }
        return "EVT-" + String.format("%03d", nextNumber);
    }

    private int parseEventSequence(String eventId) {
        try {
            return Integer.parseInt(eventId.substring("EVT-".length()));
        } catch (NumberFormatException | IndexOutOfBoundsException exception) {
            return 0;
        }
    }

    private String generateNextBookingId() {
        int nextNumber = bookings.size() + 1;
        String maximumKey = bookings.stream()
                .map(Booking::getBookingId)
                .filter(id -> id != null && id.startsWith("BKG-"))
                .max((left, right) -> Integer.compare(parseBookingSequence(left), parseBookingSequence(right)))
                .orElse(null);
        if (maximumKey != null) {
            nextNumber = parseBookingSequence(maximumKey) + 1;
        }
        return "BKG-" + String.format("%03d", nextNumber);
    }

    private int parseBookingSequence(String bookingId) {
        try {
            return Integer.parseInt(bookingId.substring("BKG-".length()));
        } catch (NumberFormatException | IndexOutOfBoundsException exception) {
            return 0;
        }
    }
}
