from fastmcp import FastMCP

from prompts import prompts_server
from resources import resources_server
from tools import tools_server

mcp = FastMCP("Corporate Assistant")

mcp.mount(resources_server)
mcp.mount(tools_server)
mcp.mount(prompts_server)

if __name__ == "__main__":
    mcp.run(transport="http", port=8000)
