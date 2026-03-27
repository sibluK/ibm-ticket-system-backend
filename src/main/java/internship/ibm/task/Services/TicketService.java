package internship.ibm.task.Services;

import internship.ibm.task.DTOs.TicketWithCommentDTO;
import internship.ibm.task.Models.Ticket;
import internship.ibm.task.Repositories.TicketRepository;
import internship.ibm.task.Exceptions.ResourceNotFoundException;
import internship.ibm.task.Utility.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final Mapper mapper;

    public TicketService(TicketRepository ticketRepository, Mapper mapper) {
        this.ticketRepository = ticketRepository;
        this.mapper = mapper;
    }

    public List<TicketWithCommentDTO> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(mapper::mapTicketWithCommentToDTO)
                .toList();
    }

    public TicketWithCommentDTO getTicketById(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found:" + ticketId));

        TicketWithCommentDTO mappedTicket = mapper.mapTicketWithCommentToDTO(ticket);
        System.out.println(mappedTicket);

        return mappedTicket;
    }
}
