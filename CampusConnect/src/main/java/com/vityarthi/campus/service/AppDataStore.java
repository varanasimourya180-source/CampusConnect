package com.vityarthi.campus.service;

import com.vityarthi.campus.model.Booking;
import com.vityarthi.campus.model.Event;
import com.vityarthi.campus.model.Student;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AppDataStore {
    public static final String DATA_DIRECTORY_NAME = "campus-connect-data";

    private final Path dataDirectory;

    public AppDataStore() {
        this(resolveDefaultDataDirectory());
    }

    public AppDataStore(Path dataDirectory) {
        this.dataDirectory = dataDirectory.toAbsolutePath().normalize();
        ensureDirectoryExists();
        System.err.println("[persistence] Data directory: " + this.dataDirectory);
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public synchronized void saveStudents(Map<String, Student> students) {
        writeSerializableFile("students.ser", new LinkedHashMap<>(students));
        System.err.println("[persistence] Saved " + students.size() + " students to " + resolve("students.ser"));
    }

    public synchronized Map<String, Student> loadStudents() {
        Object object = readSerializableFile("students.ser");
        if (!(object instanceof Map<?, ?> map)) {
            return new LinkedHashMap<>();
        }

        Map<String, Student> students = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() instanceof String key && entry.getValue() instanceof Student student) {
                students.put(key, student);
            }
        }
        System.err.println("[persistence] Loaded " + students.size() + " students from " + resolve("students.ser"));
        return students;
    }

    public synchronized void saveEvents(Map<String, Event> events) {
        writeSerializableFile("events.ser", new LinkedHashMap<>(events));
        System.err.println("[persistence] Saved " + events.size() + " events to " + resolve("events.ser"));
    }

    public synchronized Map<String, Event> loadEvents() {
        Object object = readSerializableFile("events.ser");
        if (!(object instanceof Map<?, ?> map)) {
            return new LinkedHashMap<>();
        }

        Map<String, Event> events = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() instanceof String key && entry.getValue() instanceof Event event) {
                events.put(key, event);
            }
        }
        System.err.println("[persistence] Loaded " + events.size() + " events from " + resolve("events.ser"));
        return events;
    }

    public synchronized void saveBookings(List<Booking> bookings) {
        writeSerializableFile("bookings.ser", new ArrayList<>(bookings));
        System.err.println("[persistence] Saved " + bookings.size() + " bookings to " + resolve("bookings.ser"));
    }

    public synchronized List<Booking> loadBookings() {
        Object object = readSerializableFile("bookings.ser");
        if (!(object instanceof List<?> list)) {
            return new ArrayList<>();
        }

        List<Booking> bookings = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Booking booking) {
                bookings.add(booking);
            }
        }
        System.err.println("[persistence] Loaded " + bookings.size() + " bookings from " + resolve("bookings.ser"));
        return bookings;
    }

    private void ensureDirectoryExists() {
        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create persistent data directory " + dataDirectory, exception);
        }
    }

    private void writeSerializableFile(String fileName, Serializable object) {
        Path file = resolve(fileName);
        Path temporaryFile = null;
        try {
            Files.createDirectories(dataDirectory);
            temporaryFile = Files.createTempFile(dataDirectory, fileName, ".tmp");
            try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(temporaryFile))) {
                outputStream.writeObject(object);
                outputStream.flush();
            }
            try {
                Files.move(temporaryFile, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save persistent data to " + file, exception);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException exception) {
                    System.err.println("Warning: Unable to remove temporary data file " + temporaryFile + ": " + exception.getMessage());
                }
            }
        }
    }

    private Object readSerializableFile(String fileName) {
        Path file = resolve(fileName);
        if (!Files.exists(file)) {
            System.err.println("[persistence] No saved data file yet: " + file);
            return null;
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(file))) {
            return inputStream.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            throw new IllegalStateException("Unable to load persistent data from " + file
                    + "; the file was preserved for recovery", exception);
        }
    }

    private static Path resolveDefaultDataDirectory() {
        try {
            Path codeLocation = Paths.get(AppDataStore.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).toAbsolutePath().normalize();
            Path applicationDirectory = Files.isDirectory(codeLocation) ? codeLocation : codeLocation.getParent();
            if (applicationDirectory == null) {
                throw new IllegalStateException("Unable to determine the application directory from " + codeLocation);
            }

            Path parent = applicationDirectory.getParent();
            if (applicationDirectory.getFileName() != null
                    && "target".equalsIgnoreCase(applicationDirectory.getFileName().toString())) {
                applicationDirectory = parent;
            } else if (parent != null && parent.getFileName() != null
                    && "target".equalsIgnoreCase(parent.getFileName().toString())) {
                applicationDirectory = parent.getParent();
            }
            if (applicationDirectory == null) {
                throw new IllegalStateException("Unable to determine the project directory from " + codeLocation);
            }
            return applicationDirectory.resolve(DATA_DIRECTORY_NAME);
        } catch (URISyntaxException exception) {
            throw new IllegalStateException("Unable to resolve the application data directory", exception);
        }
    }

    public Path resolve(String fileName) {
        return dataDirectory.resolve(fileName);
    }
}
