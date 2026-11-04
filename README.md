# Appointment Scheduler

Java Swing application for Software Design 1 (SDN150S), Project 12.

## Current version

Day 3 adds editing, deletion, automatic file storage and daily, weekly and monthly views. Each appointment has a title, date, start time, end time and optional location. Invalid dates, times, blank titles and an end time at or before the start are rejected.

## Run on Windows

A Java JDK version 21 or newer is required.

1. Double-click **Run Day 3 Demo.cmd** to build and open the application.
2. For subsequent runs, double-click the generated **Day 3 Demo** shortcut to open the GUI without a terminal.
3. Run the `.cmd` file again after editing the source.

Days 1 and 2 remain available through their launchers and shortcuts.

Day 3 saves to `dist/demos/day3/appointments.tsv` when run through its launcher. Keep that folder writable and back up the data file. Use one window per data file; if another window has already changed it, reopen before saving. Week views run Monday to Sunday. Select a table row to edit it, then choose **Save changes**. **Add appointment** always creates a new record. Deletion requires confirmation.

To edit the visual form, open `demos/day3` as a NetBeans project, then open `demo.SchedulerFrame` and choose **Design**. The paired `.form` file is included.

## Tests

From a terminal in the project folder:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File demos/test-demo.ps1 -Day 3
```

See [test results](docs/TEST_RESULTS.md) for verified checks and [requirements](docs/REQUIREMENTS.md) for the remaining assignment work.

## Report and design

The [technical progress report](docs/SDN150S_Project12_Technical_Report.pdf) covers Day 3 and includes pseudocode, a UML class diagram and the Java source listing. The [Word version](docs/SDN150S_Project12_Technical_Report.docx) is editable. The [diagram source](docs/day3-class-diagram.mmd) is maintained with the code.

## Development milestones

| Date | Features | Status |
| --- | --- | --- |
| 2 November | Basic window and empty schedule | Complete |
| 3 November | Add appointments, validation and table display | Complete |
| 4 November | Edit/delete, save/reload and day/week/month views | Complete |
| 5 November | Reminders, recurrence and clash detection | Planned |
| 6 November | Calendar export and final integration | Planned |

Development target: 6 November 2026. Submission deadline: 8 November 2026.
