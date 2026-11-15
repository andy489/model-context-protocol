from fastmcp import FastMCP

from data import employees

resources_server = FastMCP(name="CorporateResources")


@resources_server.resource(
    "corporate://holidays/2025",
    description="Official company holidays for 2025.",
)
def company_holidays():
    return [
        "2025-01-01",  # New Year's Day
        "2025-01-20",  # Martin Luther King Jr. Day
        "2025-05-26",  # Memorial Day
        "2025-07-04",  # Independence Day
        "2025-09-01",  # Labor Day
        "2025-11-27",  # Thanksgiving
        "2025-12-25",  # Christmas Day
    ]


@resources_server.resource("corporate://employees/{employee_id}")
def get_employee_details(employee_id: str):
    return employees.get(employee_id, {"error": "Employee not found"})
