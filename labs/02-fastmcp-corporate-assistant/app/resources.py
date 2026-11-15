from fastmcp import FastMCP

from data import employees

resources_server = FastMCP(name="CorporateResources")

# TODO: Define a resource at "corporate://holidays/2025" that returns a list of holiday dates.
# Use the @resources_server.resource() decorator with a description.

# TODO: Define a dynamic resource at "corporate://employees/{employee_id}"
# that looks up an employee from the `employees` dict and returns their details.
# Return {"error": "Employee not found"} if the ID does not exist.
