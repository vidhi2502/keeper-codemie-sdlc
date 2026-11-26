---
name: run-pipeline
description: Run the full agentic SDLC pipeline stage by stage
disable-model-invocation: true
---
Run the pipeline from the first stage that is not Approved. Call each agent as a subagent, show me a summary after each stage, and wait for my approval before continuing.