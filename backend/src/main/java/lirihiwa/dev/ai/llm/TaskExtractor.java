package lirihiwa.dev.ai.llm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class TaskExtractor {

    private static final String SYSTEM_PROMPT = """
            Ты помощник секретаря. Из расшифровки совещания выдели поручения: \
            кому, что и к какому сроку нужно сделать.
            Правила:
            - Включай только явные поручения, у которых есть действие. Обсуждения и мнения не включай.
            - Распознавание речи могло исказить слова, исправляй очевидные ошибки по смыслу.
            - assignee: имя или фамилия ответственного так, как они названы в речи. Если не названо, null.
            - text: суть поручения одним предложением.
            - topic: тема одним-двумя словами или null.
            - territory: территория или объект, если упомянуты, иначе null.
            - deadline: срок так, как он назван в речи, иначе null.
            - sourceIndex: номер реплики в квадратных скобках, в которой дано поручение.
            Верни только JSON строго такого вида:
            {"tasks":[{"assignee":"...","text":"...","topic":"...","territory":null,"deadline":"...","sourceIndex":0}]}
            Если поручений нет, верни {"tasks":[]}.
            """;

    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;

    public TaskExtractor(LlmClient llmClient, ObjectMapper objectMapper) {
        this.llmClient = llmClient;
        this.objectMapper = objectMapper;
    }

    /**
     * lines: реплики в хронологическом порядке, уже в виде "Диктор 1: текст".
     */
    public List<ExtractedTask> extract(List<String> lines) {
        StringBuilder transcript = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            transcript.append('[').append(i).append("] ").append(lines.get(i)).append('\n');
        }

        String json = llmClient.generateJson(SYSTEM_PROMPT, transcript.toString());
        try {
            JsonNode tasks = objectMapper.readTree(json).path("tasks");
            if (!tasks.isArray()) {
                return Collections.emptyList();
            }
            return objectMapper.convertValue(tasks, new TypeReference<List<ExtractedTask>>() {
            });
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse LLM response: " + json, e);
        }
    }
}
