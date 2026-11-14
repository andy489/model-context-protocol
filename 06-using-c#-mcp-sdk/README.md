# Using the C# Model Context Protocol SDK

A fast-paced, demo-focused course on building, securing, and deploying MCP servers with the C# SDK. Covers remote (Streamable HTTP) and local (STDIO) transports, OAuth security with ASP.NET Core, consuming MCP from AI agents, and publishing developer-targeted servers to NuGet.org.

All five modules are consolidated into a single notebook: [`using-csharp-mcp-sdk.ipynb`](using-csharp-mcp-sdk.ipynb)

## Modules

| # | Module | Topic |
|---|---|---|
| 01 | Getting Started with the MCP SDK | SDK packages, MCP feature primitives, CarvedRock demo app |
| 02 | Creating an MCP Server for APIs | Tool registration strategies, tool logic design, automated tests |
| 03 | Security Considerations for MCP Servers | OAuth, four security scenarios, role-based access, token forwarding |
| 04 | Using an MCP Server from AI Agents | Agent setup, slash commands via MCP Prompts, token exchange, elicitation |
| 05 | Creating Developer-Targeted MCP Servers and Deploying to NuGet.org | Templates, IDE config, server metadata, packing and publishing |

## Demo Projects

| Directory | Description |
|---|---|
| [`01-getting-started-demos/`](01-getting-started-demos/) | Hello-world MCP server (STDIO + Streamable HTTP) and CarvedRock baseline application |
| [`02-api-server-demos/`](02-api-server-demos/) | CarvedRock MCP server with product tools and automated tests (begin + end states) |
| [`03-security-demos/`](03-security-demos/) | OAuth-secured MCP server with role-based access and structured logging (begin + end states) |
| [`04-agent-demos/`](04-agent-demos/) | AI agent consuming the secured MCP server, with token exchange and elicitation (begin + end states) |
| [`05-developer-server-demos/`](05-developer-server-demos/) | Developer-targeted MCP server ready for NuGet publishing, and final CarvedRock application state |

## Setup

The demos are .NET / Aspire projects. Requirements:

- .NET SDK
- Docker Desktop (or Rancher / Podman) for Aspire container orchestration
- An IDE: Visual Studio, VS Code, or Rider
