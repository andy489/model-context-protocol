# FastMCP Foundations

A practical introduction to building MCP servers with FastMCP — covering the protocol's architecture, core Python primitives, structured message handling, middleware patterns, observability, and production deployment.

All three modules are consolidated into a single Jupyter notebook.

## Notebook

| File | Description |
|---|---|
| [`fastmcp-foundations.ipynb`](fastmcp-foundations.ipynb) | All modules in one notebook |

## Modules

| # | Title | Topics |
|---|---|---|
| 01 | Introduction to FastMCP | MCP background, MxN problem, architecture, pros/cons, ecosystem, alternatives, real-world use cases |
| 02 | FastMCP Essentials | Decorators, connecting to clients (VS Code, Claude Desktop, ChatGPT), building servers, tools vs resources, chaining |
| 03 | Metrics and Observability | Structured message handling, middleware, plugins, state, integrations, typed config, observability, security, production |

## Demo Code

[`fastmcp-course-demos/`](fastmcp-course-demos/) contains all example MCP servers:

| File | Description |
|---|---|
| `time-mcp.py` | Minimal server exposing current UTC time |
| `password_server.py` | Cryptographically secure password generator with entropy reporting |
| `weather_server.py` | Simulated weather server with current conditions and forecasts |
| `btc_price_server.py` | Live Bitcoin price via CoinMarketCap API |
| `btc_trend_server.py` | Bitcoin price trend via chained tool calls |
| `mcp_docs_server.py` | Document and knowledge base server using URI resources |
| `middleware_server.py` | Logging middleware demonstration |
| `weather_plugins.py` | Weather tools packaged as a plugin |
| `sentiment_plugin.py` | Sentiment analysis plugin |
| `translate_plugin.py` | Foreign language translation plugin |

## Assets

[`assets/`](assets/) contains 10 educational diagrams extracted from the course slides.

## Setup

```bash
pip install fastmcp pymupdf pillow
```

The demo servers have their own dependencies listed in [`fastmcp-course-demos/requirements.txt`](fastmcp-course-demos/requirements.txt).
