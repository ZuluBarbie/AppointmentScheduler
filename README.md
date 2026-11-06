# Appointment Scheduler

Java Swing application for Software Design 1 (SDN150S), Project 12.

## Current version

Day 5 exports the displayed Day, Week or Month to an iCalendar file. The scheduler supports appointment creation, editing and deletion, local storage, daily and weekly repeats, in-app reminders and clash checks.

## Run on Windows

A Java JDK version 21 or newer is required.

1. Double-click **Run Day 5 Demo.cmd** to build and open the application.
2. For subsequent runs, double-click the generated **Day 5 Demo** shortcut to open the GUI without a terminal.
3. Run the `.cmd` file again after editing the source.

Days 1 to 4 are available through their launchers and shortcuts.

Day 5 saves to `dist/demos/day5/appointments.tsv` when run through its launcher. To start with Day 4 appointments, copy the Day 4 data file into the Day 5 folder before opening Day 5 and before entering new records there. Use one window per data file. Week views run Monday to Sunday. **Save changes** and **Delete selected** apply to the whole series when a repeat row is selected. A repeat requires an end date within 366 days of the start. Keep the app open for reminders. Zero turns them off.

Choose a view and date, then click **Export view (.ics)**. The file contains one event per displayed occurrence and uses the computer’s time zone to convert event times to UTC. Import it into your calendar as a snapshot. Reimporting may create duplicates. The scheduler’s reminders stay in the app.

Open `demos/day5` with **File → Open Project** in NetBeans. Open **Source Packages → demo → SchedulerFrame.java → Design** to see the base form and its export button. The repeat and reminder controls are added in Java. Use **Run Project** to show the full window.

## Tests

From a terminal in the project folder:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File demos/test-demo.ps1 -Day 5
```

See [test results](docs/TEST_RESULTS.md) for verified checks and [requirements](docs/REQUIREMENTS.md) for the remaining assignment work.

## Report and design

The technical report covers Day 5 and includes pseudocode, a UML class diagram and Java excerpts. The complete program is under `demos/day5/src`. [PDF report](docs/SDN150S_Project12_Technical_Report.pdf) · [Word report](docs/SDN150S_Project12_Technical_Report.docx) · [UML source](docs/day5-class-diagram.mmd) · [Submission and recording guide](docs/SUBMISSION.md)

## Development milestones

| Date | Features | Status |
| --- | --- | --- |
| 2 November | Basic window and empty schedule | Complete |
| 3 November | Add appointments, validation and table display | Complete |
| 4 November | Edit/delete, save/reload and day/week/month views | Complete |
| 5 November | Reminders, recurrence and clash detection | Complete |
| 6 November | Calendar export and final integration | Complete |

Development target: 6 November 2026. Submission deadline: 8 November 2026.
