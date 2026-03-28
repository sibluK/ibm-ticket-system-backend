package internship.ibm.task.Services;

import internship.ibm.task.DTOs.AnalysisResult;
import internship.ibm.task.DTOs.TicketAnalysis;
import internship.ibm.task.Enums.CommentState;
import internship.ibm.task.Enums.TicketCategory;
import internship.ibm.task.Enums.TicketPriority;
import internship.ibm.task.Exceptions.ResourceNotFoundException;
import internship.ibm.task.Models.Comment;
import internship.ibm.task.Models.Ticket;
import internship.ibm.task.Repositories.CommentRepository;
import internship.ibm.task.Repositories.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HuggingFaceServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentService commentService;
    @Mock
    private CommentStateService commentStateService;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    @Spy
    private HuggingFaceService huggingFaceService;

    @Test
    void givenInvalidCommentId_whenRequested_thenThrowsResourceNotFoundException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> huggingFaceService.analyzeCommentAsync(1L)
        );

        verify(commentRepository, times(1)).findById(1L);
        assertEquals("Comment not found: " + 1L, ex.getMessage());
    }

    @Test
    void givenNegativeCreateTicketResponseFromAI_thenSetCorrectCommentState() {
        Comment comment = new Comment(1L, "Comment that does not need a ticket", Instant.now(), CommentState.PROCESSING, false, null);
        AnalysisResult negativeResult = new AnalysisResult(false, null);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        doReturn(negativeResult)
                .when(huggingFaceService)
                .sendAnalyzeRequest(comment.getText());

        huggingFaceService.analyzeCommentAsync(comment.getId());

        assertEquals(CommentState.COMPLETED, comment.getState());
        assertFalse(comment.isTicketCreated());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void givenEmptyTicketAnalysis_thenSetCorrectCommentState() {
        Comment comment = new Comment(1L, "Comment that does need a ticket", Instant.now(), CommentState.PROCESSING, false, null);
        AnalysisResult positiveResultButEmpty = new AnalysisResult(true, null);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        doReturn(positiveResultButEmpty)
                .when(huggingFaceService)
                .sendAnalyzeRequest(comment.getText());

        huggingFaceService.analyzeCommentAsync(comment.getId());

        assertEquals(CommentState.FAILED, comment.getState());
        assertFalse(comment.isTicketCreated());
        assertNull(positiveResultButEmpty.ticketAnalysis());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void givenPositiveCreateTicketResponseFromAI_thenCreateTicketAndChangeCommentState() {
        Comment comment = new Comment(1L, "Comment that does need a ticket", Instant.now(), CommentState.PROCESSING, false, null);
        TicketAnalysis ticketAnalysis = new TicketAnalysis("ticket-title", TicketCategory.OTHER, TicketPriority.LOW, "ticket-summary");
        AnalysisResult positiveResult = new AnalysisResult(true, ticketAnalysis);
        Ticket ticket = new Ticket(1L, "ticket-title", TicketCategory.OTHER, TicketPriority.LOW, "ticket-summary", Instant.now(), comment);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        doReturn(positiveResult)
                .when(huggingFaceService)
                .sendAnalyzeRequest(comment.getText());

        huggingFaceService.analyzeCommentAsync(comment.getId());

        assertEquals(CommentState.COMPLETED, comment.getState());
        assertTrue(comment.isTicketCreated());

        assertNotNull(comment.getTicket());
        assertSame(comment, comment.getTicket().getComment());

        verify(commentRepository).save(any(Comment.class));
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void analyzeCommentAsync_shouldMarkCommentFailed_whenExceptionOccurs() {
        Long commentId = 1L;

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setText("Some comment");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        doThrow(new RuntimeException("AI failure"))
                .when(huggingFaceService)
                .sendAnalyzeRequest(anyString());

        huggingFaceService.analyzeCommentAsync(commentId);

        verify(commentStateService).markCommentFailed(commentId);

        verify(ticketRepository, never()).save(any());

        verify(commentRepository, never()).save(argThat(c -> c.getState() == CommentState.COMPLETED));
    }
}
