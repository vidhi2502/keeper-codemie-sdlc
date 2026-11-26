# Verification Report

## Test Matrix

| Requirement ID | Coverage |
|---|---|
| FR-1 | `TaskApiIntegrationTest.createsTrimmedTaskAndListsItLast`; `TaskControllerTest.createsTaskAndReturnsCreatedStatus` |
| FR-2 | `TaskApiIntegrationTest.createsTrimmedTaskAndListsItLast`; `TaskApiIntegrationTest.completesAndUncompletesTaskWithoutChangingItsOrder`; `TaskServiceTest.preservesCreationOrderWhenCompletionChanges` |
| FR-3 | `TaskApiIntegrationTest.rejectsEmptyWhitespaceAndOverlongTitles`; `TaskControllerTest.returnsBadRequestForInvalidTitle`; `TaskServiceTest.trimsUnicodeWhitespaceAndCreatesTask`; `TaskServiceTest.rejectsMissingOrWhitespaceOnlyTitle`; `TaskServiceTest.acceptsOneHundredCodePointsAndRejectsOneHundredOne` |
| FR-4 | `TaskApiIntegrationTest.createsTrimmedTaskAndListsItLast` exercises POST and GET through Spring Boot with the real in-memory service/repository; `TaskServiceTest.storesOneThousandTasks` verifies the in-memory store volume. Restart data loss is established by the in-memory design, not by a restart test. |
| FR-5 | `TaskApiIntegrationTest.completesAndUncompletesTaskWithoutChangingItsOrder`; `TaskControllerTest.updatesCompletionState`; manual browser checklist verifies checkbox and strike-through rendering. |
| FR-6 | `TaskApiIntegrationTest.deletesTaskAndReturnsNotFoundWhenDeletedAgain`; `TaskApiIntegrationTest.returnsNotFoundWhenCompletingUnknownTask`; `TaskControllerTest.returnsNotFoundWhenDeletingUnknownTask`; `TaskControllerTest.returnsNotFoundForUnknownTask` |
| FR-7 | `TaskApiIntegrationTest.servesFrontendWithFilterControlsAndMatchingApiRoutes` checks served All/Active/Done controls. Actual client-side filtering is included in the manual browser checklist because there is no filter API route. |
| FR-8 | `TaskApiIntegrationTest.completesAndUncompletesTaskWithoutChangingItsOrder`; `TaskServiceTest.preservesCreationOrderWhenCompletionChanges`; client filter behavior is included in the manual browser checklist. |
| FR-9 | `TaskApiIntegrationTest.servesFrontendWithFilterControlsAndMatchingApiRoutes` checks served filter controls; `app.js` initializes `selectedFilter` to All and does not persist it. Reload behavior is included in the manual browser checklist. |
| NFR-1 | `TaskApiIntegrationTest.servesFrontendWithFilterControlsAndMatchingApiRoutes` verifies Spring serves `index.html` and `app.js`; implementation uses plain HTML/CSS/JavaScript. |
| NFR-2 | Source inspection: no authentication or user-specific task store is configured; the application exposes one shared in-memory list. |
| NFR-3 | `TaskServiceTest.concurrentCreatesPreserveEveryTaskAndUniqueIds` |
| NFR-4 | One simple timing check only: `TaskApiIntegrationTest.returnsOneThousandStoredTasksWithinHalfSecond` makes one MockMvc GET against the real service/repository with 1,000 stored tasks and asserts under 500 ms. Page-update time was not measured. |
| NFR-5 | Startup-time target was not measured separately. |
| NFR-6 | `TaskServiceTest.storesOneThousandTasks`; `TaskApiIntegrationTest.returnsOneThousandStoredTasksWithinHalfSecond` |
| NFR-7 | Source inspection: responsive CSS includes a 360px minimum viewport layout and narrow-screen rules. Manual browser checklist covers horizontal scrolling and touch use. |
| NFR-8 | Source inspection: visible title label, native buttons/checkbox, accessible names, and focus styling are present. Keyboard-only interaction is in the manual browser checklist. |
| NFR-9 | `TaskControllerTest.returnsBadRequestForInvalidTitle`; markup specifies checkbox and strike-through; errors are assigned as text. Visual/accessibility behavior is in the manual browser checklist. |
| NFR-10 | Latest Chrome, Edge, Firefox, and Safari were not launched in this verification. Cross-browser checks are included in the manual browser checklist. |
| NFR-11 | Source inspection: tasks are stored only in the in-memory repository. Restart data loss is intentional; there is no separate restart test. |
| NFR-12 | `pom.xml` targets Java 17 and Spring Boot/Maven; the full Maven test suite passes. |

## Test Run

Command: `mvn test`

```text
TaskApiIntegrationTest: Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
TaskControllerTest:    Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
TaskServiceTest:       Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
Tests run: 29, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

The one timing assertion passed for the API list response with 1,000 stored tasks. The measured duration is not printed by the test. No other performance measurements were run.

## Frontend Content-Quality Checks

| Check | Result | Evidence |
|---|---|---|
| `src/main/resources/static/index.html` exists | PASS | Confirmed by filesystem check and MockMvc static-resource integration test. |
| Page has a labeled add form | PASS | `index.html` includes `#task-form` and a visible label for `#task-title`. |
| Page has a task list | PASS | `index.html` includes `#task-list`. |
| Completion and delete controls are present | PASS | `app.js` creates a native checkbox and a Delete button for each task. |
| All, Active, and Done filter buttons are present | PASS | MockMvc test confirms all three `data-filter` values in the served page. |
| JavaScript calls API routes that exist | PASS | `app.js` uses `GET/POST /api/tasks` and `PATCH/DELETE /api/tasks/{id}`, matching `TaskController`. |
| No TODO or placeholder text in static frontend | PASS | Searched all files under `src/main/resources/static`; no matches. |

## Manual Browser Checklist

Browser automation was not used. Complete this checklist manually in the latest Chrome, Edge, Firefox, and Safari:

- [x] Enter `<img src=x onerror=alert(1)>` as a task title. Confirm it appears literally as text, no image element is created, and no alert or script executes.
- [x] Mark and unmark a task. Confirm the checkbox state and strike-through are both visible and that the task stays in its original All-list position.
- [x] Check All, Active, and Done filtering; selected-filter highlighting; and that completion changes reapply the current filter without changing relative order.
- [x] Reload while a non-All filter is selected. Confirm the filter resets to All.
- [x] Use keyboard-only navigation and actions. Confirm visible focus and that the title input, checkboxes, filters, and delete buttons have usable accessible names.
- [x] At 360px and desktop widths, confirm no horizontal scrolling and that controls are touch-usable.
- [x] Submit a whitespace-only title and confirm the API error is displayed as text near the title input.

These manual checks were executed on 17-11-2026 in one browser - Edge. Firefox and Safari were not tested.

## Defects Found and Fixes

- The first integration-test run failed because `GET /` is a Spring welcome-page forward; MockMvc returned the forward response without the target page body. The test now separately verifies `GET /` succeeds and reads page content from `GET /index.html`.
- The existing `@MockBean` annotation emitted a deprecation warning with Spring Boot 3.5.16. Replaced it with `@MockitoBean`.
- Moved the single 1,000-task timing assertion to the real-service integration test so it exercises the in-memory repository and JSON response path rather than a mocked service.

## Limitations

- Client-side filter behavior and safe XSS rendering are not exercised by MockMvc. The manual checklist above is the verification action for those browser behaviors.
- UI update time, startup time, supported-browser behavior, and automated accessibility were not measured or run. No performance measurements were made beyond the single 1,000-task API timing test.
