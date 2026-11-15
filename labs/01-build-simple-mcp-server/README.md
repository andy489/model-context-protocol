# Lab: Build a Simple MCP Server

A hands-on lab that walks you through building a minimal **Model Context Protocol (MCP)** server using **FastAPI** and **Pydantic**. The server exposes a clean `/mcp` endpoint that supports `discovery` (listing available tools) and `execute` (running a tool with arguments).

---

## Project Structure

```
01-build-simple-mcp-server/
├── app/
│   ├── __init__.py
│   ├── main.py        # FastAPI app and /mcp endpoint
│   ├── models.py      # Pydantic models for requests and responses
│   └── tools.py       # Tool implementations and definitions
├── solution/
│   └── app/           # Completed solution with all exercises
├── tests/
│   └── test_mcp.py    # pytest test suite
├── .env               # Environment variables (not committed in real projects)
├── .env.example       # Template for environment variables
├── requirements.txt
└── build-simple-mcp-server.ipynb   # Step-by-step lab notebook
```

---

## How It Works

This is a **teaching project** — it contains no LLM and no real weather API. The goal is to learn the *structure* of an MCP server.

```
Client (curl / LLM) → POST /mcp → FastAPI → tool function → response
```

The `/mcp` endpoint supports two verbs:

- **`discovery`** — the client asks "what tools do you have?" and the server returns a list of tool definitions (name, description, parameters). This is how an LLM learns what it can call.
- **`execute`** — the client says "run `get_weather` with `location=Berlin`" and the server dispatches to the matching Python function and returns the result.

The weather tool is intentionally fake:

```python
def get_weather(location: str) -> str:
    return f"The weather in {location} is sunny."  # always sunny
```

No API call is made — it always returns "sunny" for any city. In a real MCP server you would replace this with an actual weather API (e.g. OpenWeatherMap), but the protocol layer stays identical. The point is to understand the discovery/execute pattern, not to get real weather data.

---

## Quickstart

### 1. Clone and enter the lab directory

```bash
cd labs/01-build-simple-mcp-server
```

### 2. Create and activate a virtual environment

```bash
python3 -m venv .venv
source .venv/bin/activate      # macOS/Linux
# .venv\Scripts\activate       # Windows
```

> Once the venv is activated, `python` and `pip` inside it point to Python 3.

### 3. Install dependencies

```bash
pip3 install -r requirements.txt
```

### 4. Copy the environment file

```bash
cp .env.example .env
```

### 5. Start the server

```bash
uvicorn app.main:app --reload --host 127.0.0.1 --port 8000
```

The server is now running at `http://127.0.0.1:8000`.

---

## Using the API

### Discover available tools

```bash
curl -s -X POST http://127.0.0.1:8000/mcp \
  -H "Content-Type: application/json" \
  -d '{"verb": "discovery"}' | python -m json.tool
```

**Response:**
```json
{
  "tools": [
    {
      "name": "get_weather",
      "description": "Gets the current weather for a specified location.",
      "parameters": [
        { "name": "location", "type": "string" }
      ]
    }
  ],
  "result": null
}
```

### Execute a tool

```bash
curl -s -X POST http://127.0.0.1:8000/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "verb": "execute",
    "tool_name": "get_weather",
    "arguments": { "location": "Berlin" }
  }' | python -m json.tool
```

**Response:**
```json
{
  "tools": null,
  "result": "The weather in Berlin is sunny."
}
```

### Interactive API docs

FastAPI generates interactive docs automatically:

- **Swagger UI:** `http://127.0.0.1:8000/docs`
- **ReDoc:** `http://127.0.0.1:8000/redoc`

---

## Running the Tests

```bash
pytest tests/ -v
```

Expected output:
```
tests/test_mcp.py::test_discovery_returns_tools              PASSED
tests/test_mcp.py::test_discovery_tool_has_parameters        PASSED
tests/test_mcp.py::test_execute_get_weather                  PASSED
tests/test_mcp.py::test_execute_unknown_tool_returns_404     PASSED
tests/test_mcp.py::test_unknown_verb_returns_400             PASSED
tests/test_mcp.py::test_execute_without_arguments_uses_empty_dict PASSED
```

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `APP_HOST` | `127.0.0.1` | Host the server binds to |
| `APP_PORT` | `8000` | Port the server listens on |
| `APP_ENV` | `development` | Runtime environment |

---

## Lab Notebook

Open `build-simple-mcp-server.ipynb` in Jupyter to follow the step-by-step walkthrough:

```bash
jupyter notebook build-simple-mcp-server.ipynb
```

---

## Solution

The `solution/` directory contains the completed version of the server with all exercises implemented:

```bash
uvicorn solution.app.main:app --reload --port 8001
```
