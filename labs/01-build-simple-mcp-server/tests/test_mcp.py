import pytest
from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)


def test_discovery_returns_tools():
    response = client.post("/mcp", json={"verb": "discovery"})
    assert response.status_code == 200
    data = response.json()
    assert "tools" in data
    assert len(data["tools"]) == 1
    assert data["tools"][0]["name"] == "get_weather"


def test_discovery_tool_has_parameters():
    response = client.post("/mcp", json={"verb": "discovery"})
    tool = response.json()["tools"][0]
    assert len(tool["parameters"]) == 1
    assert tool["parameters"][0]["name"] == "location"


def test_execute_get_weather():
    response = client.post("/mcp", json={
        "verb": "execute",
        "tool_name": "get_weather",
        "arguments": {"location": "Paris"}
    })
    assert response.status_code == 200
    assert "Paris" in response.json()["result"]


def test_execute_unknown_tool_returns_404():
    response = client.post("/mcp", json={
        "verb": "execute",
        "tool_name": "nonexistent_tool",
        "arguments": {}
    })
    assert response.status_code == 404


def test_unknown_verb_returns_400():
    response = client.post("/mcp", json={"verb": "unknown"})
    assert response.status_code == 400


def test_execute_without_arguments_uses_empty_dict():
    response = client.post("/mcp", json={
        "verb": "execute",
        "tool_name": "get_weather",
        "arguments": {"location": "Tokyo"}
    })
    assert response.status_code == 200
    assert "Tokyo" in response.json()["result"]
