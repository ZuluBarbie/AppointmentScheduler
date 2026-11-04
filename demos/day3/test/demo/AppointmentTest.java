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
        System.out.println(passed + " checks passed.");
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
        check("views sort by date",book.view(java.time.LocalDate.of(2026,11,4),"Week").get(0).getTitle().equals("Monday"));
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
        check("same-day results sort by start time",boundary.view(java.time.LocalDate.of(2027,1,1),"Day").get(0).getTitle().equals("Earlier"));
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
        try { stale.add(first); throw new AssertionError("stale overwrite accepted"); }
        catch (java.io.IOException expected) { check("stale window cannot overwrite newer save",
                stale.getAll().size()==4 && new AppointmentBook(data).getAll().size()==5); }
    }
}
