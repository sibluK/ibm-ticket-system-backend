package internship.ibm.task.Services;

import internship.ibm.task.DTOs.CreateCommentRequest;
import internship.ibm.task.DTOs.SimplePage;
import internship.ibm.task.Enums.CommentState;
import internship.ibm.task.Models.Comment;
import internship.ibm.task.Repositories.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final HuggingFaceService huggingFaceService;

    public CommentService(CommentRepository commentRepository, HuggingFaceService huggingFaceService) {
        this.commentRepository = commentRepository;
        this.huggingFaceService = huggingFaceService;
    }

    public SimplePage<Comment> getAllComments(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size,  Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> pagedComments = commentRepository.findAll(pageable);
        return new SimplePage<>(
                pagedComments.getContent(),
                pagedComments.getTotalElements(),
                pagedComments.getTotalPages(),
                page
        );
    }

    public Comment addComment(CreateCommentRequest createCommentRequest) {
        Comment comment = new Comment();
        comment.setText(createCommentRequest.text());
        comment.setCreatedAt(Instant.now());
        comment.setState(CommentState.PROCESSING);
        Comment savedComment = commentRepository.save(comment);
        huggingFaceService.analyzeCommentAsync(savedComment.getId());
        return savedComment;
    }
}
