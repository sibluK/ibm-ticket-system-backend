package internship.ibm.task.Services;

import internship.ibm.task.DTOs.CreateCommentRequest;
import internship.ibm.task.DTOs.SimplePage;
import internship.ibm.task.Enums.CommentState;
import internship.ibm.task.Models.Comment;
import internship.ibm.task.Repositories.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private HuggingFaceService huggingFaceService;
    @InjectMocks
    private CommentService commentService;

    @Test
    void givenPageAndSize_whenRequested_thenReturnsSimplePageOfComments() {
        int pageNumber = 1;
        int pageSize = 3;
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        Comment comment1 = new Comment(
                1L,
                "comment-1",
                Instant.now(),
                CommentState.PROCESSING,
                false,
                null
        );

        Comment comment2 = new Comment(
                2L,
                "comment-2",
                Instant.now(),
                CommentState.COMPLETED,
                true,
                null
        );

        List<Comment> commentsList = List.of(comment1, comment2);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<Comment> pagedComments = new PageImpl<>(commentsList, pageable, commentsList.size());

        when(commentRepository.findAll(any(Pageable.class))).thenReturn(pagedComments);

        SimplePage<Comment> result = commentService.getAllComments(pageNumber, pageSize);

        assertNotNull(result);
        assertEquals(commentsList.size(), result.content().size());
        assertEquals(comment1, result.content().get(0));
        assertEquals(comment2, result.content().get(1));
        assertEquals(pageNumber, result.currentPage());
        assertEquals(commentsList.size(), result.totalElements());
        assertEquals(1, result.totalPages());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(commentRepository).findAll(pageableCaptor.capture());

        Pageable usedPageable = pageableCaptor.getValue();
        assertEquals(pageNumber - 1, usedPageable.getPageNumber());
        assertEquals(pageSize, usedPageable.getPageSize());
        assertNotNull(usedPageable.getSort().getOrderFor("createdAt"));
        assertEquals(
                Sort.Direction.DESC,
                Objects.requireNonNull(usedPageable.getSort().getOrderFor("createdAt")).getDirection()
        );
    }

    @Test
    void givenValidCreateCommentRequest_whenRequested_thenReturnCreatedComment() {
        CreateCommentRequest createCommentRequest = new CreateCommentRequest("test_comment");
        Comment savedComment = new Comment(
                1L,
                createCommentRequest.text(),
                Instant.now(),
                CommentState.PROCESSING,
                false,
                null
        );

        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        Comment result = commentService.addComment(createCommentRequest);

        verify(huggingFaceService).analyzeCommentAsync(1L);

        assertNotNull(result);
        assertEquals(savedComment.getId(), result.getId());
        assertEquals(savedComment.getText(), result.getText());
        assertEquals(savedComment.getCreatedAt(), result.getCreatedAt());
        assertEquals(savedComment.getState(), result.getState());
        assertFalse(savedComment.isTicketCreated());
        assertEquals(CommentState.PROCESSING, result.getState());
    }
}
