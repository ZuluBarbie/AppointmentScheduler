# Demonstrations

Each `dayN/src` folder contains a standalone version with entry point `demo.Main`.

## Day 1

An empty appointment table and date heading. Run **Run Day 1 Demo.cmd** from the project root.

## Day 2

An appointment entry form and table. Run **Run Day 2 Demo.cmd** from the project root.

For a short group demonstration:

1. Leave the title blank and choose **Add appointment** to show validation.
2. Enter a title, a date as `YYYY-MM-DD`, and times as `HH:mm`.
3. Choose **Add appointment** and show the new table row.
4. Try an end time before the start time; the existing rows remain unchanged.

Appointments are kept only while the Day 2 window is open. Editing, deletion, storage and reminders are scheduled for later versions.

## Build and launch

The `.cmd` launchers compile the selected source and generate a **Day N Demo** shortcut in the project root. Use the shortcut for presentations without a terminal window. Each build uses a new JAR filename, allowing an earlier version to remain open during a rebuild. Build output is stored under `build/` and `dist/` and is excluded from Git.
