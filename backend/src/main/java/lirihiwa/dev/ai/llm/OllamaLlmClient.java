package lirihiwa.dev.ai.llm;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class OllamaLlmClient implements LlmClient {

    private final RestClient restClient;
    private final String model;

    public OllamaLlmClient(@Value("${llm.ollama.url}") String url,
                           @Value("${llm.ollama.model}") String model) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofMinutes(15));
        this.restClient = RestClient.builder()
                .baseUrl(url)
                .requestFactory(factory)
                .build();
        this.model = model;
    }

    @Override
    public String generateJson(String systemPrompt, String userPrompt) {
        Map<String, Object> request = Map.of(
                "model", model,
                "stream", false,
                "format", "json",
                "options", Map.of("temperature", 0, "num_ctx", 8192),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                )
        );

        JsonNode response = restClient.post()
                .uri("/api/chat")
                .body(request)
                .retrieve()
                .body(JsonNode.class);

        if (response == null || response.path("message").path("content").isMissingNode()) {
            throw new IllegalStateException("Empty response from Ollama");
        }
        return response.path("message").path("content").asText();
    }
}
