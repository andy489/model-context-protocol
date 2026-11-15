# Lab: FastMCP Corporate Assistant

A hands-on lab where you'll build a production-ready MCP server for a **Corporate Assistant** application using **FastMCP**. By the end you'll understand the three core MCP primitives — Resources, Prompts, and Tools — and how FastMCP maps them directly onto Python functions.

---

## Background: What Is FastMCP?

**MCP (Model Context Protocol)** is a standardized wire protocol that lets an LLM client (like Claude, Cursor, or your own app) discover and call capabilities exposed by a server. Those capabilities come in three kinds:

| Primitive | What it is | When an LLM uses it |
|-----------|-----------|---------------------|
| **Resource** | Read-only data endpoint | "Give me the holiday list" |
| **Prompt** | Reusable text template | "Give me a welcome email template" |
| **Tool** | Callable action with side effects | "Send this email" |

The raw MCP protocol is JSON-RPC over stdin/stdout or HTTP — verbose and low-level. **FastMCP** wraps it in a Pythonic decorator API: you write a plain Python function, add one decorator, and FastMCP handles schema generation, serialization, validation, and routing automatically.

### What FastMCP does for you

When you write:

```python
@mcp.tool(description="Sends an email to a recipient.")
def send_email(recipient: str, subject: str, body: str):
    ...
```

FastMCP automatically:
- reads `recipient`, `subject`, `body` type annotations → generates a JSON schema the LLM sees
- reads the docstring → fills the description the LLM reads
- validates incoming arguments before your function runs
- converts exceptions into proper MCP error responses
- registers the function in the server's capability list

### How it looks without FastMCP

Without FastMCP you would implement the raw MCP JSON-RPC protocol manually. A minimal tools handler would look something like this:

```python
# Raw MCP over HTTP — what you'd write without FastMCP
from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse

app = FastAPI()

@app.post("/mcp")
async def handle(request: Request):
    body = await request.json()
    method = body.get("method")

    if method == "tools/list":
        return JSONResponse({"tools": [
            {
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
            }
        ]})

    if method == "tools/call":
        args = body["params"]["arguments"]
        if "@" not in args["recipient"]:
            return JSONResponse({"error": {"code": -32000, "message": "Invalid email"}})
        # ... actual logic
        return JSONResponse({"result": {"status": "sent"}})
```

With FastMCP you write **only** the business logic function. All of the schema, routing, and serialization above is generated for you.

---

## Project Structure

```
02-fastmcp-corporate-assistant/
├── app/
│   ├── data.py        # Shared in-memory data — provided, do not modify
│   ├── resources.py   # Exercise 1: define Resources
│   ├── prompts.py     # Exercise 2: define Prompts
│   ├── tools.py       # Exercise 3: define Tools
│   └── server.py      # Wires sub-servers together — provided, do not modify
├── solution/          # Completed reference implementation
├── client.py          # Test client — run this to verify your work
├── .env.example
└── requirements.txt
```

`app/server.py` and `app/data.py` are already complete. Your work is entirely in `resources.py`, `prompts.py`, and `tools.py`.

---

## How the Server Is Structured

Rather than one large file, this lab splits responsibilities across three **sub-servers**, each a standalone `FastMCP` instance:

```python
# resources.py
resources_server = FastMCP(name="CorporateResources")

# prompts.py
prompts_server = FastMCP(name="CorporatePrompts")

# tools.py
tools_server = FastMCP(name="CorporateTools")
```

`server.py` then imports and merges them into a single parent server at startup:

```python
mcp = FastMCP("Corporate Assistant")

async def setup():
    await mcp.import_server(resources_server)
    await mcp.import_server(tools_server)
    await mcp.import_server(prompts_server)
```

`import_server()` is FastMCP's **server composition** feature — it copies all tools, resources, and prompts from the child into the parent. After `setup()` the parent exposes everything as one unified MCP server. This pattern keeps each concern in its own file while presenting a single endpoint to clients.

---

## FastMCP Concepts in Depth

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

**What FastMCP generates automatically:**
- The resource name comes from the function name (`company_holidays`)
- The MIME type is inferred: lists/dicts → `application/json`, strings → `text/plain`
- The description comes from the decorator argument (or the docstring if omitted)

**Dynamic resources** use URI templates (RFC 6570). FastMCP extracts template variables and passes them as function arguments:

```python
@resources_server.resource("corporate://employees/{employee_id}")
def get_employee_details(employee_id: str):
    return employees.get(employee_id, {"error": "Employee not found"})
```

A request for `corporate://employees/U123` automatically calls `get_employee_details(employee_id="U123")`.

---

### Prompts

Prompts are reusable text templates. The LLM requests one by name (with optional arguments) and uses the returned text as context or a starting point.

```python
@prompts_server.prompt(description="Creates a formatted project status update.")
def project_status_update(project_name: str, progress: int):
    return f"""**Status Update**

Project: {project_name}
Current Progress: {progress}%

Next steps: [LLM should fill this in]"""
```

**What FastMCP generates automatically:**
- The prompt name comes from the function name (`project_status_update`)
- Parameters without defaults are **required**; those with defaults are optional
- Type annotations generate the JSON schema the client uses to validate arguments
- A `str` return is converted to a single user message; you can also return `list[Message]` for a conversation sequence

---

### Tools

Tools are callable actions. Unlike resources, they can have side effects (write to a database, send an email, call an API). The LLM invokes them by name with arguments.

```python
@tools_server.tool(description="Sends an email to a recipient.")
def send_email(recipient: str, subject: str, body: str):
    if "@" not in recipient:
        raise ValueError("Invalid email address: recipient must contain '@' symbol.")
    print(f"EMAIL SENT to {recipient}: {subject}")
    return {"status": "sent", "recipient": recipient, "subject": subject}
```

**What FastMCP generates automatically:**
- The tool name comes from the function name (`send_email`)
- Type annotations → JSON schema for the LLM
- Docstring → description (the decorator `description=` argument overrides it)
- Any raised exception is caught, logged, and sent back to the client as an MCP error response — you never need to manually serialize errors

**Return types:** strings become text content; dicts and Pydantic models become structured JSON content that the LLM can parse.

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

The MCP endpoint is at `http://localhost:8000/mcp`.

### 5. Run the test client (terminal 2)

```bash
# From the lab root
source .venv/bin/activate
python3 client.py
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

---

## The Exercises

Open `app/resources.py`, `app/prompts.py`, and `app/tools.py` and follow the `# TODO` comments. Use the concepts above to implement each one.

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

Edit `app/data.py` — the dynamic resource picks it up automatically, no other file changes needed:

```python
employees = {
    "U123": {"name": "Alice", "department": "Engineering"},
    "U789": {"name": "Carol", "department": "Design"},  # new
}
```

### Add a new resource / prompt / tool

Add a new decorated function in the corresponding file. `server.py` imports the sub-server object, so the new function is included at next startup — no changes to `server.py` needed.

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
cd solution
python3 server.py
```
