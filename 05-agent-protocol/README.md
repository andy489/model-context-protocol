# Agent-to-Agent (A2A) Protocol

An open standard for enabling autonomous AI agents built on different frameworks to communicate, discover each other, and collaborate on tasks. This course covers A2A architecture and core concepts, building agents across frameworks, orchestration patterns, and production deployment.

All four modules are consolidated into a single notebook: [`agent-protocol.ipynb`](agent-protocol.ipynb)

## Modules

| # | Topic |
|---|-------|
| 01 | A2A Protocol Architecture and Core Concepts |
| 02 | Building A2A Agents Across Frameworks |
| 03 | Orchestration Patterns and Multi-Agent Workflows |
| 04 | Security, Testing, and Production Deployment |

## Demo Code

[`01-a2a-agent-demos/`](01-a2a-agent-demos/) — full source code organized by module and clip, covering all four modules. Contains begin/end states for each demo.

**Setup:**
```bash
cd 01-a2a-agent-demos
pip install -r requirements.txt
```

Requires an `OPENAI_API_KEY` environment variable (or `.env` file).

## Diagrams

[`assets/`](assets/) — 6 educational diagrams extracted from course slides:

| File | Content |
|------|---------|
| `01-multi-agent-scenario.png` | The multi-framework agent integration problem |
| `02-agent-card-discovery-flow.png` | How A2A capability discovery works |
| `03-task-lifecycle.png` | A2A task state machine |
| `04-unified-a2a-client.png` | Unified client connecting to three agents |
| `05-orchestrator-worker-pattern.png` | The orchestrator-worker pattern |
| `06-hierarchical-delegation.png` | Hierarchical agent delegation flow |

## Resources

- [`04/security-testing-and-production-deployment-resources/`](04/security-testing-and-production-deployment-resources/) — A2A Inspector setup commands and Agent Stack quickstart link
