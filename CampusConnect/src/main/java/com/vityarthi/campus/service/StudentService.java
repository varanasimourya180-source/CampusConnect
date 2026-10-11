package com.vityarthi.campus.service;

import com.vityarthi.campus.model.Student;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StudentService {
    private final AppDataStore appDataStore;
    private final Map<String, Student> students;

    public StudentService() {
        this(new AppDataStore());
    }

    public StudentService(AppDataStore appDataStore) {
        this.appDataStore = appDataStore;
        this.students = new LinkedHashMap<>(appDataStore.loadStudents());
    }

    public Student registerStudent(String name, String department, String email, String year) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Student name is required");
        }
        if (department == null || department.isBlank()) {
            throw new IllegalArgumentException("Department is required");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("A valid email is required");
        }
        if (year == null || year.isBlank()) {
            throw new IllegalArgumentException("Academic year is required");
        }

        String studentId = generateNextStudentId();
        Student student = new Student(studentId, name.trim(), department.trim(), email.trim(), year.trim());
        students.put(studentId, student);
        save();
        return student;
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    public Optional<Student> findStudentById(String studentId) {
        return Optional.ofNullable(students.get(studentId));
    }

    public boolean hasStudent(String studentId) {
        return students.containsKey(studentId);
    }

    public void load() {
        students.clear();
        students.putAll(appDataStore.loadStudents());
    }

    public void save() {
        appDataStore.saveStudents(students);
    }

    public Map<String, Student> getStudentMap() {
        return new LinkedHashMap<>(students);
    }

    private String generateNextStudentId() {
        int nextNumber = students.size() + 1;
        String maximumKey = students.keySet().stream()
                .filter(key -> key != null && key.startsWith("STU-"))
                .max((left, right) -> Integer.compare(parseStudentSequence(left), parseStudentSequence(right)))
                .orElse(null);
        if (maximumKey != null) {
            nextNumber = parseStudentSequence(maximumKey) + 1;
        }
        return "STU-" + String.format("%03d", nextNumber);
    }

    private int parseStudentSequence(String studentId) {
        try {
            return Integer.parseInt(studentId.substring("STU-".length()));
        } catch (NumberFormatException | IndexOutOfBoundsException exception) {
            return 0;
        }
    }
}
