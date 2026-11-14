# MCP Advanced Features

Advanced patterns for building production-ready MCP servers and clients: server-side utilities (filters, completions, notifications), stateful client features (roots, elicitations, sampling), custom Python MCP clients, and agentic workflows on Cloudflare Workers.

All four modules are consolidated into a single notebook:

## Notebook

**[mcp-advanced-features.ipynb](mcp-advanced-features.ipynb)** — covers all modules end-to-end.

## Modules

| # | Title | Topics |
|---|---|---|
| 01 | Advanced Server Features | Filters/middleware, completions, list-change and progress notifications |
| 02 | Advanced Client Features | Stateless vs. stateful, roots, elicitations, sampling |
| 03 | Custom MCP Clients | FastMCP `Client`, tools, resources, prompts, Anthropic sampling handler |
| 04 | Agentic Workflows with MCP | Cloudflare Agents SDK, Code Mode, OpenAPI-to-MCP, durable workflows |

## Demo Files

Demo zip archives are in [`01-advanced-server-demos/`](01-advanced-server-demos/). Each zip contains a self-contained project:

| File | Module | Description |
|---|---|---|
| `Module-1-filters.zip` | 01 | TypeScript function composition and middleware pipeline |
| `Module-1-completions.zip` | 01 | Color name autocompletion with `completable` |
| `Module-1-notifications.zip` | 01 | List change, resource subscriptions, and progress notifications |
| `Module-2-roots.zip` | 02 | Filesystem scanning with declared roots |
| `Module-2-elicitation.zip` | 02 | Form-based and URL-based elicitation |
| `Module-2-sampling.zip` | 02 | Trip planner with and without tool use |
| `Module-2-stateful.zip` | 02 | Cloudflare Agents SDK stateful HTTP server |
| `Module-2-stateless.zip` | 02 | Stateless HTTP MCP server comparison |
| `Module-3-basic-client.zip` | 03 | FastMCP custom client: ping, tools, resources, prompts |
| `Module-3-sampling.zip` | 03 | Sampling client with Anthropic handler |
| `Module-4-workflow.zip` | 04 | Travel agent with TripAdvisor MCP server and durable workflow |

## Assets

Educational diagrams extracted from slides are in [`assets/`](assets/):

| File | Description |
|---|---|
| `01-filter-request-pipeline.png` | MCP request pipeline without filters |
| `02-filter-request-pipeline-with-filter.png` | MCP request pipeline with filter injected |
| `03-stateful-http-architecture.png` | Stateful HTTP with proxy/gateway pattern |
| `04-travel-agent-architecture.png` | Full travel agent architecture on Cloudflare Workers |
| `05-api-wrapping-flow.png` | Naïve API-to-MCP tool mapping |
| `06-cloudflare-code-mode-flow.png` | Cloudflare Code Mode — `search` + `execute` tools |
| `07-code-mode-with-rpc.png` | Code Mode with RPC bindings to executor sandbox |

## Technologies

- Node.js LTS, TypeScript
- FastMCP (Python)
- Python 3
- Cloudflare Workers, Agents SDK, Durable Objects, Workflows
