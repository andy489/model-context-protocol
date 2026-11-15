package com.example.corporate.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class CorporateTools {

    // TODO Exercise 3a: Add a "list_meeting_rooms" tool
    // - @Tool description: "Returns the list of available meeting rooms."
    // - No parameters
    // - Return CorporateData.MEETING_ROOMS

    // TODO Exercise 3b: Add a "send_email" tool
    // - @Tool description: "Sends an email to a recipient."
    // - Parameters: recipient (String), subject (String), body (String)
    // - Throw IllegalArgumentException if recipient does not contain "@"
    // - Return a Map with keys: status ("sent"), recipient, subject
}
