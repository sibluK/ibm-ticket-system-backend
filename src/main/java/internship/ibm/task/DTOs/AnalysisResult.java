package internship.ibm.task.DTOs;

public record AnalysisResult(
    boolean shouldCreateTicket,
    TicketAnalysis ticketAnalysis
) {
}
