---
name: pr-template
description: Pull request description template with Summary, Changes Made, Test Evidence, Known Limitations and Reviewer Checklist, plus a CHANGELOG entry format.
---
# PR description - all five sections are mandatory

## Summary
2-3 sentences: what was built and why.

## Changes Made
Bulleted list of every file added or modified, with the reason.

## Test Evidence
Paste the `mvn test` output or link to CI results.

## Known Limitations
Anything marked 'Not Found' or out of scope.

## Reviewer Checklist
- [ ] Requirements in docs/requirements.md are met
- [ ] All tests pass
- [ ] No secrets or .env files committed
- [ ] Design and review documents are up to date
- [ ] Code follows the Java rules in the repo

# CHANGELOG.md entry (Keep a Changelog format)
## [Unreleased]
### Added
- one line per feature