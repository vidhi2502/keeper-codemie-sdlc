# Requirements

## 1. User Story

**Source: Jira epic KEEP-2 — Keeper To-Do Web App**  
https://mittalvidhi2004.atlassian.net/browse/KEEP-2

> A simple web app to manage daily tasks. Spring Boot REST API backend, plain HTML/CSS/JS frontend, in-memory storage.

**Source: Jira story KEEP-3 — Add and view tasks**  
https://mittalvidhi2004.atlassian.net/browse/KEEP-3

> As a user, I want to add tasks and see them in a list so that I can track what I need to do. Acceptance criteria: - I can add a task by typing a title and clicking Add. - All tasks appear in a list, newest last. - An empty title is rejected with a clear message. - Tasks are stored on the server (in memory is fine).

**Source: Jira story KEEP-4 — Complete and delete tasks**  
https://mittalvidhi2004.atlassian.net/browse/KEEP-4

> As a user, I want to mark tasks as done and delete them so that my list stays up to date.
>
> Acceptance criteria:
>
> - I can mark a task as done, and it appears struck through.
> - I can un-mark a done task.
> - I can delete a task.
> - Deleting a task that does not exist shows a clear error.

**Source: Jira story KEEP-5 — Filter tasks**  
https://mittalvidhi2004.atlassian.net/browse/KEEP-5

> As a user, I want to filter my list by All, Active or Done so that I can focus on what matters. Acceptance criteria: - Three filter buttons: All, Active, Done. - The list shows only the matching tasks. - The selected filter is visibly highlighted.

## 2. Clarifying Q&A

| Question | User's Answer |
|---|---|
| Should whitespace-only titles be rejected and surrounding whitespace trimmed? | Yes. Reject empty or whitespace-only titles with a clear error. Trim surrounding whitespace before saving. |
| How should the 100-character limit work? | Reject titles longer than 100 characters after trimming; do not truncate. A later clarification asks that exact error status codes and wording be deferred to the architecture stage. |
| How should ordering and filters behave? | New tasks appear last in creation order in all views. Active means not done; Done means completed; All shows everything. Preserve relative order in each view, and do not move a task when it is marked done or undone. |
| What error behavior is required? | Errors for invalid titles, unknown task IDs on completion updates, and missing-task deletion must be distinguishable from success and state what went wrong. Exact response status codes and wording are left to architecture. |
| What responsive and accessibility behavior is required? | Support widths from 360px through desktop without horizontal scrolling; controls must be touch-usable. Support keyboard-only actions, a visible title label and keyboard focus, accessible names for buttons and checkboxes, checked state and strike-through for completed tasks (not color alone), and textual errors near the input. Support the latest versions of Chrome, Edge, Firefox, and Safari. |
| What are the performance, data-volume, reliability, and concurrency targets? | Each API call responds in under 500 ms on a typical developer laptop with up to 1,000 tasks; the page reflects each user action within 1 second; startup is under 15 seconds. Support up to 1,000 tasks. There is no uptime target. Data loss on restart is accepted. Concurrent writes are out of scope, but simultaneous requests must not corrupt the in-memory store, which must be thread-safe. |
| Should the selected filter persist? | No. Highlight the selected filter while the page is open; after reload, reset to All. Do not use browser storage or URL parameters for the filter. |

## 3. Functional Requirements

- **FR-1 [KEEP-3]:** A user can create a task by entering a title and activating Add.
- **FR-2 [KEEP-3]:** The task list displays tasks in creation order, with newly created tasks last.
- **FR-3 [KEEP-3]:** Before saving, the application trims surrounding whitespace from a title. It rejects an empty or whitespace-only title and any trimmed title longer than 100 characters; it does not truncate titles. The user receives a clear error.
- **FR-4 [KEEP-2, KEEP-3]:** The application provides a Spring Boot REST API under `/api/tasks` for task operations and stores tasks on the server in memory. Stored tasks are lost when the application restarts.
- **FR-5 [KEEP-4]:** A user can mark a task done and unmark a done task. A done task is shown with strike-through styling.
- **FR-6 [KEEP-4]:** A user can delete a task. If a completion update or deletion targets a task ID that does not exist, the application reports an error that is distinguishable from success and states what went wrong.
- **FR-7 [KEEP-5]:** The list provides All, Active, and Done filters. All shows every task, Active shows tasks that are not done, and Done shows completed tasks.
- **FR-8 [KEEP-5]:** The selected filter is visibly highlighted while the page is open. Changing a task's completion state does not change its position in All; each filtered view preserves the creation order of its matching tasks.
- **FR-9 [KEEP-5]:** On page reload, the selected filter resets to All. The application does not persist the filter in browser storage or URL parameters.

## 4. Non-Functional Requirements

- **NFR-1 [KEEP-2]:** The frontend uses plain HTML, CSS, and JavaScript and is served by Spring Boot. No frontend framework is required.
- **NFR-2 [KEEP-2]:** No login is required. The application supports a single user and one shared task list.
- **NFR-3 [KEEP-2]:** The in-memory task store is thread-safe and does not corrupt task data when requests arrive simultaneously.
- **NFR-4 [KEEP-2]:** With up to 1,000 stored tasks on a typical developer laptop, each API call completes in under 500 ms and the page reflects a user action within 1 second.
- **NFR-5 [KEEP-2]:** The application starts in under 15 seconds.
- **NFR-6 [KEEP-2]:** The application supports up to 1,000 tasks. No behavior or performance target is required beyond that volume.
- **NFR-7 [KEEP-2]:** At viewport widths from 360px through desktop widths, the page has no horizontal scrolling and all controls are usable by touch.
- **NFR-8 [KEEP-2]:** All actions are usable with a keyboard alone. The task-title input has a visible label, controls are buttons or checkboxes with accessible names, and keyboard focus is visible.
- **NFR-9 [KEEP-4]:** A completed task is indicated by both strike-through styling and a checked checkbox, not by color alone. Error messages are displayed as text near the title input.
- **NFR-10 [KEEP-2]:** The frontend works in the latest versions of Chrome, Edge, Firefox, and Safari.
- **NFR-11 [KEEP-2]:** No uptime target is required. Loss of in-memory data on restart is accepted.
- **NFR-12 [KEEP-2]:** Implementation follows the stated Spring Boot REST API and plain HTML/CSS/JavaScript stack; API operation details not specified here are determined during architecture.

## 5. Acceptance Criteria

- **AC-1 (FR-1, FR-2):** Given a user enters a valid title, when they activate Add, then the task is stored and appears last in the list.
- **AC-2 (FR-3):** Given a title is empty, whitespace-only, or longer than 100 characters after trimming, when the user submits it, then it is not saved and a clear error is shown. Given a valid title has surrounding whitespace, when it is saved, then the stored title has that whitespace trimmed.
- **AC-3 (FR-4):** Given the application is running, when task operations are performed through the REST API under `/api/tasks`, then task data is held in server-side memory and is available to the frontend served by Spring Boot. When the application restarts, then the in-memory task data is lost.
- **AC-4 (FR-5):** Given a task is active, when the user marks it done, then it is shown as completed with strike-through styling and a checked checkbox. When the user unmarks it, then it is shown as active.
- **AC-5 (FR-6):** Given an existing task, when the user deletes it, then it is removed. Given a completion update or deletion targets a nonexistent task ID, then an error states what went wrong and is distinguishable from success.
- **AC-6 (FR-7, FR-8):** Given tasks include both active and completed tasks, when the user selects All, Active, or Done, then only the matching tasks are shown in creation order and the selected filter is visibly highlighted. Marking or unmarking a task does not move it in All.
- **AC-7 (FR-9):** Given a filter other than All is selected, when the page reloads, then All is selected and no browser storage or URL parameter is used to restore the previous filter.
- **AC-8 (NFR-3, NFR-4, NFR-5, NFR-6):** Given up to 1,000 tasks, API calls complete in under 500 ms on a typical developer laptop, the page reflects an action within 1 second, and startup completes in under 15 seconds. Simultaneous requests do not corrupt the in-memory task store.
- **AC-9 (NFR-7, NFR-8, NFR-9, NFR-10):** Given a supported browser and viewport at least 360px wide, then the page has no horizontal scrolling, controls work by touch and keyboard, focus is visible, controls have accessible names, completion is not conveyed by color alone, and errors appear as text near the input.

## 6. Assumptions, Out of Scope, Open Questions

### Assumptions

- The app is a single-user application with no login and one shared task list, as directed by the user.
- In-memory storage is intentional; task data does not survive an application restart.
- The task title limit is 100 characters after trimming surrounding whitespace.
- The user specified the API base path `/api/tasks`, but not HTTP methods, request/response schemas, or exact routes. Architecture will define those details.
- Task identifiers are available to support completion updates and deletion; their representation is an architecture decision.
- Deletion confirmation behavior and an empty-list presentation were not specified; no additional confirmation or empty-state behavior is required by these requirements.
- “Desktop width” means supported desktop browser viewport sizes; no maximum viewport width was specified.
- The application is expected to run in a local development context. No additional security controls or deployment/network exposure requirements were provided.

### Out of Scope

- Authentication, multiple users, and separate per-user task lists.
- Persistence across application restarts.
- Due dates, priorities, and task editing.
- More than 1,000 tasks, concurrent-write semantics, and an uptime target.
- Persisting the selected filter across reloads or navigation.
- Formal WCAG certification or accessibility requirements beyond those listed above.

### Open Questions

- None identified for requirements. API route details and other implementation choices are deferred to the architecture stage.
