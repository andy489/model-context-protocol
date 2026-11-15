package com.example.corporate.resources;

import org.springframework.ai.mcp.server.McpServerFeatures;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CorporateResources {

    public List<McpServerFeatures.SyncResourceSpecification> resourceSpecifications() {
        return List.of(
                // TODO Exercise 1a: Add a static resource for company holidays
                // - URI: "corporate://holidays/2025"
                // - Name: "company_holidays"
                // - Description: "Official company holidays for 2025."
                // - Return CorporateData.HOLIDAYS_2025 serialized as JSON text content
                // Hint: use McpSchema.Resource.builder(), McpSchema.ReadResourceResult.builder(),
                //       McpSchema.TextResourceContents.builder()

                // TODO Exercise 1b: Add a dynamic resource for employee lookup
                // - URI template: "corporate://employees/{employee_id}"
                // - Name: "get_employee_details"
                // - Description: "Returns details for a given employee ID."
                // - Extract employee_id from request.uri() and look up CorporateData.EMPLOYEES
                // - Return the employee map as JSON, or {"error":"Employee not found"} if missing
        );
    }
}
