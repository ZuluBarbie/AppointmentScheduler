package demo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

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
            if (lines.isEmpty() || !Set.of("SCHEDULER-3", "SCHEDULER-4").contains(lines.get(0))) {
                throw new IOException("Unrecognised appointment file: " + this.file);
            }
            try {
                boolean legacy = lines.get(0).equals("SCHEDULER-3");
                for (int i = 1; i < lines.size(); i++) {
                    String[] row = lines.get(i).split("\t", -1);
                    if (row.length != (legacy ? 5 : 8)) throw new IllegalArgumentException("Invalid row");
                    entries.add(new Appointment(decode(row[0]), LocalDate.parse(row[1]),
                            LocalTime.parse(row[2]), LocalTime.parse(row[3]), decode(row[4]),
                            legacy ? Appointment.Repeat.NONE : Appointment.Repeat.valueOf(row[5]),
                            legacy ? null : LocalDate.parse(row[6]), legacy ? 0 : Integer.parseInt(row[7])));
                }
            } catch (RuntimeException ex) {
                throw new IOException("Appointment file contains invalid data: " + this.file, ex);
            }
        }
    }

    public List<Appointment> getAll() { return List.copyOf(entries); }

    public void add(Appointment appointment) throws IOException {
        Objects.requireNonNull(appointment);
        checkConflicts(appointment, -1);
        List<Appointment> next = new ArrayList<>(entries);
        next.add(appointment);
        save(next);
    }

    public void update(int index, Appointment appointment) throws IOException {
        Objects.requireNonNull(appointment);
        List<Appointment> next = new ArrayList<>(entries);
        next.set(index, appointment);
        checkConflicts(appointment, index);
        save(next);
    }

    public void delete(int index) throws IOException {
        List<Appointment> next = new ArrayList<>(entries);
        next.remove(index);
        save(next);
    }

    /** Week views always run Monday through Sunday, including across years. */
    public List<Occurrence> view(LocalDate anchor, String period) {
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
        List<Occurrence> matches = new ArrayList<>();
        for (Appointment appointment : entries) {
            dates(from, to).filter(appointment::occursOn)
                    .forEach(date -> matches.add(new Occurrence(appointment, date)));
        }
        matches.sort(Comparator.comparing(Occurrence::start));
        return matches;
    }

    private void checkConflicts(Appointment candidate, int excludedIndex) {
        for (int i = 0; i < entries.size(); i++) {
            Appointment existing = entries.get(i);
            if (i == excludedIndex || !candidate.getStart().isBefore(existing.getEnd())
                    || !existing.getStart().isBefore(candidate.getEnd())) continue;
            Optional<LocalDate> clash = dates(candidate.getDate(), candidate.getRepeatUntil())
                    .filter(candidate::occursOn).filter(existing::occursOn).findFirst();
            if (clash.isPresent()) {
                throw new IllegalArgumentException("Overlaps " + existing.getTitle() + " on " + clash.get() + ".");
            }
        }
    }

    private static Stream<LocalDate> dates(LocalDate from, LocalDate to) {
        return Stream.iterate(from, day -> day.plusDays(1))
                .limit(ChronoUnit.DAYS.between(from, to) + 1);
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
            lines.add("SCHEDULER-4");
            for (Appointment a : next) {
                lines.add(String.join("\t", encode(a.getTitle()), a.getDate().toString(),
                        a.getStart().toString(), a.getEnd().toString(), encode(a.getLocation()),
                        a.getRepeat().name(), a.getRepeatUntil().toString(),
                        Integer.toString(a.getReminderMinutes())));
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
