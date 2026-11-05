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

    public static void main(String[] args) throws Exception {
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
        bookChecks();
        seriesChecks();
        System.out.println(passed + " checks passed.");
    }

    private static void seriesChecks() throws Exception {
        var dir = java.nio.file.Files.createTempDirectory("scheduler-day4-");
        var file = dir.resolve("appointments.tsv");
        var date = LocalDate.of(2026, 11, 5);
        var series = new Appointment("Studio", date, LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Lab", Appointment.Repeat.WEEKLY, date.plusWeeks(2), 15);
        var book = new AppointmentBook(file);
        book.add(series);
        var rows = book.view(date, "Month");
        check("weekly rows retain their series and inclusive last date", rows.size() == 3
                && rows.get(2).date().equals(date.plusWeeks(2)) && rows.get(2).appointment() == series);
        var daily = new Appointment("Daily", date, LocalTime.of(11, 0), LocalTime.of(12, 0),
                "", Appointment.Repeat.DAILY, date.plusDays(2), 0);
        book.add(daily);
        check("daily series appears on consecutive days", book.view(date.plusDays(1), "Day").size() == 1
                && book.view(date.plusDays(2), "Day").size() == 1 && book.view(date.plusDays(3), "Day").isEmpty());
        try {
            book.add(Appointment.fromInput("Clash", date.plusWeeks(1).toString(), "09:30", "10:30", ""));
            throw new AssertionError("Accepted overlapping occurrence");
        } catch (IllegalArgumentException expected) {
            check("future occurrence clash leaves file unchanged", expected.getMessage().contains("Studio")
                    && new AppointmentBook(file).getAll().size() == 2);
        }
        try {
            book.update(1, new Appointment("Daily", date, LocalTime.of(8, 30), LocalTime.of(9, 30),
                    "", Appointment.Repeat.DAILY, date.plusDays(2), 0));
            throw new AssertionError("Accepted overlapping edit");
        } catch (IllegalArgumentException expected) {
            check("conflicting edit preserves its original series", book.getAll().get(1) == daily);
        }
        book.update(0, series);
        book.add(Appointment.fromInput("Adjacent", date.toString(), "10:00", "11:00", ""));
        check("self-edit and adjacent time slots are accepted", book.getAll().size() == 3);
        var loaded = new AppointmentBook(file).getAll().get(0);
        check("repeat and reminder settings survive reload", loaded.getRepeat() == Appointment.Repeat.WEEKLY
                && loaded.getRepeatUntil().equals(date.plusWeeks(2)) && loaded.getReminderMinutes() == 15);
        book.delete(0);
        check("deleting a series removes future occurrences", book.view(date.plusWeeks(2), "Day").isEmpty()
                && new AppointmentBook(file).getAll().size() == 2);
        rejects("repeat requires an end date", () -> new Appointment("Missing", date,
                LocalTime.NOON, LocalTime.of(13, 0), "", Appointment.Repeat.DAILY, null, 0));
        rejects("repeat range has a 366-day limit", () -> new Appointment("Too long", date,
                LocalTime.NOON, LocalTime.of(13, 0), "", Appointment.Repeat.DAILY, date.plusDays(367), 0));
        rejects("reminder limit is enforced", () -> new Appointment("Too early", date,
                LocalTime.NOON, LocalTime.of(13, 0), "", Appointment.Repeat.NONE, null, 1441));
        var reminders = new Reminders();
        var appointments = java.util.List.of(series, daily);
        check("no reminder before the lead time", reminders.due(appointments, date.atTime(8, 44)).isEmpty());
        check("reminder fires at lead time", reminders.due(appointments, date.atTime(8, 45)).size() == 1);
        check("same occurrence does not repeat its alert", reminders.due(appointments, date.atTime(8, 46)).isEmpty());
        check("past and disabled reminders stay silent", reminders.due(appointments, date.atTime(11, 30)).isEmpty());
        check("next weekly occurrence has its own alert", reminders.due(appointments,
                date.plusWeeks(1).atTime(8, 45)).size() == 1);
        var overnight = new Appointment("Early", date.plusDays(1), LocalTime.of(0, 5), LocalTime.of(1, 0),
                "", Appointment.Repeat.NONE, null, 15);
        check("reminder can fire the previous evening", new Reminders().due(
                java.util.List.of(overnight), date.atTime(23, 50)).size() == 1);
        var legacy = dir.resolve("legacy.tsv");
        java.nio.file.Files.writeString(legacy, "SCHEDULER-3\nTGVnYWN5\t2026-11-05\t09:00\t10:00\t\n");
        var original = new AppointmentBook(legacy).getAll().get(0);
        check("Day 3 file loads with repeat and reminders disabled", original.getTitle().equals("Legacy")
                && original.getRepeat() == Appointment.Repeat.NONE && original.getReminderMinutes() == 0);
    }

    private static void bookChecks() throws Exception {
        java.nio.file.Path dir=java.nio.file.Files.createTempDirectory("scheduler-day3-");
        java.nio.file.Path data=dir.resolve("appointments.tsv");
        AppointmentBook book=new AppointmentBook(data);
        check("new book is empty",book.getAll().isEmpty());
        Appointment first=Appointment.fromInput("Lecture | Café\tA","2026-11-04","10:00","11:00","Lab\n2");
        book.add(first);
        AppointmentBook reopened=new AppointmentBook(data);
        check("reopen preserves title, Unicode, separators and location",reopened.getAll().size()==1
                && reopened.getAll().get(0).getTitle().equals(first.getTitle())
                && reopened.getAll().get(0).getLocation().equals(first.getLocation()));
        check("reopen preserves date and times",reopened.getAll().get(0).getStart().equals(first.getStart())
                && reopened.getAll().get(0).getEnd().equals(first.getEnd())
                && reopened.getAll().get(0).getDate().equals(first.getDate()));
        Appointment moved=Appointment.fromInput("Moved","2026-11-05","08:00","09:00","");
        book.update(0,moved);
        check("edit replaces one record",book.getAll().size()==1 && book.getAll().get(0)==moved);
        check("edit survives restart",new AppointmentBook(data).getAll().get(0).getTitle().equals("Moved"));
        book.add(first);
        book.add(Appointment.fromInput("Monday","2026-11-02","08:00","09:00",""));
        book.add(Appointment.fromInput("Sunday","2026-11-08","08:00","09:00",""));
        book.add(Appointment.fromInput("Next Monday","2026-11-09","08:00","09:00",""));
        check("day view filters dates",book.view(java.time.LocalDate.of(2026,11,4),"Day").size()==1);
        check("week includes Monday and Sunday only",book.view(java.time.LocalDate.of(2026,11,4),"Week").size()==4);
        check("month includes all November appointments",book.view(java.time.LocalDate.of(2026,11,4),"Month").size()==5);
        check("views sort by date",book.view(java.time.LocalDate.of(2026,11,4),"Week").get(0).appointment().getTitle().equals("Monday"));
        check("views leave source order unchanged",book.getAll().get(0).getTitle().equals("Moved"));
        book.delete(0);
        check("delete removes only selected record",book.getAll().size()==4 && book.getAll().contains(first));
        check("delete survives restart",new AppointmentBook(data).getAll().size()==4);
        try { book.delete(999); throw new AssertionError("invalid index accepted"); }
        catch (IndexOutOfBoundsException expected) { check("invalid deletion preserves records",book.getAll().size()==4); }
        AppointmentBook boundary=new AppointmentBook(dir.resolve("boundary.tsv"));
        boundary.add(Appointment.fromInput("December","2026-12-31","08:00","09:00",""));
        boundary.add(Appointment.fromInput("January","2027-01-01","08:00","09:00",""));
        check("week crosses year boundary",boundary.view(java.time.LocalDate.of(2027,1,1),"Week").size()==2);
        check("month does not leak across years",boundary.view(java.time.LocalDate.of(2027,1,1),"Month").size()==1);
        boundary.add(Appointment.fromInput("Earlier","2027-01-01","07:00","08:00",""));
        check("same-day results sort by start time",boundary.view(java.time.LocalDate.of(2027,1,1),"Day").get(0).appointment().getTitle().equals("Earlier"));
        java.nio.file.Path corrupt=dir.resolve("corrupt.tsv");
        java.nio.file.Files.writeString(corrupt,"SCHEDULER-3\nbroken row\n");
        try { new AppointmentBook(corrupt); throw new AssertionError("corruption accepted"); }
        catch (java.io.IOException expected) { check("corrupt file fails visibly and remains unchanged",java.nio.file.Files.readString(corrupt).contains("broken row")); }
        java.nio.file.Files.writeString(corrupt,"unknown format");
        try { new AppointmentBook(corrupt); throw new AssertionError("bad header accepted"); }
        catch (java.io.IOException expected) { check("unknown format rejected",true); }
        AppointmentBook failing=new AppointmentBook(dir.resolve("gone").resolve("data.tsv"));
        try { failing.add(first); throw new AssertionError("failed save accepted"); }
        catch (java.io.IOException expected) { check("failed add preserves in-memory records",failing.getAll().isEmpty()); }
        java.nio.file.Path blocked=dir.resolve("blocked.tsv");
        AppointmentBook rollback=new AppointmentBook(blocked); rollback.add(first);
        java.nio.file.Files.delete(blocked); java.nio.file.Files.createDirectory(blocked);
        java.nio.file.Files.writeString(blocked.resolve("child"),"block replacement");
        try { rollback.update(0,moved); throw new AssertionError("failed edit accepted"); }
        catch (java.io.IOException expected) { check("failed edit preserves original record",rollback.getAll().get(0)==first); }
        try { rollback.delete(0); throw new AssertionError("failed delete accepted"); }
        catch (java.io.IOException expected) { check("failed delete preserves original record",rollback.getAll().get(0)==first); }
        try { book.getAll().clear(); throw new AssertionError("mutable result"); }
        catch (UnsupportedOperationException expected) { check("caller cannot mutate master list",book.getAll().size()==4); }
        AppointmentBook stale=new AppointmentBook(data);
        book.add(moved);
        try { stale.add(Appointment.fromInput("Later","2026-11-10","08:00","09:00","")); throw new AssertionError("stale overwrite accepted"); }
        catch (java.io.IOException expected) { check("stale window cannot overwrite newer save",
                stale.getAll().size()==4 && new AppointmentBook(data).getAll().size()==5); }
    }
}
