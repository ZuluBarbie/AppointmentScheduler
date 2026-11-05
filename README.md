# Appointment Scheduler

Java Swing application for Software Design 1 (SDN150S), Project 12.

## Current version

Day 4 adds daily and weekly repeats, in-app reminders and clash checks. Use the repeat and reminder settings below the table. A repeat requires an end date within 366 days of the start. Keep the app open for reminders. A lead time of zero turns them off.

## Run on Windows

A Java JDK version 21 or newer is required.

1. Double-click **Run Day 4 Demo.cmd** to build and open the application.
2. For subsequent runs, double-click the generated **Day 4 Demo** shortcut to open the GUI without a terminal.
3. Run the `.cmd` file again after editing the source.

Days 1 to 3 are available through their launchers and shortcuts.

Day 4 saves to `dist/demos/day4/appointments.tsv` when run through its launcher. To start with Day 3 appointments, copy the Day 3 data file into the Day 4 folder before opening Day 4 and before entering new records there. Use one window per data file. Week views run Monday to Sunday. Selecting a repeat row loads the series settings. **Save changes** and **Delete selected** apply to the whole series.

To edit the base form, open `demos/day4` as a NetBeans project, then open `demo.SchedulerFrame` and choose **Design**. The repeat and reminder controls are added in Java.

## Tests

From a terminal in the project folder:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File demos/test-demo.ps1 -Day 4
```

See [test results](docs/TEST_RESULTS.md) for verified checks and [requirements](docs/REQUIREMENTS.md) for the remaining assignment work.

## Report and design

The technical progress report covers Day 4 and includes pseudocode, a UML class diagram and Java excerpts. The complete program is under `demos/day4/src`. [PDF report](docs/SDN150S_Project12_Technical_Report.pdf) · [Word report](docs/SDN150S_Project12_Technical_Report.docx) · [UML source](docs/day4-class-diagram.mmd)

## Development milestones

| Date | Features | Status |
| --- | --- | --- |
| 2 November | Basic window and empty schedule | Complete |
| 3 November | Add appointments, validation and table display | Complete |
| 4 November | Edit/delete, save/reload and day/week/month views | Complete |
| 5 November | Reminders, recurrence and clash detection | Complete |
| 6 November | Calendar export and final integration | Planned |

Development target: 6 November 2026. Submission deadline: 8 November 2026.
