package demo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Occurrence(Appointment appointment, LocalDate date) {
    public LocalDateTime start() { return date.atTime(appointment.getStart()); }

    public Object[] toTableRow() {
        Object[] base = appointment.toTableRow();
        return new Object[]{base[0], date, base[2], base[3], base[4],
            appointment.getRepeat(), appointment.getReminderMinutes()};
    }
}
