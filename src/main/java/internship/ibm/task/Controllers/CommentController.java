package internship.ibm.task.Controllers;

import internship.ibm.task.DTOs.CreateCommentRequest;
import internship.ibm.task.DTOs.SimplePage;
import internship.ibm.task.Models.Comment;
import internship.ibm.task.Services.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<SimplePage<Comment>> getAllComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        SimplePage<Comment> comments = commentService.getAllComments(page, size);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody @Valid CreateCommentRequest createCommentRequest) {
        Comment comment = commentService.addComment(createCommentRequest);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }
}
