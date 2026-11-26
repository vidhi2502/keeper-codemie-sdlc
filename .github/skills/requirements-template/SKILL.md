---
name: requirements-template
description: Template and rules for writing requirements.md from a user story, including clarifying Q&A, functional and non-functional requirements, acceptance criteria and assumptions.
---
# requirements.md structure

Write docs/requirements.md with exactly these sections:

1. **User Story** - verbatim text and its source (JIRA key, Confluence link or file name)
2. **Clarifying Q&A** - table of Question | User's Answer
3. **Functional Requirements** - IDs FR-1, FR-2, ... each one testable
4. **Non-Functional Requirements** - IDs NFR-1, ... covering security, reliability, performance, maintainability
5. **Acceptance Criteria** - Given / When / Then, each linked to requirement IDs
6. **Assumptions, Out of Scope, Open Questions**

## Rules
- Every requirement has a unique ID.
- No vague words such as "fast" or "user-friendly" without a measurable target.
- Anything the user did not confirm goes under Assumptions or Open Questions. Never invent it.