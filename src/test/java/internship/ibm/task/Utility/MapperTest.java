package internship.ibm.task.Utility;

import internship.ibm.task.DTOs.TicketWithCommentDTO;
import internship.ibm.task.Enums.CommentState;
import internship.ibm.task.Enums.TicketCategory;
import internship.ibm.task.Enums.TicketPriority;
import internship.ibm.task.Models.Comment;
import internship.ibm.task.Models.Ticket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class MapperTest {

    @InjectMocks
    private Mapper mapper;

    @Test
    void givenTicketEntity_whenRequested_thenReturnsTicketWithCommentDTO() {
        Comment comment = new Comment(
                1L,
                "comment-text",
                Instant.now(),
                CommentState.COMPLETED,
                true,
                null
        );

        Ticket ticket = new Ticket(
                1L,
                "ticket-title",
                TicketCategory.OTHER,
                TicketPriority.LOW,
                "ticket-summary",
                Instant.now(),
                comment
        );

        comment.setTicket(ticket);

        TicketWithCommentDTO result = mapper.mapTicketWithCommentToDTO(ticket);

        assertNotNull(result);
        assertNotNull(result.comment());
        assertEquals(ticket.getId(), result.id());
        assertEquals(comment, result.comment());
    }
}
