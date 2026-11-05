package demo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Tracks which upcoming occurrences have already notified this session. */
public final class Reminders {
    private final Set<Occurrence> shown = new HashSet<>();

    public List<Occurrence> due(List<Appointment> appointments, LocalDateTime now) {
        shown.removeIf(o -> !o.start().isAfter(now) || !appointments.contains(o.appointment()));
        List<Occurrence> due = new ArrayList<>();
        LocalDate today = now.toLocalDate();
        for (Appointment appointment : appointments) {
            int minutes = appointment.getReminderMinutes();
            if (minutes == 0) continue;
            for (int offset = 0; offset <= 1; offset++) {
                LocalDate date = today.plusDays(offset);
                if (!appointment.occursOn(date)) continue;
                Occurrence occurrence = new Occurrence(appointment, date);
                LocalDateTime start = occurrence.start();
                if (start.isAfter(now) && !now.isBefore(start.minusMinutes(minutes))
                        && shown.add(occurrence)) {
                    due.add(occurrence);
                }
            }
        }
        return due;
    }
}
