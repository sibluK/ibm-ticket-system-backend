package internship.ibm.task.Utility;

import internship.ibm.task.DTOs.TicketWithCommentDTO;
import internship.ibm.task.Models.Ticket;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public TicketWithCommentDTO mapTicketWithCommentToDTO(Ticket entity) {
        return new TicketWithCommentDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getCategory(),
                entity.getPriority(),
                entity.getSummary(),
                entity.getCreatedAt(),
                entity.getComment()
        );
    }
}
