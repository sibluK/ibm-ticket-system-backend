package internship.ibm.task.Services;

import internship.ibm.task.DTOs.AnalysisResult;
import internship.ibm.task.Enums.CommentState;
import internship.ibm.task.Models.Comment;
import internship.ibm.task.Models.Ticket;
import internship.ibm.task.Repositories.CommentRepository;
import internship.ibm.task.Repositories.TicketRepository;
import internship.ibm.task.Exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class HuggingFaceService {

    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final CommentStateService commentStateService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${huggingface.access.token}")
    private String accessToken;

    @Value("${huggingface.text.api.url}")
    private String textApiUrl;

    @Value("${huggingface.model}")
    private String model;

    public HuggingFaceService(
            TicketRepository ticketRepository,
            CommentRepository commentRepository, CommentStateService commentStateService,
            RestTemplate restTemplate,
            ObjectMapper objectMapper
    ) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
        this.commentStateService = commentStateService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Async("threadPoolTaskExecutor")
    @Transactional
    public void analyzeCommentAsync(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Comment not found:"  + commentId));

        try {
            AnalysisResult result = sendAnalyzeRequest(comment.getText());

            if (!result.shouldCreateTicket()) {
                comment.setState(CommentState.COMPLETED);
                comment.setTicketCreated(false);
                commentRepository.save(comment);
                return;
            }

            if (result.ticketAnalysis() == null) {
                comment.setState(CommentState.FAILED);
                comment.setTicketCreated(false);
                commentRepository.save(comment);
                return;
            }

            Ticket ticket = new Ticket();
            ticket.setTitle(result.ticketAnalysis().title());
            ticket.setCategory(result.ticketAnalysis().category());
            ticket.setPriority(result.ticketAnalysis().priority());
            ticket.setSummary(result.ticketAnalysis().summary());
            ticket.setCreatedAt(Instant.now());
            ticket.setComment(comment);
            ticketRepository.save(ticket);

            comment.setTicket(ticket);
            comment.setState(CommentState.COMPLETED);
            comment.setTicketCreated(true);
            commentRepository.save(comment);
        } catch (Exception e) {
            commentStateService.markCommentFailed(commentId);
        }
    }

    public AnalysisResult sendAnalyzeRequest(String commentText) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = getMapHttpEntity(commentText, headers);

        ResponseEntity<ChatCompletionResponse> response = restTemplate.postForEntity(
                textApiUrl,
                requestEntity,
                ChatCompletionResponse.class
        );

        ChatCompletionResponse responseBody = response.getBody();

        if (responseBody == null || responseBody.choices() == null || responseBody.choices().isEmpty()) {
            throw new IllegalStateException("Empty AI response");
        }

        String content = responseBody.choices().get(0).message().content();
        return objectMapper.readValue(content, AnalysisResult.class);
    }

    private @NonNull HttpEntity<Map<String, Object>> getMapHttpEntity(String commentText, HttpHeaders headers) {
        String systemPrompt = """
            You are a support triage assistant.
            Analyze the user comment and decide if a support ticket should be created.
            Return ONLY valid JSON. No explanation text.

            Allowed categories: BUG, FEATURE, BILLING, ACCOUNT, OTHER
            Allowed priorities: LOW, MEDIUM, HIGH

            JSON schema:
            {
              "shouldCreateTicket": boolean,
              "ticketAnalysis": null OR {
                "title": "string",
                "category": "BUG|FEATURE|BILLING|ACCOUNT|OTHER",
                "priority": "LOW|MEDIUM|HIGH",
                "summary": "string"
              }
            }
            """;

        Map<String, Object> body = Map.of(
            "model", model,
            "messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", "User comment: " + commentText)
                ),
                "temperature", 0.1
        );

        return new HttpEntity<>(body, headers);
    }

    public record ChatCompletionResponse(List<Choice> choices) {}
    public record Choice(Message message) {}
    public record Message(String role, String content) {}
}
