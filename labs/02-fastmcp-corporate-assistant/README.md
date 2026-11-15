# Lab: FastMCP Corporate Assistant

A hands-on lab where you'll build a production-ready MCP server for a **Corporate Assistant** application using **FastMCP**. You'll learn how to expose data, reusable prompts, and AI-callable tools through the Model Context Protocol.

---

## What You'll Learn

- Structure an MCP server using FastMCP
- Expose data via **Resources** (static and dynamic)
- Provide reusable **Prompts**
- Enable AI actions through **Tools** with proper error handling

## What You'll Build

A Corporate Assistant that helps with common workplace tasks:

- Checking company holidays and employee information (Resources)
- Generating standardized email templates (Prompts)
- Listing meeting rooms and sending emails (Tools)

---

## How It Works

This project uses no LLM and no real external APIs — all data is hardcoded to keep the focus on the MCP server structure.

```
Client (client.py / LLM) → HTTP POST /mcp → FastMCP server → resource / prompt / tool → response
```

The server is split into three focused sub-servers that get merged at startup via `mcp.import_server()`:

| File | Role | MCP concept |
|------|------|-------------|
| `resources.py` | Read-only data endpoints (static + dynamic URI templates) | Resources |
| `prompts.py` | Reusable text templates an LLM can request | Prompts |
| `tools.py` | Callable actions with validated inputs | Tools |
| `server.py` | Wires all sub-servers together and starts HTTP | Entry point |
| `data.py` | Shared in-memory data (employees dict) | — |
| `client.py` | Test client that exercises all three MCP concepts | — |

**Resources** are pulled by the client on demand (e.g. "give me the holiday list"). **Prompts** return text templates that an LLM uses as a starting point. **Tools** are functions the LLM can invoke to take an action — they can fail with errors, which the client must handle.

---

## Project Structure

```
02-fastmcp-corporate-assistant/
├── app/
│   ├── data.py        # Shared in-memory data — provided, do not modify
│   ├── resources.py   # TODO: define resources
│   ├── prompts.py     # TODO: define prompts
│   ├── tools.py       # TODO: define tools
│   └── server.py      # Wires sub-servers together — provided, do not modify
├── solution/          # Completed reference implementation
├── client.py          # Test client — run this to verify your implementation
├── .env.example
└── requirements.txt
```

---

## Quickstart

### 1. Create and activate a virtual environment

```bash
python3 -m venv .venv
source .venv/bin/activate
```

### 2. Install dependencies

```bash
pip3 install -r requirements.txt
```

### 3. Copy the environment file

```bash
cp .env.example .env
```

### 4. Start the server (terminal 1)

```bash
cd app
python3 server.py
```

The server starts at `http://localhost:8000`. FastMCP exposes the MCP endpoint at `http://localhost:8000/mcp`.

### 5. Run the test client (terminal 2)

```bash
# From the lab root directory
source .venv/bin/activate
python3 client.py
```

Expected output when all exercises are complete:

```
Connected to Corporate Assistant MCP server!

=== Testing Resources ===
Available resources: ['company_holidays', 'get_employee_details']

=== Testing Prompts ===
Available prompts: ['generate_welcome_email', 'project_status_update']

=== Testing Tools ===
Available tools: ['list_meeting_rooms', 'send_email']
...
=== Testing Error Handling ===
Correctly caught error for invalid email: ...

All tests completed successfully!
```

---

## The Exercises

Open `app/resources.py`, `app/prompts.py`, and `app/tools.py` and follow the `# TODO` comments. `app/server.py` and `app/data.py` are already wired up — do not modify them.

### Exercise 1 — Resources (`app/resources.py`)

Define two resources on `resources_server`:

1. **Static resource** at URI `corporate://holidays/2025` — returns a list of holiday date strings
2. **Dynamic resource** at URI `corporate://employees/{employee_id}` — looks up `employee_id` in the `employees` dict from `data.py` and returns their details, or `{"error": "Employee not found"}` if missing

```python
@resources_server.resource("corporate://holidays/2025", description="...")
def company_holidays():
    return [...]

@resources_server.resource("corporate://employees/{employee_id}", description="...")
def get_employee_details(employee_id: str):
    return employees.get(employee_id, {"error": "Employee not found"})
```

### Exercise 2 — Prompts (`app/prompts.py`)

Define two prompts on `prompts_server`:

1. **`generate_welcome_email`** — no parameters, returns a new-hire welcome email template with `{new_hire_name}` and `{start_date}` placeholders
2. **`project_status_update`** — accepts `project_name: str` and `progress: int`, returns a formatted status update string

### Exercise 3 — Tools (`app/tools.py`)

Define two tools on `tools_server`:

1. **`list_meeting_rooms`** — no parameters, returns a list of room name strings
2. **`send_email`** — accepts `recipient: str`, `subject: str`, `body: str`; raises `ValueError` if `recipient` has no `@`; returns `{"status": "sent", "recipient": ..., "subject": ...}`

---

## Extending the Lab

The project is designed to be easy to extend. Here are the touch points for common changes:

### Add a new employee

Edit `app/data.py`:

```python
employees = {
    "U123": {"name": "Alice", "department": "Engineering"},
    "U456": {"name": "Bob", "department": "Marketing"},
    "U789": {"name": "Carol", "department": "Design"},   # new
}
```

No other files need to change — the dynamic resource picks it up automatically.

### Add a new resource

In `app/resources.py`, add a new function decorated with `@resources_server.resource(...)`. The URI can be static (`corporate://offices/berlin`) or dynamic (`corporate://offices/{city}`). No changes to `server.py` needed.

### Add a new prompt

In `app/prompts.py`, add a new function decorated with `@prompts_server.prompt(...)`. Parameters become the prompt's arguments automatically via type hints.

### Add a new tool

In `app/tools.py`, add a new function decorated with `@tools_server.tool(...)`. Raise standard Python exceptions for validation errors — FastMCP surfaces them to the client automatically.

### Change the port

Edit `app/server.py`:

```python
mcp.run(transport="http", port=9000)   # change port here
```

Then update `client.py` to match:

```python
client = Client("http://localhost:9000/mcp")
```

---

## Solution

The `solution/` directory contains the completed implementation. To run it:

```bash
cd solution
python3 server.py
```
