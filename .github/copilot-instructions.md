# Agentic SDLC Pipeline - Global Rules

This repository implements an 8-stage agentic SDLC pipeline using GitHub Copilot.
Each stage is a custom agent defined in `.github/agents/`, coordinated by an orchestrator agent.

## Pipeline stages and artifacts (in order)
1. Requirements -> docs/requirements.md
2. Architecture -> docs/architecture.md
3. Design Review -> docs/design-review.md (and updates architecture.md)
4. Implementation Planning -> docs/impl-plan.md
5. 5. Implementation -> src/main/java, src/main/resources/static and src/test/java
6. Code Review -> docs/code-review.md
7. Verification -> docs/verification-report.md
8. Pull Request -> docs/pr-description.md, CHANGELOG.md and the GitHub PR

## Technology stack
- Backend: Java 17, Spring Boot (REST API), Maven
- Frontend: plain HTML, CSS and JavaScript in src/main/resources/static (no frameworks)
- Storage: in-memory
- Testing: JUnit 5 and Spring Boot Test (MockMvc)
- Work tracking: Jira via the Atlassian MCP tools
- Hook scripts: PowerShell scripts in .github/hooks/scripts/

## Rules for EVERY agent
- Read the previous stage's artifact before starting. If it is missing, STOP and tell the user which stage must run first.
- Never invent facts. If something is unknown, write "Not Found" or ask the user.
- Never commit secrets, tokens, passwords or .env files.
- Work only on the branch `feature/keeper`. Never commit directly to `main`.
- Human in the loop: after producing your artifact, show a short summary and ask the user to approve. Commit only after approval.
- Use conventional commit messages, for example `docs(requirements): add requirements.md`.
- After committing, push the branch and report the commit SHA.
- After finishing, update docs/pipeline-state.md with your stage status, commit SHA and timestamp.
- End every response by naming the next recommended stage.