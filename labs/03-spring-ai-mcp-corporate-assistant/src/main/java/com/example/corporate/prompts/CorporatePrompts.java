package com.example.corporate.prompts;

import org.springframework.ai.mcp.server.McpServerFeatures;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CorporatePrompts {

    public List<McpServerFeatures.SyncPromptSpecification> promptSpecifications() {
        return List.of(
                // TODO Exercise 2a: Add a "generate_welcome_email" prompt
                // - No required arguments
                // - Description: "Generates a welcome email template for a new hire."
                // - Return a template string with {new_hire_name} and {start_date} placeholders
                // Hint: use McpSchema.Prompt.builder(), McpSchema.GetPromptResult.builder(),
                //       McpSchema.PromptMessage, McpSchema.TextContent, McpSchema.Role.USER

                // TODO Exercise 2b: Add a "project_status_update" prompt
                // - Required arguments: "project_name" (String), "progress" (String, 0-100)
                // - Description: "Creates a formatted project status update."
                // - Return a formatted status string using the arguments
        );
    }
}
