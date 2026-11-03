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
