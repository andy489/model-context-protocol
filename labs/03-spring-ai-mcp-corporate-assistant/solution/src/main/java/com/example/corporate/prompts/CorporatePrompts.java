package com.example.corporate.prompts;

import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.server.McpServerFeatures;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CorporatePrompts {

    public List<McpServerFeatures.SyncPromptSpecification> promptSpecifications() {
        return List.of(
                welcomeEmailPrompt(),
                projectStatusPrompt()
        );
    }

    private McpServerFeatures.SyncPromptSpecification welcomeEmailPrompt() {
        var prompt = McpSchema.Prompt.builder("generate_welcome_email")
                .description("Generates a welcome email template for a new hire.")
                .arguments(List.of())
                .build();

        return new McpServerFeatures.SyncPromptSpecification(prompt, (exchange, request) -> {
            String template = """
                    Subject: Welcome to the Team, {new_hire_name}!

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
                    The People Team""";

            return McpSchema.GetPromptResult.builder()
                    .description("Generates a welcome email template for a new hire.")
                    .messages(List.of(
                            McpSchema.PromptMessage.builder()
                                    .role(McpSchema.Role.USER)
                                    .content(McpSchema.TextContent.builder()
                                            .text(template)
                                            .build())
                                    .build()))
                    .build();
        });
    }

    private McpServerFeatures.SyncPromptSpecification projectStatusPrompt() {
        var prompt = McpSchema.Prompt.builder("project_status_update")
                .description("Creates a formatted project status update.")
                .arguments(List.of(
                        McpSchema.PromptArgument.builder("project_name")
                                .description("Name of the project")
                                .required(true)
                                .build(),
                        McpSchema.PromptArgument.builder("progress")
                                .description("Progress percentage (0-100)")
                                .required(true)
                                .build()))
                .build();

        return new McpServerFeatures.SyncPromptSpecification(prompt, (exchange, request) -> {
            String projectName = (String) request.arguments().getOrDefault("project_name", "Unknown");
            int progress = Integer.parseInt((String) request.arguments().getOrDefault("progress", "0"));

            String statusLabel = progress >= 75 ? "On Track" : progress >= 50 ? "In Progress" : "Early Stage";
            String bar = "█".repeat(progress / 10) + "░".repeat(10 - progress / 10);

            String text = """
                    **Project Status Update**

                    Project: %s
                    Progress: %d%% — %s

                    %s %d%%

                    Next steps: [Describe what's happening next]
                    Blockers: [List any blockers or risks]
                    ETA: [Estimated completion date]""".formatted(projectName, progress, statusLabel, bar, progress);

            return McpSchema.GetPromptResult.builder()
                    .description("Creates a formatted project status update.")
                    .messages(List.of(
                            McpSchema.PromptMessage.builder()
                                    .role(McpSchema.Role.USER)
                                    .content(McpSchema.TextContent.builder()
                                            .text(text)
                                            .build())
                                    .build()))
                    .build();
        });
    }
}
