from fastmcp import FastMCP

prompts_server = FastMCP(name="CorporatePrompts")


@prompts_server.prompt(description="Generates a welcome email template for a new hire.")
def generate_welcome_email():
    return """Subject: Welcome to the Team, {new_hire_name}!

Hi {new_hire_name},

We're thrilled to have you joining us on {start_date}!

Here's what to expect on your first day:
- 9:00 AM: Check in at reception and collect your badge
- 9:30 AM: IT setup and equipment handover
- 11:00 AM: Meet your team
- 12:30 PM: Welcome lunch with your manager

Your onboarding buddy will reach out before your start date with more details.

Looking forward to having you on board!

Best regards,
The People Team"""


@prompts_server.prompt(description="Creates a formatted project status update.")
def project_status_update(project_name: str, progress: int):
    if progress >= 100:
        status_label = "Complete"
    elif progress >= 75:
        status_label = "On Track"
    elif progress >= 50:
        status_label = "In Progress"
    else:
        status_label = "Early Stage"

    return f"""**Project Status Update**

Project: {project_name}
Progress: {progress}% — {status_label}

{'█' * (progress // 10)}{'░' * (10 - progress // 10)} {progress}%

Next steps: [Describe what's happening next]
Blockers: [List any blockers or risks]
ETA: [Estimated completion date]"""
