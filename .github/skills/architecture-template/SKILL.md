---
name: architecture-template
description: Template for architecture.md and design-review.md including Mermaid component diagram, data flow, technology choices, risks and design decisions.
---
# architecture.md structure

1. **Context and Goals** - link each goal to requirement IDs
2. **Component Diagram** - Mermaid `flowchart`
3. **Components and Responsibilities** - table: Component | Responsibility | Requirement IDs
4. **Data Flow** - Mermaid `sequenceDiagram`
5. **Technology Choices** - choice, rationale, alternatives rejected (Java 17, Maven, JUnit 5 are fixed)
6. **Error Handling and Security Approach**

# design-review.md structure

1. **Findings table** - ID | Area | Severity (High/Medium/Low) | Finding | Decision | Status
2. **Agreed Design Decisions**
3. **Changes made to architecture.md** - list each change and the finding ID that caused it

Review areas: requirements coverage, security, error handling, testability, single points of failure, over-engineering.