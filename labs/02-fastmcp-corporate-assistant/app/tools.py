from fastmcp import FastMCP

tools_server = FastMCP(name="CorporateTools")


@tools_server.tool(description="Returns the list of available meeting rooms.")
def list_meeting_rooms():
    return ["Conference Room A", "Conference Room B", "Board Room", "Focus Room"]


@tools_server.tool(description="Sends an email to a recipient.")
def send_email(recipient: str, subject: str, body: str):
    if "@" not in recipient:
        raise ValueError("Invalid email address: recipient must contain '@' symbol.")
    print(f"EMAIL SENT to {recipient}: {subject}")
    return {"status": "sent", "recipient": recipient, "subject": subject}
