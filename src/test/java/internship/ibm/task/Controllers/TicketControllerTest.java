package internship.ibm.task.Controllers;

import internship.ibm.task.DTOs.TicketWithCommentDTO;
import internship.ibm.task.Enums.TicketCategory;
import internship.ibm.task.Enums.TicketPriority;
import internship.ibm.task.Services.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(controllers = TicketController.class)
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService ticketService;

    @Test
    void whenRequested_thenReturnsTickets() throws Exception {
        TicketWithCommentDTO ticket1 = new TicketWithCommentDTO(
                1L,
                "ticket-title-1",
                TicketCategory.OTHER,
                TicketPriority.LOW,
                "ticket-summary-1",
                Instant.now(),
                null
        );

        TicketWithCommentDTO ticket2 = new TicketWithCommentDTO(
                1L,
                "ticket-title-2",
                TicketCategory.OTHER,
                TicketPriority.LOW,
                "ticket-summary-2",
                Instant.now(),
                null
        );

        List<TicketWithCommentDTO> tickets = List.of(ticket1, ticket2);

        when(ticketService.getAllTickets()).thenReturn(tickets);

        mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(tickets.size()))
                .andExpect(jsonPath("$[0].id").value(1));


        verify(ticketService).getAllTickets();
    }
}
