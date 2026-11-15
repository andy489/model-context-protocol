package com.example.corporate;

import com.example.corporate.prompts.CorporatePrompts;
import com.example.corporate.resources.CorporateResources;
import com.example.corporate.tools.CorporateTools;
import org.springframework.ai.mcp.server.McpServerFeatures;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class CorporateAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorporateAssistantApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider corporateTools(CorporateTools tools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(tools)
                .build();
    }

    @Bean
    public List<McpServerFeatures.SyncResourceSpecification> resources(CorporateResources corporateResources) {
        return corporateResources.resourceSpecifications();
    }

    @Bean
    public List<McpServerFeatures.SyncPromptSpecification> prompts(CorporatePrompts corporatePrompts) {
        return corporatePrompts.promptSpecifications();
    }
}
