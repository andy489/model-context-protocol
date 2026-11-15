from fastapi import FastAPI, HTTPException

from app.models import ModelContextRequest, ModelContextResponse
from app.tools import (
    GET_WEATHER_TOOL, get_weather,
    ADD_NUMBERS_TOOL, add_numbers,
)


app = FastAPI(title="Simple MCP Server")

ALL_TOOLS = [GET_WEATHER_TOOL, ADD_NUMBERS_TOOL]

tool_registry = {
    "get_weather": get_weather,
    "add_numbers": add_numbers,
}


@app.post("/mcp", response_model=ModelContextResponse)
async def handle_mcp_request(request: ModelContextRequest):
    # ── discovery: advertise all available tools ───────────────────────────────
    if request.verb == "discovery":
        return ModelContextResponse(tools=ALL_TOOLS)

    # ── execute: run a named tool with supplied arguments ──────────────────────
    elif request.verb == "execute":
        if not request.tool_name:
            raise HTTPException(status_code=400, detail="'tool_name' is required for verb 'execute'.")
        try:
            tool_function = tool_registry[request.tool_name]
            arguments = request.arguments or {}
            result = tool_function(**arguments)
            return ModelContextResponse(result=result)
        except KeyError:
            raise HTTPException(status_code=404, detail=f"Tool '{request.tool_name}' not found.")
        except TypeError as exc:
            raise HTTPException(status_code=422, detail=f"Invalid arguments: {exc}")

    # ── ping: health check (Exercise 2) ───────────────────────────────────────
    elif request.verb == "ping":
        return ModelContextResponse(status="pong")

    raise HTTPException(status_code=400, detail=f"Unknown verb '{request.verb}'.")
