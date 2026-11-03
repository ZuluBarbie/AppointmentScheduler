# Appointment Scheduler

Java Swing application for Software Design 1 (SDN150S), Project 12.

## Current version

Day 2 adds appointments to a read-only table. Each appointment has a title, date, start time, end time and optional location. Invalid dates, times, blank titles and an end time at or before the start are rejected. Appointments are held in memory and are lost when the window closes.

## Run on Windows

A Java JDK version 21 or newer is required.

1. Double-click **Run Day 2 Demo.cmd** to build and open the application.
2. For subsequent runs, double-click the generated **Day 2 Demo** shortcut to open the GUI without a terminal.
3. Run the `.cmd` file again after editing the source.

Day 1 remains available through **Run Day 1 Demo.cmd** and its generated shortcut.

## Tests

From a terminal in the project folder:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File demos/test-demo.ps1 -Day 2
```

See [test results](docs/TEST_RESULTS.md) for verified checks and [requirements](docs/REQUIREMENTS.md) for the remaining assignment work.

## Report and design

The [technical progress report](docs/SDN150S_Project12_Technical_Report.pdf) covers Day 2 and includes pseudocode, a UML class diagram and the Java source listing. The [Word version](docs/SDN150S_Project12_Technical_Report.docx) is editable. The [diagram source](docs/day2-class-diagram.mmd) is maintained with the code.

## Development milestones

| Date | Features | Status |
| --- | --- | --- |
| 2 November | Basic window and empty schedule | Complete |
| 3 November | Add appointments, validation and table display | Complete |
| 4 November | Edit/delete, save/reload and day/week/month views | Planned |
| 5 November | Reminders, recurrence and clash detection | Planned |
| 6 November | Calendar export and final integration | Planned |

Development target: 6 November 2026. Submission deadline: 8 November 2026.
