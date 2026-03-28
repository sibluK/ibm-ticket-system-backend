package internship.ibm.task.Controllers;

import internship.ibm.task.DTOs.CreateCommentRequest;
import internship.ibm.task.DTOs.SimplePage;
import internship.ibm.task.Enums.CommentState;
import internship.ibm.task.Models.Comment;
import internship.ibm.task.Services.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(controllers = CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @Test
    void givenPageAndSize_whenRequested_thenReturnsCommentsPage() throws Exception {
        SimplePage<Comment> page = new SimplePage<>(
                List.of(new Comment(), new Comment()),
                2,
                1,
                1
        );

        when(commentService.getAllComments(1, 10)).thenReturn(page);

        mockMvc.perform(get("/comments")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));

        verify(commentService).getAllComments(1, 10);
    }

    @Test
    void givenValidCreateCommentRequest_whenRequested_thenCreateComment() throws Exception {
        CreateCommentRequest createCommentRequest = new CreateCommentRequest("Valid Comment");
        Comment comment = new Comment(
                1L,
                "text",
                Instant.parse("2026-03-28T12:00:00Z"),
                CommentState.COMPLETED,
                false,
                null
        );

        when(commentService.addComment(createCommentRequest)).thenReturn(comment);

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(comment.getId()))
                .andExpect(jsonPath("$.text").value(comment.getText()))
                .andExpect(jsonPath("$.createdAt").value("2026-03-28T12:00:00Z"))
                .andExpect(jsonPath("$.state").value(comment.getState().name()))
                .andExpect(jsonPath("$.ticketCreated").value(comment.isTicketCreated()));

        verify(commentService).addComment(createCommentRequest);
    }
}
