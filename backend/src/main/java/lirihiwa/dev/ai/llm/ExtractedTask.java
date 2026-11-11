package lirihiwa.dev.ai.llm;

public record ExtractedTask(
        String assignee,
        String text,
        String topic,
        String territory,
        String deadline,
        Integer sourceIndex
) {
}
