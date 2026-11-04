package demo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.util.*;

/** Holds validated records; a failed save leaves the previous list unchanged. */
public final class AppointmentBook {
    private final Path file;
    private byte[] savedBytes;
    private List<Appointment> entries = new ArrayList<>();

    public AppointmentBook(Path file) throws IOException {
        this.file = file.toAbsolutePath();
        if (Files.exists(this.file)) {
            savedBytes = Files.readAllBytes(this.file);
            List<String> lines = new String(savedBytes, StandardCharsets.UTF_8).lines().toList();
            if (lines.isEmpty() || !lines.get(0).equals("SCHEDULER-3")) {
                throw new IOException("Unrecognised appointment file: " + this.file);
            }
            try {
                for (int i = 1; i < lines.size(); i++) {
                    String[] row = lines.get(i).split("\t", -1);
                    if (row.length != 5) { throw new IllegalArgumentException("Invalid row"); }
                    entries.add(new Appointment(decode(row[0]), LocalDate.parse(row[1]),
                            LocalTime.parse(row[2]), LocalTime.parse(row[3]), decode(row[4])));
                }
            } catch (RuntimeException ex) {
                throw new IOException("Appointment file contains invalid data: " + this.file, ex);
            }
        }
    }

    public List<Appointment> getAll() { return List.copyOf(entries); }

    public void add(Appointment appointment) throws IOException {
        Objects.requireNonNull(appointment);
        List<Appointment> next = new ArrayList<>(entries);
        next.add(appointment);
        save(next);
    }

    public void update(int index, Appointment appointment) throws IOException {
        Objects.requireNonNull(appointment);
        List<Appointment> next = new ArrayList<>(entries);
        next.set(index, appointment);
        save(next);
    }

    public void delete(int index) throws IOException {
        List<Appointment> next = new ArrayList<>(entries);
        next.remove(index);
        save(next);
    }

    /** Week views always run Monday through Sunday, including across years. */
    public List<Appointment> view(LocalDate anchor, String period) {
        LocalDate from = switch (period) {
            case "Week" -> anchor.with(DayOfWeek.MONDAY);
            case "Month" -> anchor.withDayOfMonth(1);
            default -> anchor;
        };
        LocalDate to = switch (period) {
            case "Week" -> from.plusDays(6);
            case "Month" -> from.withDayOfMonth(from.lengthOfMonth());
            default -> from;
        };
        return entries.stream().filter(a -> !a.getDate().isBefore(from)
                && !a.getDate().isAfter(to))
                .sorted(Comparator.comparing(Appointment::getDate)
                        .thenComparing(Appointment::getStart)).toList();
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    /** Replace the data file only after the complete new file has been written. */
    private void save(List<Appointment> next) throws IOException {
        byte[] current = Files.exists(file) ? Files.readAllBytes(file) : null;
        if (!Arrays.equals(savedBytes, current)) {
            throw new IOException("Data changed in another window. Close and reopen this window.");
        }
        Path temporary = Files.createTempFile(file.getParent(), "appointments-", ".tmp");
        try {
            List<String> lines = new ArrayList<>();
            lines.add("SCHEDULER-3");
            for (Appointment a : next) {
                lines.add(String.join("\t", encode(a.getTitle()), a.getDate().toString(),
                        a.getStart().toString(), a.getEnd().toString(), encode(a.getLocation())));
            }
            Files.write(temporary, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
            entries = next;
            savedBytes = Files.readAllBytes(file);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
