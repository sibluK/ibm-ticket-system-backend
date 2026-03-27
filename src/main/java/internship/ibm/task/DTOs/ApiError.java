package internship.ibm.task.DTOs;

import java.time.Instant;
import java.util.Map;

public record ApiError(
        int status,
        String error,
        Map<String, String> message,
        String path,
        Instant timestamp
) {
}
