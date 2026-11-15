package com.example.corporate.data;

import java.util.List;
import java.util.Map;

public class CorporateData {

    public static final Map<String, Map<String, String>> EMPLOYEES = Map.of(
            "U123", Map.of("name", "Alice", "department", "Engineering"),
            "U456", Map.of("name", "Bob", "department", "Marketing")
    );

    public static final List<String> HOLIDAYS_2025 = List.of(
            "2025-01-01",
            "2025-01-20",
            "2025-05-26",
            "2025-07-04",
            "2025-09-01",
            "2025-11-27",
            "2025-12-25"
    );

    public static final List<String> MEETING_ROOMS = List.of(
            "Conference Room A",
            "Conference Room B",
            "Board Room",
            "Focus Room"
    );

    private CorporateData() {
    }
}
