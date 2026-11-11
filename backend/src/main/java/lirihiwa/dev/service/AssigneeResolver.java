package lirihiwa.dev.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lirihiwa.dev.ai.llm.ExtractedTask;
import lirihiwa.dev.ai.llm.LlmClient;
import lirihiwa.dev.employee.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AssigneeResolver {

    private static final Logger log = LoggerFactory.getLogger(AssigneeResolver.class);

    private static final String SYSTEM_PROMPT = """
            Ты помощник секретаря. Нужно выбрать ответственного за поручение из списка кандидатов.
            Правила:
            - Учитывай имя из речи, территорию, тему и направление деятельности кандидатов.
            - Имя из речи могло быть искажено распознаванием или сказано кратко ("Маша" = "Мария", "Саша" = "Александр" или "Александра").
            - Если поручение относится к территории или направлению, выбирай кандидата, который за них отвечает.
            - Выбирай только из списка.
            - Если в речи названо только имя и по поручению нельзя различить нескольких кандидатов с таким именем (нет территории, темы или направления), верни null.
            - Краткая форма имени подходит, если она соответствует полному имени кандидата и такой кандидат один.
            Верни только JSON такого вида: {"employeeId": 7} или {"employeeId": null}
            """;

    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;
    private final EmployeeMatcher employeeMatcher;

    public AssigneeResolver(LlmClient llmClient, ObjectMapper objectMapper, EmployeeMatcher employeeMatcher) {
        this.llmClient = llmClient;
        this.objectMapper = objectMapper;
        this.employeeMatcher = employeeMatcher;
    }

    public Optional<Employee> resolve(ExtractedTask task) {
        List<Employee> candidates = employeeMatcher.findCandidates(task.assignee());
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        if (candidates.size() == 1) {
            return Optional.of(candidates.get(0));
        }

        try {
            String json = llmClient.generateJson(SYSTEM_PROMPT, buildPrompt(task, candidates));
            log.info("Assignee LLM response for '{}': {}", task.assignee(), json);
            JsonNode idNode = objectMapper.readTree(json).path("employeeId");
            if (idNode.isMissingNode() || idNode.isNull() || !idNode.canConvertToLong()) {
                return Optional.empty();
            }
            long id = idNode.asLong();
            return candidates.stream().filter(e -> e.getId() == id).findFirst();
        } catch (Exception e) {
            log.warn("Failed to resolve assignee for task '{}'", task.text(), e);
            return Optional.empty();
        }
    }

    private String buildPrompt(ExtractedTask task, List<Employee> candidates) {
        StringBuilder sb = new StringBuilder();
        sb.append("Поручение: ").append(task.text()).append('\n');
        sb.append("Ответственный в речи: ").append(task.assignee()).append('\n');
        sb.append("Тема: ").append(task.topic()).append('\n');
        sb.append("Территория: ").append(task.territory()).append("\n\n");
        sb.append("Кандидаты:\n");
        for (Employee e : candidates) {
            sb.append("- id=").append(e.getId())
                    .append(", ").append(e.getLastName()).append(' ').append(e.getFirstName())
                    .append(' ').append(e.getPatronymic() == null ? "" : e.getPatronymic())
                    .append(", должность: ").append(e.getPosition())
                    .append(", организация: ").append(e.getOrganization())
                    .append(", территория: ").append(e.getTerritory())
                    .append(", направление: ").append(e.getScope())
                    .append('\n');
        }
        return sb.toString();
    }
}
