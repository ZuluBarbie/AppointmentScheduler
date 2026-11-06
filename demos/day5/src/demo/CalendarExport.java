package demo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class CalendarExport {
    private static final DateTimeFormatter UTC = DateTimeFormatter
            .ofPattern("uuuuMMdd'T'HHmmss'Z'", Locale.ROOT).withZone(ZoneOffset.UTC);

    private CalendarExport() {}

    public static void write(Path target, List<Occurrence> rows, ZoneId zone, boolean replace)
            throws IOException {
        String calendar = render(rows, zone, Instant.now());
        Path destination = target.toAbsolutePath();
        Path temporary = Files.createTempFile(destination.getParent(), "calendar-", ".tmp");
        try {
            Files.writeString(temporary, calendar, StandardCharsets.UTF_8);
            if (replace) Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING);
            else Files.move(temporary, destination);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    static String render(List<Occurrence> rows, ZoneId zone, Instant timestamp) {
        StringBuilder output = new StringBuilder();
        line(output, "BEGIN:VCALENDAR");
        line(output, "VERSION:2.0");
        line(output, "PRODID:-//SDN150S Group 12//Appointment Scheduler//EN");
        for (Occurrence row : rows) {
            Appointment appointment = row.appointment();
            line(output, "BEGIN:VEVENT");
            line(output, "UID:" + UUID.randomUUID() + "@appointment-scheduler.local");
            line(output, "DTSTAMP:" + UTC.format(timestamp));
            line(output, "DTSTART:" + eventTime(row.start(), zone));
            line(output, "DTEND:" + eventTime(row.date().atTime(appointment.getEnd()), zone));
            line(output, "SUMMARY:" + escape(appointment.getTitle()));
            if (!appointment.getLocation().isEmpty()) {
                line(output, "LOCATION:" + escape(appointment.getLocation()));
            }
            line(output, "END:VEVENT");
        }
        line(output, "END:VCALENDAR");
        return output.toString();
    }

    private static String eventTime(LocalDateTime time, ZoneId zone) {
        List<ZoneOffset> offsets = zone.getRules().getValidOffsets(time);
        if (offsets.isEmpty()) {
            throw new DateTimeException(time + " does not exist in " + zone + " due to a clock change.");
        }
        Instant instant = time.toInstant(offsets.getFirst());
        int year = instant.atOffset(ZoneOffset.UTC).getYear();
        if (year < 1 || year > 9999) throw new DateTimeException("Calendar export requires years 0001 to 9999.");
        return UTC.format(instant);
    }

    private static String escape(String value) {
        if (value.codePoints().anyMatch(c -> c < 32 && c != '\t' && c != '\r' && c != '\n' || c == 127)) {
            throw new IllegalArgumentException("An appointment contains an unsupported control character.");
        }
        return value.replace("\\", "\\\\").replace(",", "\\,").replace(";", "\\;")
                .replace("\r\n", "\\n").replace("\n", "\\n").replace("\r", "\\n");
    }

    /** Fold by UTF-8 bytes without splitting a Unicode character. */
    private static void line(StringBuilder output, String value) {
        int bytes = 0;
        for (int index = 0; index < value.length();) {
            int codePoint = value.codePointAt(index);
            String character = Character.toString(codePoint);
            int size = character.getBytes(StandardCharsets.UTF_8).length;
            if (bytes + size > 75) {
                output.append("\r\n ");
                bytes = 1;
            }
            output.append(character);
            bytes += size;
            index += Character.charCount(codePoint);
        }
        output.append("\r\n");
    }
}
