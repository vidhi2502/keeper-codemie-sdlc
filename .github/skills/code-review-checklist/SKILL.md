---
name: code-review-checklist
description: Structured code review checklist covering correctness, security, error handling, test coverage, code clarity, DRY principle and dependency safety for Java code.
---
# Code review checklist

Evaluate every area. Give PASS, FAIL or PARTIAL with file and line evidence.

| Area | Review Question |
|---|---|
| Correctness | Does each component behave as specified in requirements.md? |
| Security | Are secrets excluded from output? Is user input validated? |
| Error Handling | Are API failures, missing files and empty repos handled gracefully? |
| Test Coverage | Do tests cover the happy path AND the 'Not Found' / missing-field edge cases? |
| Code Clarity | Are names self-explanatory? Is the logic easy to follow without comments? |
| DRY Principle | Is there duplicated logic that can become a shared method? |
| Dependency Safety | Are any dependency versions in pom.xml known to be vulnerable? |

## Dependency check
Inspect pom.xml versions. If possible run `mvn versions:display-dependency-updates` and flag outdated or vulnerable libraries.

## Output
Write docs/code-review.md with the table above, a list of fixes applied, and any open issues.