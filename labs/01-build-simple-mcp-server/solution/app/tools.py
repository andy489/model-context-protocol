from .models import Tool, ToolParameter


# ── Tool 1: get_weather ────────────────────────────────────────────────────────

def get_weather(location: str) -> str:
    """Gets the current weather for a specified location."""
    return f"The weather in {location} is sunny."


GET_WEATHER_TOOL = Tool(
    name="get_weather",
    description="Gets the current weather for a specified location.",
    parameters=[
        ToolParameter(name="location", type="string")
    ]
)


# ── Tool 2: add_numbers (Exercise 1) ──────────────────────────────────────────

def add_numbers(a: float, b: float) -> str:
    """Adds two numbers and returns the result."""
    return str(a + b)


ADD_NUMBERS_TOOL = Tool(
    name="add_numbers",
    description="Adds two numbers and returns the result as a string.",
    parameters=[
        ToolParameter(name="a", type="number"),
        ToolParameter(name="b", type="number"),
    ]
)
