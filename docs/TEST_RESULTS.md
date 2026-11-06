# Test results

## Day 2 on 3 November 2026

Built independently with `demos/build-demo.ps1 -Day 2`, targeting Java 21. The build generated a runnable JAR and a terminal-free Windows shortcut.

`demos/test-demo.ps1 -Day 2`: **15 checks passed**. Checks cover title trimming, optional location, parsed field values, table formatting, blank/overlong titles, title length boundary, invalid dates, leap years, wrong date format, invalid hours/minutes, equal or reversed times and a missing constructor date.

GUI verification on Windows:

- Day 2 window opened with an empty five-column table.
- Adding a blank title displayed a validation message and kept the table empty.
- Adding `Group demonstration` with a valid date and 09:00-10:00 times displayed one row and a total of one.
- Input labels, success/error feedback and the in-memory storage notice were visible.

The sample appointment existed only in the verification window's memory.

## Day 3 on 4 November 2026

`demos/build-demo.ps1 -Day 3` built the standalone JAR and generated the Day 3 GUI shortcut. `demos/test-demo.ps1 -Day 3`: **38 checks passed**. The 15 Day 2 validation checks are retained; 23 additional checks cover:

- Saving and reopening Unicode titles, separators, locations, dates and times.
- Editing/deleting only the selected record and retaining changes after restart.
- Filtering daily, Monday-to-Sunday weekly and calendar-month views, including year boundaries and time ordering.
- Protecting the master list, rejecting unknown/corrupt files and preserving records after failed add/edit/delete writes.
- Rejecting a stale window's save after another instance has already changed the file.

Windows GUI verification used isolated data under `build/gui-day3`, not the normal demo data file. Day, Week and Month displayed one, two and three matching appointments respectively. Selecting a row populated the fields and enabled edit/delete controls. Editing a title through **Save changes** refreshed the table and displayed a saved message. Closing and reopening restored the edited title and three stored records. Deletion and write failures were checked by the automated tests, not by manually deleting user records.

NetBeans 31 opened `demos/day3/src/demo/SchedulerFrame.java` with its paired `.form` file in **Design** view. The form controls and Swing component palette were displayed without a form-loading error.

## Day 4 on 5 November 2026

`demos/build-demo.ps1 -Day 4` built the standalone JAR and generated the shortcut. `demos/test-demo.ps1 -Day 4` passed **55 checks**, including 38 adapted regression checks and 17 checks for repeat dates, clash rejection, series editing and deletion, adjacent appointments, saved settings, Day 3 file loading and reminder timing.

A Swing verification run on Windows used a separate file under `build/gui-day4`. The reminder popup displayed the title and start time, and selecting a repeat row populated its series settings. The Add button rejected the overlapping series and displayed its name and date. The verification compared the file bytes before and after rejection. Both rendered windows were checked visually.

## Day 5 on 6 November 2026

The standalone JAR built and the Day 5 shortcut was generated. `demos/test-demo.ps1 -Day 5` passed **64 checks**. Nine new checks covered iCalendar framing, visible recurrence dates, UTC conversion, text escaping, UTF-8 folding, unique event IDs, file replacement and a daylight-saving gap.

NetBeans 31 opened the Day 5 project and displayed its JFrame in **Design** view. The project's Ant build also produced a JAR using the Ant installation bundled with NetBeans.

A Windows Swing test activated **Export view (.ics)**, approved a destination in the save chooser and verified the success message and generated file. A two-day series produced one event in the Day view. The source data remained intact. The rendered window was inspected. An import into a third-party calendar was outside this test.
