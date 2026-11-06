package demo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public final class Appointment {
    public enum Repeat { NONE, DAILY, WEEKLY }
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter
            .ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private final String title;
    private final LocalDate date;
    private final LocalTime start;
    private final LocalTime end;
    private final String location;
    private final Repeat repeat;
    private final LocalDate repeatUntil;
    private final int reminderMinutes;

    public Appointment(String title, LocalDate date, LocalTime start, LocalTime end,
            String location) {
        this(title, date, start, end, location, Repeat.NONE, null, 0);
    }

    public Appointment(String title, LocalDate date, LocalTime start, LocalTime end,
            String location, Repeat repeat, LocalDate repeatUntil, int reminderMinutes) {
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
        this.repeat = Objects.requireNonNull(repeat);
        if (repeat != Repeat.NONE && (repeatUntil == null || repeatUntil.isBefore(date)
                || ChronoUnit.DAYS.between(date, repeatUntil) > 366)) {
            throw new IllegalArgumentException("Repeat until must be on or within 366 days of the start date.");
        }
        if (reminderMinutes < 0 || reminderMinutes > 1440) {
            throw new IllegalArgumentException("Reminder must be between 0 and 1440 minutes.");
        }
        this.repeatUntil = repeat == Repeat.NONE ? date : repeatUntil;
        this.reminderMinutes = reminderMinutes;
    }

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
    public Repeat getRepeat() { return repeat; }
    public LocalDate getRepeatUntil() { return repeatUntil; }
    public int getReminderMinutes() { return reminderMinutes; }

    public boolean occursOn(LocalDate day) {
        if (day.isBefore(date) || day.isAfter(repeatUntil)) return false;
        return repeat != Repeat.WEEKLY || day.getDayOfWeek() == date.getDayOfWeek();
    }

    public Object[] toTableRow() {
        return new Object[]{title, date.toString(), start.format(TIME_FORMAT),
            end.format(TIME_FORMAT), location};
    }
}
