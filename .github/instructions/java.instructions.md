---
applyTo: "**/*.java"
---
# Java coding rules
- Target Java 17. Use Maven standard layout: src/main/java and src/test/java.
- Class names in PascalCase, methods and variables in camelCase. Names must be self-explanatory.
- Keep methods small and single-purpose. Avoid duplicated logic; extract shared helper methods.
- Add Javadoc to every public class and method.
- Validate all external input (file paths, CLI arguments, API responses). Never log or print secrets.
- Handle errors explicitly. Never swallow exceptions silently.
- Use JUnit 5 for tests. Test names describe behaviour, for example `returnsNotFoundWhenReadmeMissing`.
- Every feature needs tests for the happy path AND for "Not Found" / missing-input edge cases.