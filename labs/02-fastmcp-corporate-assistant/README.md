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
Client (client.py / LLM) → HTTP → FastMCP server → resource / prompt / tool → response
```

The server is composed of three sub-servers that get merged at startup:

| File | Purpose |
|------|---------|
| `resources.py` | Static and dynamic data endpoints |
| `prompts.py` | Reusable text templates for LLMs |
| `tools.py` | Callable actions (with validation) |
| `server.py` | Wires them all together and starts HTTP server |
| `data.py` | Shared in-memory data (employees dict) |
| `client.py` | Test client that exercises all features |

---

## Project Structure

```
02-fastmcp-corporate-assistant/
├── app/
│   ├── data.py        # Shared in-memory data
│   ├── resources.py   # TODO: define resources
│   ├── prompts.py     # TODO: define prompts
│   ├── tools.py       # TODO: define tools
│   └── server.py      # Wires sub-servers together
├── solution/          # Completed reference implementation
├── client.py          # Test client
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

### 4. Start the server

```bash
cd app
python3 server.py
```

The server runs at `http://localhost:8000`. FastMCP exposes the MCP endpoint at `http://localhost:8000/mcp`.

### 5. Run the test client (in a second terminal)

```bash
source .venv/bin/activate
python3 client.py
```

---

## The Exercises

Open `app/resources.py`, `app/prompts.py`, and `app/tools.py`. Each file contains `# TODO` comments guiding you through what to implement. `app/server.py` and `app/data.py` are already complete — do not modify them.

### Exercise 1 — Resources (`resources.py`)

- Define a **static resource** at `corporate://holidays/2025` returning a list of holiday dates
- Define a **dynamic resource** at `corporate://employees/{employee_id}` that looks up an employee by ID

### Exercise 2 — Prompts (`prompts.py`)

- Define a `generate_welcome_email` prompt returning a new-hire email template
- Define a `project_status_update` prompt accepting `project_name` and `progress` parameters

### Exercise 3 — Tools (`tools.py`)

- Define a `list_meeting_rooms` tool returning a list of room names
- Define a `send_email` tool that validates the recipient address and returns a status dict

---

## Solution

The `solution/` directory contains the completed implementation:

```bash
cd solution
python3 server.py
```
