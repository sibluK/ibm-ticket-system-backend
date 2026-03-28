package internship.ibm.task.Services;

import internship.ibm.task.Enums.CommentState;
import internship.ibm.task.Exceptions.ResourceNotFoundException;
import internship.ibm.task.Models.Comment;
import internship.ibm.task.Repositories.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentStateServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentStateService commentStateService;

    @Test
    void givenCommentId_whenRequested_thenMarkCommentAsFailed() {
        Long commentId = 1L;

        Comment comment = new Comment(
                commentId,
                "text",
                Instant.now(),
                CommentState.PROCESSING,
                false,
                null
        );

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentStateService.markCommentFailed(commentId);

        assertEquals(CommentState.FAILED, comment.getState());
        assertFalse(comment.isTicketCreated());

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(comment);
    }
}