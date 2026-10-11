package com.vityarthi.campus;

import com.vityarthi.campus.service.AppDataStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenceRestartIntegrationTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void freshApplicationProcessesPersistDataUsingDefaultApplicationDirectory() throws Exception {
        Path projectDirectory = temporaryDirectory.resolve("CampusConnect");
        Path classesDirectory = projectDirectory.resolve("target").resolve("classes");
        Path launchDirectory = temporaryDirectory.resolve("different-working-directory");
        Files.createDirectories(launchDirectory);
        copyApplicationClasses(classesDirectory);

        Path expectedDataDirectory = projectDirectory.resolve(AppDataStore.DATA_DIRECTORY_NAME);
        String registrationOutput = runApplication(classesDirectory, launchDirectory, String.join(System.lineSeparator(),
                "1",
                "Restart Integration Student",
                "CSE",
                "restart.integration@example.com",
                "2027",
                "2",
                "Restart Integration Event",
                "2026-10-20",
                "Integration Hall",
                "4",
                "0",
                ""));

        assertTrue(registrationOutput.contains(AppDataStore.DATA_DIRECTORY_NAME), registrationOutput);
        assertTrue(registrationOutput.contains("Saved 1 students to "), registrationOutput);
        assertTrue(registrationOutput.contains("Saved 1 events to "), registrationOutput);
        assertTrue(Files.exists(expectedDataDirectory.resolve("students.ser")));
        assertTrue(Files.exists(expectedDataDirectory.resolve("events.ser")));

        String restartOutput = runApplication(classesDirectory, launchDirectory,
                String.join(System.lineSeparator(), "3", "4", "0", ""));

        assertTrue(restartOutput.contains("Loaded 1 students from "), restartOutput);
        assertTrue(restartOutput.contains("Loaded 1 events from "), restartOutput);
        assertTrue(restartOutput.indexOf("Loaded 1 students from ")
                < restartOutput.indexOf("Welcome to Campus Connect"), restartOutput);
        assertTrue(restartOutput.contains("Restart Integration Student"), restartOutput);
        assertTrue(restartOutput.contains("Restart Integration Event"), restartOutput);
    }

    private void copyApplicationClasses(Path destination) throws Exception {
        Path source = Paths.get(AppDataStore.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI()).toAbsolutePath().normalize();
        try (Stream<Path> paths = Files.walk(source)) {
            for (Path sourcePath : paths.toList()) {
                Path destinationPath = destination.resolve(source.relativize(sourcePath));
                if (Files.isDirectory(sourcePath)) {
                    Files.createDirectories(destinationPath);
                } else {
                    Files.createDirectories(destinationPath.getParent());
                    Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private String runApplication(Path classesDirectory, Path workingDirectory, String input) throws Exception {
        String executableName = System.getProperty("os.name").toLowerCase().contains("win")
                ? "java.exe"
                : "java";
        Path javaExecutable = Path.of(System.getProperty("java.home"), "bin", executableName);
        Process process = new ProcessBuilder(
                javaExecutable.toString(),
                "-cp",
                classesDirectory.toString(),
                "com.vityarthi.campus.ui.CampusConsole")
                .directory(workingDirectory.toFile())
                .redirectErrorStream(true)
                .start();

        try (var output = process.getOutputStream()) {
            output.write(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
        String processOutput = new String(process.getInputStream().readAllBytes(),
                java.nio.charset.StandardCharsets.UTF_8);
        assertEquals(0, process.waitFor(), processOutput);
        return processOutput;
    }
}
