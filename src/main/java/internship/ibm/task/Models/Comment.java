package internship.ibm.task.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import internship.ibm.task.Enums.CommentState;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private Instant createdAt;
    @Enumerated(EnumType.STRING)
    private CommentState state;
    private boolean ticketCreated;

    @OneToOne(mappedBy = "comment")
    @JsonIgnore
    private Ticket ticket;
}
