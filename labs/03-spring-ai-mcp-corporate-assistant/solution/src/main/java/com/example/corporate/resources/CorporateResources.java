package com.example.corporate.resources;

import com.example.corporate.data.CorporateData;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.server.McpServerFeatures;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CorporateResources {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<McpServerFeatures.SyncResourceSpecification> resourceSpecifications() {
        return List.of(
                holidaysResource(),
                employeeResource()
        );
    }

    private McpServerFeatures.SyncResourceSpecification holidaysResource() {
        var resource = McpSchema.Resource.builder()
                .uri("corporate://holidays/2025")
                .name("company_holidays")
                .description("Official company holidays for 2025.")
                .mimeType("application/json")
                .build();

        return new McpServerFeatures.SyncResourceSpecification(resource, (exchange, request) -> {
            try {
                String json = objectMapper.writeValueAsString(CorporateData.HOLIDAYS_2025);
                return McpSchema.ReadResourceResult.builder(
                        List.of(McpSchema.TextResourceContents.builder(request.uri(), json)
                                .mimeType("application/json")
                                .build()))
                        .build();
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize holidays", e);
            }
        });
    }

    private McpServerFeatures.SyncResourceSpecification employeeResource() {
        var resource = McpSchema.Resource.builder()
                .uri("corporate://employees/{employee_id}")
                .name("get_employee_details")
                .description("Returns details for a given employee ID.")
                .mimeType("application/json")
                .build();

        return new McpServerFeatures.SyncResourceSpecification(resource, (exchange, request) -> {
            try {
                String uri = request.uri();
                String employeeId = uri.substring(uri.lastIndexOf('/') + 1);
                Map<String, ?> employee = CorporateData.EMPLOYEES.getOrDefault(
                        employeeId, Map.of("error", "Employee not found"));
                String json = objectMapper.writeValueAsString(employee);
                return McpSchema.ReadResourceResult.builder(
                        List.of(McpSchema.TextResourceContents.builder(uri, json)
                                .mimeType("application/json")
                                .build()))
                        .build();
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize employee", e);
            }
        });
    }
}
