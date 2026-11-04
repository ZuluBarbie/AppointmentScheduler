package demo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/** A validated appointment with immutable values. */
public final class Appointment {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter
            .ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private final String title;
    private final LocalDate date;
    private final LocalTime start;
    private final LocalTime end;
    private final String location;

    public Appointment(String title, LocalDate date, LocalTime start, LocalTime end,
            String location) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Enter an appointment title.");
        }
        if (title.trim().length() > 60) {
            throw new IllegalArgumentException("Use no more than 60 characters for the title.");
        }
        if (date == null || start == null || end == null) {
            throw new IllegalArgumentException("Date, start time and end time are required.");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time on the same day.");
        }
        this.title = title.trim();
        this.date = date;
        this.start = start;
        this.end = end;
        this.location = location == null ? "" : location.trim();
    }

    /** Parse form values strictly so invalid dates cannot silently change day. */
    public static Appointment fromInput(String title, String date, String start,
            String end, String location) {
        LocalDate parsedDate;
        LocalTime parsedStart;
        LocalTime parsedEnd;
        try {
            parsedDate = LocalDate.parse(clean(date), DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Enter a valid date as YYYY-MM-DD.");
        }
        try {
            parsedStart = LocalTime.parse(clean(start), TIME_FORMAT);
            parsedEnd = LocalTime.parse(clean(end), TIME_FORMAT);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Enter valid times as HH:mm, for example 09:00.");
        }
        return new Appointment(title, parsedDate, parsedStart, parsedEnd, location);
    }

    private static String clean(String value) { return value == null ? "" : value.trim(); }

    public String getTitle() { return title; }
    public LocalDate getDate() { return date; }
    public LocalTime getStart() { return start; }
    public LocalTime getEnd() { return end; }
    public String getLocation() { return location; }

    public Object[] toTableRow() {
        return new Object[]{title, date.toString(), start.format(TIME_FORMAT),
            end.format(TIME_FORMAT), location};
    }
}
