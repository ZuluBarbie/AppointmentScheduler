# Incremental group demonstrations

These are simplified teaching versions adapted from an existing local scheduler reference. This restarted repository begins with Day 1 only. They demonstrate features progressively; they do not represent the original creation history of the reference application.

Each day's source is independent and retained so the group can rerun earlier demonstrations. Future days are created on their actual work dates. Each entry point is `demo.Main`, built using only that day's `src` folder.

## Run Day 1

Double-click **Day 1 Demo** (the shortcut with an arrow) in the main project folder. It opens the graphical application without a terminal. The window contains today's date and an empty, read-only appointment table. No add/edit/delete, storage, recurrence or reminders are included yet.

After editing source, use **Run Day 1 Demo.cmd** to rebuild and launch. This developer launcher briefly opens a terminal for compiler messages. Every build creates a new JAR and updates the shortcut, so rebuilding works even while an older demo is still open. Existing windows keep their earlier version until closed. Build outputs are retained in `dist/demos/day1`; shortcuts are local and regenerated on another computer by running the build script.

Explain the JFrame, BorderLayout, JTable, constructor calls and Swing event thread. This is a short interface demonstration, not the final assignment demo.

## Planned feature progression

| Day | Date | Features visible to the group |
| --- | --- | --- |
| 1 | 2 November | Basic window and empty schedule |
| 2 | 3 November | Add appointments, validate inputs, display them in the table |
| 3 | 4 November | Edit/delete appointments, save/reload, day/week/month views |
| 4 | 5 November | Reminders, recurrence and clash detection |
| 5 | 6 November | Calendar export, final integration and submission rehearsal |

Days 2-5 are planned, not yet built or verified. Each later day includes the earlier features and gets its own `Run Day N Demo.cmd` launcher. Build and manually check the GUI before committing it. The full reference application is preserved outside this repository.
