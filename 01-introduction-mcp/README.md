# Introduction to Model Context Protocol (MCP)

A practical introduction to MCP — the open standard that connects AI applications to external tools and data sources. Covers core concepts, architecture, the advantages over existing integration approaches, and real-world usage patterns.

All three modules are consolidated into a single notebook: [`introduction-to-mcp.ipynb`](introduction-to-mcp.ipynb)

## Modules

| Module | Topic |
|---|---|
| 01 | Understanding the Model Context Protocol — definitions, participants (host/client/server), primitives (tools/resources/prompts), and data transfer (JSON-RPC, STDIO/HTTP) |
| 02 | Advantages of MCP — the integration problem, limitations of alternatives, and the five benefits MCP provides |
| 03 | MCP in Action — use cases, the ecosystem, the request-response lifecycle (initialization/operation/shutdown), and context management |

## Assets

The [`assets/`](assets/) directory contains 7 educational diagrams extracted from the course slides:

- `01-mcp-participants-overview.png` — Host, Client, and Server roles
- `02-angular-cli-mcp-example.png` — Concrete host-to-server connection example
- `03-mcp-primitives-all-three.png` — Prompts, Resources, and Tools provided by a server
- `04-mcp-json-rpc-data-layer.png` — JSON-RPC as the data layer
- `05-mcp-json-payload-example.png` — Example `tools/list` JSON-RPC payload
- `06-mcp-build-once-use-everywhere.png` — Multiple data sources connecting via MCP
- `07-mcp-operation-request-response.png` — Full operation phase: user → host → server → response

## Setup

The notebook uses only Python standard library and `IPython.display` for rendering diagrams. No additional dependencies are required beyond a standard Jupyter environment.
