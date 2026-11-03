package demo;

import java.time.LocalDate;
import java.time.LocalTime;

/** Business-rule regression checks; run through test-demo.ps1. */
public class AppointmentTest {
    private static int passed;

    private static void check(String name, boolean condition) {
        if (!condition) { throw new AssertionError(name); }
        passed++;
        System.out.println("PASS " + name);
    }

    private static void rejects(String name, Runnable action) {
        try {
            action.run();
            throw new AssertionError("Accepted invalid input: " + name);
        } catch (IllegalArgumentException expected) {
            check(name, !expected.getMessage().isBlank());
        }
    }

    public static void main(String[] args) {
        Appointment valid = Appointment.fromInput("  Group meeting  ", "2026-11-03",
                "09:05", "10:00", "  Lab 1  ");
        check("trim title and location", valid.getTitle().equals("Group meeting")
                && valid.getLocation().equals("Lab 1"));
        check("preserve parsed date and times", valid.getDate().equals(LocalDate.of(2026, 11, 3))
                && valid.getStart().equals(LocalTime.of(9, 5)) && valid.getEnd().equals(LocalTime.of(10, 0)));
        check("table row uses five columns and padded times", valid.toTableRow().length == 5
                && valid.toTableRow()[2].equals("09:05"));
        rejects("blank title", () -> Appointment.fromInput(" ", "2026-11-03", "09:00", "10:00", ""));
        rejects("title longer than 60 characters", () -> Appointment.fromInput("a".repeat(61),
                "2026-11-03", "09:00", "10:00", ""));
        rejects("invalid calendar day", () -> Appointment.fromInput("Meeting", "2026-02-30", "09:00", "10:00", ""));
        rejects("non-leap February 29", () -> Appointment.fromInput("Meeting", "2026-02-29", "09:00", "10:00", ""));
        rejects("wrong date format", () -> Appointment.fromInput("Meeting", "03/11/2026", "09:00", "10:00", ""));
        rejects("24:00 start time", () -> Appointment.fromInput("Meeting", "2026-11-03", "24:00", "10:00", ""));
        rejects("invalid minutes", () -> Appointment.fromInput("Meeting", "2026-11-03", "09:60", "10:00", ""));
        rejects("equal start and end", () -> Appointment.fromInput("Meeting", "2026-11-03", "09:00", "09:00", ""));
        rejects("end before start", () -> Appointment.fromInput("Meeting", "2026-11-03", "10:00", "09:00", ""));
        rejects("null date in constructor", () -> new Appointment("Meeting", null, LocalTime.NOON,
                LocalTime.of(13, 0), ""));
        check("leap date and optional location", Appointment.fromInput("Meeting", "2028-02-29",
                "09:00", "10:00", null).getLocation().isEmpty());
        check("60-character title allowed", Appointment.fromInput("a".repeat(60), "2026-11-03",
                "09:00", "10:00", "").getTitle().length() == 60);
        System.out.println(passed + " checks passed.");
    }
}
