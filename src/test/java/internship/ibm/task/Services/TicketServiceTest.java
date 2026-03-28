package internship.ibm.task.Services;

import internship.ibm.task.DTOs.TicketWithCommentDTO;
import internship.ibm.task.Enums.CommentState;
import internship.ibm.task.Enums.TicketCategory;
import internship.ibm.task.Enums.TicketPriority;
import internship.ibm.task.Exceptions.ResourceNotFoundException;
import internship.ibm.task.Models.Comment;
import internship.ibm.task.Models.Ticket;
import internship.ibm.task.Repositories.TicketRepository;
import internship.ibm.task.Utility.Mapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private Mapper mapper;
    @InjectMocks
    private TicketService ticketService;

    @Test
    void whenRequested_thenReturnsAllTickets() {
        Comment comment1 = new Comment(1L, "comment-1", Instant.now(), CommentState.PROCESSING, false, null);
        Comment comment2 = new Comment(2L, "comment-2", Instant.now(), CommentState.COMPLETED, true, null);

        Ticket ticket1 = new Ticket();
        ticket1.setId(1L);
        ticket1.setTitle("ticket-title-1");
        ticket1.setCategory(TicketCategory.OTHER);
        ticket1.setPriority(TicketPriority.LOW);
        ticket1.setSummary("ticket-summary-1");
        ticket1.setCreatedAt(Instant.now());
        ticket1.setComment(comment1);

        Ticket ticket2 = new Ticket();
        ticket2.setId(2L);
        ticket2.setTitle("ticket-title-2");
        ticket2.setCategory(TicketCategory.BUG);
        ticket2.setPriority(TicketPriority.HIGH);
        ticket2.setSummary("ticket-summary-2");
        ticket2.setCreatedAt(Instant.now());
        ticket2.setComment(comment2);

        TicketWithCommentDTO dto1 = new TicketWithCommentDTO(
                1L, "ticket-title-1", TicketCategory.OTHER, TicketPriority.LOW, "ticket-summary-1", ticket1.getCreatedAt(), comment1
        );

        TicketWithCommentDTO dto2 = new TicketWithCommentDTO(
                2L, "ticket-title-2", TicketCategory.BUG, TicketPriority.HIGH, "ticket-summary-2", ticket2.getCreatedAt(), comment2
        );

        when(ticketRepository.findAll()).thenReturn(List.of(ticket1, ticket2));
        when(mapper.mapTicketWithCommentToDTO(ticket1)).thenReturn(dto1);
        when(mapper.mapTicketWithCommentToDTO(ticket2)).thenReturn(dto2);

        List<TicketWithCommentDTO> result = ticketService.getAllTickets();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));

        verify(ticketRepository, times(1)).findAll();
        verify(mapper, times(1)).mapTicketWithCommentToDTO(ticket1);
        verify(mapper, times(1)).mapTicketWithCommentToDTO(ticket2);
        verifyNoMoreInteractions(ticketRepository, mapper);
    }

    @Test
    void whenNoTickets_thenReturnsEmptyList() {
        when(ticketRepository.findAll()).thenReturn(List.of());

        List<TicketWithCommentDTO> result = ticketService.getAllTickets();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(ticketRepository, times(1)).findAll();
        verify(mapper, never()).mapTicketWithCommentToDTO(any(Ticket.class));
    }

    @Test
    void givenTicketId_whenRequested_thenReturnsTicketWithCommentDTO() {
        Comment comment1 = new Comment(1L, "comment-1", Instant.now(), CommentState.PROCESSING, false, null);

        Ticket ticket1 = new Ticket();
        ticket1.setId(1L);
        ticket1.setTitle("ticket-title-1");
        ticket1.setCategory(TicketCategory.OTHER);
        ticket1.setPriority(TicketPriority.LOW);
        ticket1.setSummary("ticket-summary-1");
        ticket1.setCreatedAt(Instant.now());
        ticket1.setComment(comment1);

        TicketWithCommentDTO dto1 = new TicketWithCommentDTO(
                1L,
                "ticket-title-1",
                TicketCategory.OTHER,
                TicketPriority.LOW,
                "ticket-summary-1",
                Instant.now(),
                comment1
        );

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket1));
        when(mapper.mapTicketWithCommentToDTO(any(Ticket.class))).thenReturn(dto1);

        TicketWithCommentDTO result = ticketService.getTicketById(1L);

        assertNotNull(result);
        assertEquals(dto1, result);

        verify(ticketRepository, times(1)).findById(1L);
        verify(mapper, times(1)).mapTicketWithCommentToDTO(any(Ticket.class));
        verifyNoMoreInteractions(ticketRepository, mapper);
    }

    @Test
    void givenInvalidTicketId_whenRequested_thenThrowsResourceNotFoundException() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> ticketService.getTicketById(1L)
        );

        verify(ticketRepository, times(1)).findById(1L);
        assertEquals("Ticket not found: " + 1L, ex.getMessage());
    }
}
