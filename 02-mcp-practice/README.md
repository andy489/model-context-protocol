# MCP in Practice

A hands-on guide to building, distributing, and securing MCP (Model Context Protocol) servers using TypeScript and C#. The course builds a complete time-off management MCP server for the fictional Globomantics HR system, covering Tools, Resources, Prompts, remote deployment, and OAuth 2.1 authentication.

All four modules are consolidated into a single notebook: [`mcp-in-practice.ipynb`](mcp-in-practice.ipynb)

## Modules

| # | Title | Topics |
|---|---|---|
| 01 | Getting Started with MCP | Local environment setup, scaffolding, MCP Inspector |
| 02 | Building MCP Servers | Tools, Resources, Prompts, context engineering, distribution |
| 03 | Remote MCP Servers | HTTP transport, hosting strategies, Azure deployment |
| 04 | Securing MCP | OAuth 2.1, PRM, JWT validation, auth context in handlers |

## Demos

See [`01-getting-started-demos/`](01-getting-started-demos/) for the complete project collection used throughout the course:

- **`m1-empty/`** — bare-bones C# MCP server scaffold
- **`m1-inspector/`** — server configured for the MCP Inspector
- **`m2-stdio/`** — STDIO transport demo
- **`m2-token-counting/`** — Anthropic token counting in TypeScript
- **`everything-demo/`** — full Globomantics solution: HRM API + MCP Server with OAuth (.NET Aspire)

## Assets

Diagram images extracted from the course slides are stored in [`assets/`](assets/).

## Setup

The course uses:
- **Node.js 22** — TypeScript MCP SDK
- **.NET 8 / .NET 10** — C# MCP SDK
- **Claude Desktop** or **VS Code with GitHub Copilot** — MCP client

To run the notebook: open `mcp-in-practice.ipynb` in JupyterLab or VS Code with the Jupyter extension.
