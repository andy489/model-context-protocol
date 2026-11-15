from fastmcp import FastMCP

tools_server = FastMCP(name="CorporateTools")

# TODO: Define a tool called "list_meeting_rooms" with a description.
# It should return a list of room names:
# ["Conference Room A", "Conference Room B", "Board Room", "Focus Room"]

# TODO: Define a tool called "send_email" that accepts recipient (str),
# subject (str), and body (str).
# - Raise ValueError if recipient does not contain "@".
# - Print a confirmation line (mock send).
# - Return a dict: {"status": "sent", "recipient": ..., "subject": ...}
