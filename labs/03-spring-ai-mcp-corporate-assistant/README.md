# Lab 03: Spring AI MCP Corporate Assistant

The Java/Spring equivalent of Lab 02. You'll build the same **Corporate Assistant** MCP server — employees, holidays, meeting rooms, email — but using **Spring AI MCP** and Spring Boot instead of Python/FastMCP.

By the end you'll understand how the three MCP primitives (Resources, Prompts, Tools) map onto Spring idioms, and how the Java approach compares to the Python one.

---

## What Is Spring AI MCP?

[Spring AI](https://spring.io/projects/spring-ai) is the official Spring integration for AI workloads. Its MCP module provides:

- A Spring Boot starter that auto-configures an MCP server
- `@Tool` annotation on regular Spring bean methods — identical intent to FastMCP's `@mcp.tool`
- `McpServerFeatures.SyncResourceSpecification` and `SyncPromptSpecification` — builder-based registration for Resources and Prompts
- HTTP/Streamable transport wired through standard Spring MVC

### Comparison to FastMCP (Lab 02)

| | FastMCP (Python) | Spring AI MCP (Java) |
|---|---|---|
| **Tools** | `@mcp.tool` decorator on a function | `@Tool` annotation on a Spring bean method |
| **Resources** | `@mcp.resource` decorator | `McpServerFeatures.SyncResourceSpecification` builder |
| **Prompts** | `@mcp.prompt` decorator | `McpServerFeatures.SyncPromptSpecification` builder |
| **Server startup** | `mcp.run(transport="http")` | Spring Boot `main()` + `application.properties` |
| **Composition** | `mcp.mount(sub_server)` | Spring `@Bean` list injection |
| **Schema generation** | From Python type annotations | From Java method signatures |

Tools are the closest 1:1 mapping — just an annotation on a method. Resources and Prompts require more boilerplate in Java because Spring AI uses a builder/specification pattern rather than decorators.

---

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     LLM Client                          │
│           (Claude Desktop / MCP Inspector)              │
└──────────────────────────┬──────────────────────────────┘
                           │  MCP Streamable HTTP
                           ▼
┌─────────────────────────────────────────────────────────┐
│          Corporate Assistant MCP Server                 │
│         Spring Boot — http://localhost:8080/mcp         │
│                                                         │
│  ┌───────────────────┐  ┌──────────────────────────┐   │
│  │  CorporateTools   │  │    CorporatePrompts       │   │
│  │  @Component       │  │    @Component             │   │
│  │                   │  │                           │   │
│  │  @Tool            │  │  SyncPromptSpecification  │   │
│  │  listMeetingRooms │  │  generate_welcome_email   │   │
│  │  sendEmail        │  │  project_status_update    │   │
│  └───────────────────┘  └──────────────────────────┘   │
│                                                         │
│  ┌───────────────────┐  ┌──────────────────────────┐   │
│  │ CorporateResources│  │    CorporateData          │   │
│  │ @Component        │  │    (static data class)    │   │
│  │                   │  │                           │   │
│  │ SyncResource      │  │  EMPLOYEES                │   │
│  │ Specification     │  │  HOLIDAYS_2025            │   │
│  │ holidays/2025     │  │  MEETING_ROOMS            │   │
│  │ employees/{id}    │  │                           │   │
│  └───────────────────┘  └──────────────────────────┘   │
│                                                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │         CorporateAssistantApplication           │   │
│  │  @SpringBootApplication                         │   │
│  │  Wires Tools, Resources, Prompts as @Beans      │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### Key wiring in `CorporateAssistantApplication.java`

```java
// Tools: wrap the @Component with MethodToolCallbackProvider
@Bean
public ToolCallbackProvider corporateTools(CorporateTools tools) {
    return MethodToolCallbackProvider.builder().toolObjects(tools).build();
}

// Resources and Prompts: register as List<> beans
@Bean
public List<McpServerFeatures.SyncResourceSpecification> resources(CorporateResources r) {
    return r.resourceSpecifications();
}

@Bean
public List<McpServerFeatures.SyncPromptSpecification> prompts(CorporatePrompts p) {
    return p.promptSpecifications();
}
```

Spring AI's autoconfiguration detects these beans and registers them with the MCP server automatically.

---

## Project Structure

```
03-spring-ai-mcp-corporate-assistant/
├── src/main/java/com/example/corporate/
│   ├── CorporateAssistantApplication.java   # Spring Boot entry point — do not modify
│   ├── data/
│   │   └── CorporateData.java               # Shared data — do not modify
│   ├── resources/
│   │   └── CorporateResources.java          # Exercise 1: define Resources
│   ├── prompts/
│   │   └── CorporatePrompts.java            # Exercise 2: define Prompts
│   └── tools/
│       └── CorporateTools.java              # Exercise 3: define Tools
├── src/main/resources/
│   └── application.properties              # Server config — do not modify
├── solution/                               # Completed reference implementation
└── pom.xml
```

---

## Quickstart

### Prerequisites

- Java 17+
- Maven 3.8+ (`mvn -v` to check)

Verify with:
```bash
java -version
mvn -version
```

### 1. Build and start the server

```bash
cd labs/03-spring-ai-mcp-corporate-assistant
mvn spring-boot:run
```

The server starts at `http://localhost:8080/mcp`.

### 2. Test with MCP Inspector (recommended)

```bash
npx @modelcontextprotocol/inspector http://localhost:8080/mcp
```

Opens a browser UI to explore tools, resources, and prompts interactively.

### 3. Test with a Python one-liner

```bash
python3 -c "
import asyncio
from fastmcp import Client

async def main():
    async with Client('http://localhost:8080/mcp') as c:
        print('Tools:', [t.name for t in await c.list_tools()])
        print('Resources:', [r.name for r in await c.list_resources()])
        print('Prompts:', [p.name for p in await c.list_prompts()])

asyncio.run(main())
"
```

> **Note:** Raw `curl` does not work. The Streamable HTTP transport requires a session handshake — use the Inspector or a proper MCP client.

### 4. Run the solution

```bash
cd solution
mvn spring-boot:run
```

---

## FastMCP Concepts in Depth

### Tools — closest to FastMCP

In FastMCP:
```python
@tools_server.tool(description="Returns the list of available meeting rooms.")
def list_meeting_rooms():
    return ["Conference Room A", "Conference Room B", "Board Room", "Focus Room"]
```

In Spring AI MCP:
```java
@Tool(description = "Returns the list of available meeting rooms.")
public List<String> listMeetingRooms() {
    return CorporateData.MEETING_ROOMS;
}
```

Same intent, same decorator pattern. The method must be on a Spring `@Component` bean, and the bean must be wrapped with `MethodToolCallbackProvider` in the application class.

### Resources — more verbose in Java

In FastMCP:
```python
@resources_server.resource("corporate://holidays/2025", description="Official company holidays.")
def company_holidays():
    return ["2025-01-01", "2025-07-04", "2025-12-25"]
```

In Spring AI MCP:
```java
var resource = McpSchema.Resource.builder()
        .uri("corporate://holidays/2025")
        .name("company_holidays")
        .description("Official company holidays for 2025.")
        .mimeType("application/json")
        .build();

return new McpServerFeatures.SyncResourceSpecification(resource, (exchange, request) -> {
    String json = objectMapper.writeValueAsString(CorporateData.HOLIDAYS_2025);
    return McpSchema.ReadResourceResult.builder(
            List.of(McpSchema.TextResourceContents.builder(request.uri(), json)
                    .mimeType("application/json").build()))
            .build();
});
```

The builder pattern makes the schema explicit — more boilerplate, but also more control.

**Dynamic resources** extract the URI template variable manually from `request.uri()`:
```java
String uri = request.uri();  // "corporate://employees/U123"
String employeeId = uri.substring(uri.lastIndexOf('/') + 1);  // "U123"
```

### Prompts — same verbosity as Resources

In FastMCP:
```python
@prompts_server.prompt(description="Creates a project status update.")
def project_status_update(project_name: str, progress: int):
    return f"Project: {project_name} — {progress}% complete"
```

In Spring AI MCP:
```java
var prompt = McpSchema.Prompt.builder("project_status_update")
        .description("Creates a formatted project status update.")
        .arguments(List.of(
                McpSchema.PromptArgument.builder("project_name").required(true).build(),
                McpSchema.PromptArgument.builder("progress").required(true).build()))
        .build();

return new McpServerFeatures.SyncPromptSpecification(prompt, (exchange, request) -> {
    String projectName = (String) request.arguments().get("project_name");
    // ...
    return McpSchema.GetPromptResult.builder()
            .messages(List.of(McpSchema.PromptMessage.builder()
                    .role(McpSchema.Role.USER)
                    .content(McpSchema.TextContent.builder().text(text).build())
                    .build()))
            .build();
});
```

---

## The Exercises

Open the three files and follow the `// TODO` comments.

### Exercise 1 — Resources (`src/.../resources/CorporateResources.java`)

1. **Static resource** at `corporate://holidays/2025`
   - Name: `company_holidays`, MIME: `application/json`
   - Returns `CorporateData.HOLIDAYS_2025` serialized to JSON
2. **Dynamic resource** at `corporate://employees/{employee_id}`
   - Name: `get_employee_details`
   - Extracts the employee ID from the URI, looks up `CorporateData.EMPLOYEES`
   - Returns `{"error":"Employee not found"}` if the ID doesn't exist

### Exercise 2 — Prompts (`src/.../prompts/CorporatePrompts.java`)

1. **`generate_welcome_email`** — no arguments, returns a template with `{new_hire_name}` and `{start_date}` placeholders
2. **`project_status_update`** — arguments: `project_name` (required), `progress` (required); returns a formatted status string

### Exercise 3 — Tools (`src/.../tools/CorporateTools.java`)

1. **`listMeetingRooms`** — no parameters, returns `CorporateData.MEETING_ROOMS`
2. **`sendEmail`** — parameters: `recipient`, `subject`, `body`; throws `IllegalArgumentException` if `recipient` has no `@`; returns `Map.of("status","sent","recipient",...,"subject",...)`

---

## Extending the Lab

### Add a new employee

Edit `CorporateData.java` — the resource picks it up automatically on next request:
```java
public static final Map<String, Map<String, String>> EMPLOYEES = Map.of(
    "U123", Map.of("name", "Alice", "department", "Engineering"),
    "U456", Map.of("name", "Bob",   "department", "Marketing"),
    "U789", Map.of("name", "Carol", "department", "Design")   // new
);
```

### Add a new tool

Add a `@Tool` method to `CorporateTools`. No changes to `CorporateAssistantApplication` needed — `MethodToolCallbackProvider` picks up all `@Tool` methods on the bean.

### Add a new resource or prompt

Add a new `SyncResourceSpecification` or `SyncPromptSpecification` to the list returned by `resourceSpecifications()` / `promptSpecifications()`.

### Change the port

In `application.properties`:
```properties
server.port=9090
```
