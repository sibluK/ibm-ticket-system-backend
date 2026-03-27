package internship.ibm.task.Services;

import internship.ibm.task.Enums.CommentState;
import internship.ibm.task.Exceptions.ResourceNotFoundException;
import internship.ibm.task.Models.Comment;
import internship.ibm.task.Repositories.CommentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CommentStateService {

    private final CommentRepository commentRepository;

    public CommentStateService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void markCommentFailed(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));

        comment.setState(CommentState.FAILED);
        comment.setTicketCreated(false);
        commentRepository.save(comment);
    }
}
