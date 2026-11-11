package lirihiwa.dev.service;

import lirihiwa.dev.ai.llm.ExtractedTask;

public record ResolvedTask(
        ExtractedTask task,
        Long employeeId
) {
}
