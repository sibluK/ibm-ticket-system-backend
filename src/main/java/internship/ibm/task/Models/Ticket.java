package internship.ibm.task.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import internship.ibm.task.Enums.TicketCategory;
import internship.ibm.task.Enums.TicketPriority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "comment")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Enumerated(EnumType.STRING)
    private TicketCategory category;
    @Enumerated(EnumType.STRING)
    private TicketPriority priority;
    @Column(length = 1000)
    private String summary;
    private Instant createdAt;

    @OneToOne
    @JoinColumn(name = "comment_id")
    @JsonIgnore
    private Comment comment;
}
