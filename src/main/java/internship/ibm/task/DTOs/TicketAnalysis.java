package internship.ibm.task.DTOs;

import internship.ibm.task.Enums.TicketCategory;
import internship.ibm.task.Enums.TicketPriority;

public record TicketAnalysis(
        String title,
        TicketCategory category,
        TicketPriority priority,
        String summary
) {
}
