"""
Example client for connecting to the Corporate Assistant FastMCP server.

Usage:
    # First, start the server in one terminal:
    python app/server.py

    # Then, in another terminal, run the client:
    python client.py
"""

import asyncio
import sys

from fastmcp import Client


async def test_server():
    """Test the Corporate Assistant MCP server."""
    client = Client("http://localhost:8000/mcp")

    try:
        async with client:
            print("Connected to Corporate Assistant MCP server!\n")

            print("=== Testing Resources ===")
            resources = await client.list_resources()
            print(f"Available resources: {[r.name for r in resources]}\n")

            if any(r.name == "company_holidays" for r in resources):
                holidays = await client.read_resource("corporate://holidays/2025")
                print(f"Company holidays: {holidays}\n")

            if any(r.name == "get_employee_details" for r in resources):
                employee = await client.read_resource(
                    "corporate://employees/{employee_id}", {"employee_id": "U123"}
                )
                print(f"Employee details: {employee}\n")

            print("=== Testing Prompts ===")
            prompts = await client.list_prompts()
            print(f"Available prompts: {[p.name for p in prompts]}\n")

            if any(p.name == "generate_welcome_email" for p in prompts):
                email_template = await client.get_prompt("generate_welcome_email", {})
                print(f"Welcome email template:\n{email_template}\n")

            if any(p.name == "project_status_update" for p in prompts):
                status = await client.get_prompt(
                    "project_status_update",
                    {"project_name": "Q4 Launch", "progress": 75},
                )
                print(f"Project status update:\n{status}\n")

            print("=== Testing Tools ===")
            tools = await client.list_tools()
            print(f"Available tools: {[t.name for t in tools]}\n")

            if any(t.name == "list_meeting_rooms" for t in tools):
                rooms = await client.call_tool("list_meeting_rooms", {})
                print(f"Meeting rooms: {rooms}\n")

            if any(t.name == "send_email" for t in tools):
                result = await client.call_tool(
                    "send_email",
                    {
                        "recipient": "alice@company.com",
                        "subject": "Test Email",
                        "body": "This is a test email from the MCP client.",
                    },
                )
                print(f"Email sent: {result}\n")

            print("=== Testing Error Handling ===")
            try:
                await client.call_tool(
                    "send_email",
                    {
                        "recipient": "invalid-email",
                        "subject": "Test",
                        "body": "This should fail.",
                    },
                )
                print("ERROR: Should have raised ValueError for invalid email!")
            except Exception as e:
                print(f"Correctly caught error for invalid email: {e}\n")

            print("All tests completed successfully!")

    except Exception as e:
        print(f"Error connecting to server: {e}")
        print("\nMake sure the server is running first:")
        print("  python app/server.py")
        sys.exit(1)


if __name__ == "__main__":
    asyncio.run(test_server())
