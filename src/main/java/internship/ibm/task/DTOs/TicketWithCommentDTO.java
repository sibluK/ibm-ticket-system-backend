package internship.ibm.task.DTOs;

import internship.ibm.task.Enums.TicketCategory;
import internship.ibm.task.Enums.TicketPriority;
import internship.ibm.task.Models.Comment;

import java.time.Instant;

public record TicketWithCommentDTO(
        Long id,
        String title,
        TicketCategory category,
        TicketPriority priority,
        String summary,
        Instant createdAt,
        Comment comment
) {
}
