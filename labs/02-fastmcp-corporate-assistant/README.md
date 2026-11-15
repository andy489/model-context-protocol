# Lab: FastMCP Corporate Assistant

A hands-on lab where you'll build a production-ready MCP server for a **Corporate Assistant** application using **FastMCP**. By the end you'll understand the three core MCP primitives — Resources, Prompts, and Tools — and how FastMCP maps them directly onto Python functions.

---

## What Problem Does This Solve?

LLMs like Claude are powerful reasoners, but they are isolated — they can't read your company's employee database, send emails, or look up holiday calendars without being explicitly given that capability.

**MCP (Model Context Protocol)** is the standard that bridges this gap. It defines a protocol through which an LLM client can:
1. **Discover** what capabilities a server exposes
2. **Read** data from it (Resources)
3. **Use** reusable templates (Prompts)
4. **Call** actions on it (Tools)

Without MCP, every LLM integration is custom: a different API, a different schema format, a different way to handle errors. With MCP, any compliant client (Claude Desktop, Cursor, your own app) can connect to any compliant server and immediately understand what it can do.

This lab builds a **Corporate Assistant** MCP server — a fake company backend that exposes employee data, email tools, and document templates. The domain is illustrative; the patterns apply to any real backend.

---

## What Is FastMCP?

The raw MCP protocol is JSON-RPC over HTTP — verbose and low-level. **FastMCP** wraps it in a Pythonic decorator API: you write a plain Python function, add one decorator, and FastMCP handles schema generation, serialization, validation, and routing automatically.

### With FastMCP

```python
@mcp.tool(description="Sends an email to a recipient.")
def send_email(recipient: str, subject: str, body: str):
    if "@" not in recipient:
        raise ValueError("Invalid email address.")
    return {"status": "sent", "recipient": recipient}
```

FastMCP automatically:
- reads type annotations → generates the JSON schema the LLM sees
- validates incoming arguments before your function runs
- converts exceptions into proper MCP error responses
- registers the function in the server's capability list

### Without FastMCP

You would hand-write all of this yourself:

```python
# Raw MCP over HTTP — without FastMCP
from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse

app = FastAPI()

@app.post("/mcp")
async def handle(request: Request):
    body = await request.json()
    method = body.get("method")

    if method == "tools/list":
        return JSONResponse({"tools": [{
            "name": "send_email",
            "description": "Sends an email to a recipient.",
            "inputSchema": {
                "type": "object",
                "properties": {
                    "recipient": {"type": "string"},
                    "subject":   {"type": "string"},
                    "body":      {"type": "string"},
                },
                "required": ["recipient", "subject", "body"],
            },
        }]})

    if method == "tools/call":
        args = body["params"]["arguments"]
        if "@" not in args["recipient"]:
            return JSONResponse({"error": {"code": -32000, "message": "Invalid email"}})
        return JSONResponse({"result": {"status": "sent"}})
```

With FastMCP you write **only** the business logic. Everything else is generated.

---

## The Three MCP Primitives

| Primitive | What it is | When an LLM uses it |
|-----------|-----------|---------------------|
| **Resource** | Read-only data endpoint addressed by a URI | "Give me the holiday list for 2025" |
| **Prompt** | Reusable text template, optionally parameterized | "Give me a welcome email template" |
| **Tool** | Callable action that can have side effects | "Send this email to Alice" |

### Resources

Resources are lazy read-only data endpoints. The client requests a URI; FastMCP calls your function and returns the result.

```python
@resources_server.resource(
    "corporate://holidays/2025",
    description="Official company holidays for 2025.",
)
def company_holidays():
    return ["2025-01-01", "2025-07-04", "2025-12-25"]
```

**Dynamic resources** use URI templates — FastMCP extracts variables and passes them as arguments:

```python
@resources_server.resource("corporate://employees/{employee_id}")
def get_employee_details(employee_id: str):
    return employees.get(employee_id, {"error": "Employee not found"})
```

A request for `corporate://employees/U123` automatically calls `get_employee_details(employee_id="U123")`.

### Prompts

Prompts are reusable text templates. The LLM requests one by name and uses the returned text as a starting point for its response.

```python
@prompts_server.prompt(description="Creates a formatted project status update.")
def project_status_update(project_name: str, progress: int):
    return f"**{project_name}** — {progress}% complete"
```

Parameters without defaults are required; those with defaults are optional. A `str` return becomes a single user message; you can return `list[Message]` for a multi-turn conversation sequence.

### Tools

Tools are callable actions that can have side effects — write to a database, send an email, call an external API.

```python
@tools_server.tool(description="Sends an email to a recipient.")
def send_email(recipient: str, subject: str, body: str):
    if "@" not in recipient:
        raise ValueError("Invalid email address: recipient must contain '@'.")
    return {"status": "sent", "recipient": recipient, "subject": subject}
```

Any raised exception is caught and sent back to the client as a structured MCP error — you never manually serialize errors.

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                   LLM Client                        │
│         (Claude Desktop / Cursor / your app)        │
└───────────────────────┬─────────────────────────────┘
                        │  MCP protocol (JSON-RPC over HTTP)
                        ▼
┌─────────────────────────────────────────────────────┐
│              Corporate Assistant MCP Server         │
│                   app/server.py                     │
│                                                     │
│  ┌─────────────────┐  ┌──────────────────────────┐  │
│  │  resources.py   │  │       prompts.py         │  │
│  │                 │  │                          │  │
│  │ company_holidays│  │ generate_welcome_email   │  │
│  │ get_employee    │  │ project_status_update    │  │
│  │   _details      │  │                          │  │
│  └─────────────────┘  └──────────────────────────┘  │
│                                                     │
│  ┌─────────────────┐  ┌──────────────────────────┐  │
│  │    tools.py     │  │        data.py           │  │
│  │                 │  │                          │  │
│  │ list_meeting    │  │  employees = {           │  │
│  │   _rooms        │  │    "U123": {...},        │  │
│  │ send_email      │  │    "U456": {...},        │  │
│  │                 │  │  }                       │  │
│  └─────────────────┘  └──────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

The server is split into **sub-servers** — each file owns one concern:

```python
# Each file defines its own FastMCP instance
resources_server = FastMCP(name="CorporateResources")
prompts_server   = FastMCP(name="CorporatePrompts")
tools_server     = FastMCP(name="CorporateTools")
```

`server.py` composes them into one parent server using `mount()`:

```python
mcp = FastMCP("Corporate Assistant")
mcp.mount(resources_server)
mcp.mount(tools_server)
mcp.mount(prompts_server)
```

`mount()` copies all capabilities from each sub-server into the parent. The result is a single endpoint at `http://localhost:8000/mcp` that exposes everything. This pattern keeps each concern isolated while presenting a unified API to clients.

---

## Project Structure

```
02-fastmcp-corporate-assistant/
├── app/
│   ├── data.py        # Shared in-memory data — do not modify
│   ├── resources.py   # Exercise 1: define Resources
│   ├── prompts.py     # Exercise 2: define Prompts
│   ├── tools.py       # Exercise 3: define Tools
│   └── server.py      # Wires sub-servers together — do not modify
├── solution/          # Completed reference implementation
├── client.py          # Scripted test client
├── .env.example
└── requirements.txt
```

---

## Quickstart

Choose either `uv` (recommended on macOS) or the classic `venv` approach.

### Option A — uv (recommended on macOS)

[uv](https://docs.astral.sh/uv/) handles virtual environments automatically. Install it once:

```bash
curl -LsSf https://astral.sh/uv/install.sh | sh
```

After installation, restart your terminal or run `source ~/.zshrc` to make `uv` available.

**1. Install dependencies**

```bash
uv pip install -r requirements.txt
```

**2. Copy the environment file**

```bash
cp .env.example .env
```

**3. Start the server (terminal 1)**

```bash
uv run python app/server.py
```

The MCP endpoint is at `http://localhost:8000/mcp`.

**4. Run the test client (terminal 2)**

```bash
uv run python client.py
```

---

### Option B — venv + pip

**1. Create and activate a virtual environment**

```bash
python3 -m venv .venv
source .venv/bin/activate
```

> You must run `source .venv/bin/activate` in every new terminal before running any Python commands.

**2. Install dependencies**

```bash
pip install -r requirements.txt
```

**3. Copy the environment file**

```bash
cp .env.example .env
```

**4. Start the server (terminal 1)**

```bash
python3 app/server.py
```

The MCP endpoint is at `http://localhost:8000/mcp`.

**5. Run the test client (terminal 2)**

```bash
source .venv/bin/activate   # if this is a new terminal
python3 client.py
```

---

## Testing Options

There are four ways to interact with the running server.

### 1. Scripted test client (quickest verification)

Runs a fixed sequence of calls covering all primitives and error handling:

```bash
python3 client.py          # venv
uv run python client.py    # uv
```

Expected output when all exercises are complete:

```
Connected to Corporate Assistant MCP server!

=== Testing Resources ===
Available resources: ['company_holidays', 'get_employee_details']
...
=== Testing Prompts ===
Available prompts: ['generate_welcome_email', 'project_status_update']
...
=== Testing Tools ===
Available tools: ['list_meeting_rooms', 'send_email']
...
=== Testing Error Handling ===
Correctly caught error for invalid email: ...

All tests completed successfully!
```

### 2. MCP Inspector — browser UI (best for exploration)

A visual interface where you can browse all capabilities and invoke them with a form — no JSON required:

```bash
npx @modelcontextprotocol/inspector http://localhost:8000/mcp
```

Opens in your browser automatically.

### 3. Python one-liner (quick ad-hoc calls)

```bash
python3 -c "
import asyncio
from fastmcp import Client

async def main():
    async with Client('http://localhost:8000/mcp') as c:
        tools = await c.list_tools()
        print([t.name for t in tools])
        result = await c.call_tool('list_meeting_rooms', {})
        print(result)

asyncio.run(main())
"
```

> **Note:** Raw `curl` does not work with this server. FastMCP 4.x uses the Streamable HTTP transport which requires a session handshake — single-shot curl requests return `Missing session ID`. Use the Inspector or the Python client instead.

### 4. Claude Desktop (the real use case)

Add the server to Claude Desktop's MCP config and Claude will discover and call your tools automatically during conversation. This is what the MCP protocol is ultimately designed for.

---

## The Exercises

Open `app/resources.py`, `app/prompts.py`, and `app/tools.py` and follow the `# TODO` comments.

### Exercise 1 — Resources (`app/resources.py`)

1. Static resource at `corporate://holidays/2025` — returns a list of holiday date strings
2. Dynamic resource at `corporate://employees/{employee_id}` — looks up the employee in `employees` from `data.py`, returns `{"error": "Employee not found"}` if missing

### Exercise 2 — Prompts (`app/prompts.py`)

1. `generate_welcome_email` — no parameters, returns a new-hire welcome email template with `{new_hire_name}` and `{start_date}` placeholders
2. `project_status_update(project_name: str, progress: int)` — returns a formatted status update string

### Exercise 3 — Tools (`app/tools.py`)

1. `list_meeting_rooms` — no parameters, returns a list of room name strings
2. `send_email(recipient, subject, body)` — raises `ValueError` if `recipient` has no `@`; returns `{"status": "sent", "recipient": ..., "subject": ...}`

---

## Extending the Lab

### Add a new employee

Edit `app/data.py` — the dynamic resource picks it up automatically:

```python
employees = {
    "U123": {"name": "Alice", "department": "Engineering"},
    "U789": {"name": "Carol", "department": "Design"},  # new
}
```

### Add a new resource / prompt / tool

Add a decorated function in the corresponding file. `server.py` mounts the sub-server object, so the new function is included at next startup — no changes to `server.py` needed.

### Change the port

In `app/server.py`:
```python
mcp.run(transport="http", port=9000)
```

In `client.py`:
```python
client = Client("http://localhost:9000/mcp")
```

---

## Solution

The `solution/` directory contains the completed implementation:

```bash
# uv
uv run python solution/server.py

# venv
source .venv/bin/activate
python3 solution/server.py
```
