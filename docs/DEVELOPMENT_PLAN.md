# Five-day incremental development plan

Use the milestone dates in README.md. Scheduled work is 06:00 Africa/Johannesburg on 3-6 November 2026.

Day 1 contains only the basic window. Each subsequent day copies the preceding demo into `demos/dayN/src` and adds that day's features. Keep the `demo.Main` entry point and add a `Run Day N Demo.cmd` launcher using `demos/build-demo.ps1 -Day N -Run`. Preserve earlier days so the group can rerun them. Each day must compile independently and open a working GUI. Add meaningful business-rule tests as those rules are introduced. Verify and record actual results before committing and pushing.

The original full scheduler and its history are preserved locally outside this restarted repository. Reference implementation: sibling `AppointmentScheduler-complete`; old repository backup: sibling `RepositoryBackups`. Adapt reference code honestly; do not backdate commits or claim the existing code was originally authored on a later date.

Keep author and committer Noluthando <211407068+ZuluBarbie@users.noreply.github.com>, without AI co-author trailers. Preserve user changes. Push meaningful work each day to the private ZuluBarbie/AppointmentScheduler repository.

## Completion by 6 November

- Verify final functionality, validation, persistence, reminders, recurrence, views, clashes and export.
- Confirm names/student numbers and review UML, pseudocode and the technical report against actual code and tests.
- Prepare the required report PDF, Java source and 3-5 minute demonstration recording.
- Arrange marker access to the private repository. Submission is 8 November; 7 November is a buffer.
