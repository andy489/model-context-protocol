package com.example.corporate.tools;

import com.example.corporate.data.CorporateData;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CorporateTools {

    @Tool(description = "Returns the list of available meeting rooms.")
    public List<String> listMeetingRooms() {
        return CorporateData.MEETING_ROOMS;
    }

    @Tool(description = "Sends an email to a recipient.")
    public Map<String, String> sendEmail(String recipient, String subject, String body) {
        if (!recipient.contains("@")) {
            throw new IllegalArgumentException(
                    "Invalid email address: recipient must contain '@' symbol.");
        }
        System.out.printf("EMAIL SENT to %s: %s%n", recipient, subject);
        return Map.of("status", "sent", "recipient", recipient, "subject", subject);
    }
}
