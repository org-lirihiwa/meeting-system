package lirihiwa.dev.ai.llm;

public interface LlmClient {

    /**
     * Отправляет запрос в языковую модель и возвращает ответ в виде JSON-строки.
     */
    String generateJson(String systemPrompt, String userPrompt);
}
