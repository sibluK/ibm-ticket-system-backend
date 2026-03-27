package internship.ibm.task.Controllers;

import internship.ibm.task.DTOs.TicketWithCommentDTO;
import internship.ibm.task.Services.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<TicketWithCommentDTO>> getAllTickets() {
        List<TicketWithCommentDTO> tickets = ticketService.getAllTickets();
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketWithCommentDTO> getTicketById(@PathVariable Long ticketId) {
        TicketWithCommentDTO ticket = ticketService.getTicketById(ticketId);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
     }
}
