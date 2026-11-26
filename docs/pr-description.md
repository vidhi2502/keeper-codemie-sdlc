## Summary
Keeper is a to-do web app with a Spring Boot REST API and a plain HTML/CSS/JS frontend. It was built through the 8-stage agentic SDLC pipeline from Jira epic KEEP-2 and stories KEEP-3 to KEEP-5.

## Changes Made
- **Pipeline setup:** Added the `.github` agents, skills, hooks, and prompts for the pipeline.
- **Docs:** Added requirements, architecture, design review, implementation plan, code review, and verification report.
- **Backend:** Implemented the Spring Boot task API with validation and in-memory storage.
- **Frontend:** Added the plain HTML/CSS/JS task interface with filters and text-only rendering.
- **Tests:** Added service, MockMvc, and integration tests, including one API timing check.
- **README:** Documented setup, usage, task behavior, and API routes.
- **Jira:** Created plan tasks KEEP-6 to KEEP-11 under epic KEEP-2.

## Test Evidence
`mvn test` — 29 tests, 0 failures, 0 errors, 0 skipped; BUILD SUCCESS.

## Known Limitations
- Manual browser checks were run in Edge only; Chrome, Firefox and Safari were not tested.
- Startup time and UI update time were not measured; only one API timing check was run.
- Restart data loss was verified by the in-memory design only; no restart test was run.

## Reviewer Checklist
- [ ] Requirements in `docs/requirements.md` are met
- [ ] All tests pass
- [ ] No secrets or `.env` files are committed
- [ ] Docs are up to date
- [ ] Code follows the repository rules
