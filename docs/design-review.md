# Design Review

## 1. Findings

| ID | Area | Severity | Finding | Decision | Status |
|---|---|---|---|---|---|
| DR-1 | Reliability / concurrency | Medium | The architecture promised a synchronized repository and list snapshots but did not define whether task values were mutable. A shallow list snapshot of mutable tasks could still expose changes outside the repository lock during concurrent reads and writes. | Accept: make task values immutable, replace values on update, and return detached ordered snapshots. | Resolved in architecture |
| DR-2 | Requirements coverage / filtering | Medium | After a completion update, the data flow only said to update the checkbox and strike-through. Without reapplying the selected filter, a completed task could remain in Active or an active task could remain in Done. | Accept: update the local ordered task value and reapply the current filter after a successful update. | Resolved in architecture |
| DR-3 | Requirements coverage / validation | Medium | Java `String.length()` counts UTF-16 code units, not Unicode code points, and `String.strip()` does not trim every Unicode space separator. This could reject a title with 100 supplementary characters or accept a space-only title that the requirement expects to reject. | Accept: define a character as a Unicode code point, trim boundary code points classified by `Character.isWhitespace` or `Character.isSpaceChar`, and count code points after trimming. | Resolved in architecture |
| DR-4 | Security / testability | Low | MockMvc can verify API behavior but cannot establish that the browser renders a markup-looking title as text rather than creating DOM elements. | Accept: require a browser-level check of literal text rendering in addition to API tests. | Resolved in architecture |
| DR-5 | Error handling | Low | “All API errors” were promised in the JSON envelope, while the listed handler responsibilities did not include unsupported methods. That left a gap in the stated contract for requests to documented routes using an unsupported method. | Accept: scope the envelope to documented operations and define a 405 `METHOD_NOT_ALLOWED` response for unsupported methods on those routes. | Resolved in architecture |
| DR-6 | Testability / performance | Low | The architecture repeated the 500 ms API, 1 second UI, and 15 second startup targets but did not say how the verification stage should measure or report them. | Accept: measure with 1,000 tasks on a typical developer laptop and record the environment and observed API, UI, and startup times. | Resolved in architecture |
| DR-7 | Reliability / scope | Low | In-memory storage loses tasks on restart. This is a durability limitation, but it is explicitly accepted by FR-4 and NFR-11. | Reject durable persistence as out of scope; preserve the specified in-memory behavior and reset on restart. | Rejected — out of scope |

## 2. Agreed Design Decisions

- Task values are immutable. The repository synchronizes ID allocation and map access, replaces immutable values on completion updates, and returns detached snapshots in insertion order.
- After a successful completion update, the browser replaces the corresponding value in its ordered local list and reapplies the selected filter. This keeps Active and Done accurate without changing the task's position in All.
- “100 characters” means 100 Unicode code points after trimming boundary code points classified by `Character.isWhitespace` or `Character.isSpaceChar`. Combining sequences may contain multiple code points.
- XSS protection remains based on rendering dynamic strings as text. Browser-level verification supplements backend tests to check that markup-looking titles do not create DOM elements or execute.
- Errors from documented API operations use the JSON error envelope. Unsupported methods on documented routes return 405 with `METHOD_NOT_ALLOWED`; unexpected errors remain generic and do not expose stack traces.
- Verification records observed API, UI, and startup timing against the requirements' targets using 1,000 tasks on a typical developer laptop.
- Durable storage is not added. The restart data loss is explicitly accepted by the requirements.

## 3. Changes Made to `architecture.md`

- **DR-1:** Specified immutable task values, replacement on update, synchronized ID/map access, and detached ordered snapshots.
- **DR-2:** Specified updating the local ordered list and reapplying the selected filter after a successful completion update.
- **DR-3:** Replaced UTF-16 code-unit counting with a documented Unicode code-point limit and defined boundary whitespace trimming to include both Java whitespace and space characters.
- **DR-4:** Added a browser-level check for literal rendering of markup-looking task titles; retained JUnit 5 and MockMvc for backend behavior.
- **DR-5:** Scoped the error envelope to documented API operations and defined 405 handling for unsupported methods on those routes.
- **DR-6:** Added a verification measurement approach for API response time, page update time, and startup time.
- **DR-7:** No architecture change. Durable storage remains rejected because persistence across restart is out of scope under FR-4 and NFR-11.
