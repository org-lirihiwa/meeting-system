package lirihiwa.dev.service;

import lirihiwa.dev.task.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;
    private final String from;

    public NotificationService(JavaMailSender mailSender, @Value("${app.mail.from}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    /**
     * Отправляет письмо ответственному за поручение. Вызывать только для задач с назначенным сотрудником.
     */
    public void sendTaskAssigned(Task task) {
        var employee = task.getEmployee();
        if (employee == null) {
            throw new IllegalStateException("Task " + task.getId() + " has no assignee");
        }

        StringBuilder body = new StringBuilder();
        body.append("Здравствуйте, ").append(employee.getFirstName());
        if (employee.getPatronymic() != null && !employee.getPatronymic().isBlank()) {
            body.append(' ').append(employee.getPatronymic());
        }
        body.append("!\n\n");
        body.append("По итогам совещания «").append(task.getMeeting().getTitle())
                .append("» вам назначено поручение:\n\n");
        body.append(task.getText()).append("\n\n");
        if (task.getDeadline() != null && !task.getDeadline().isBlank()) {
            body.append("Срок: ").append(task.getDeadline()).append('\n');
        }
        if (task.getTerritory() != null && !task.getTerritory().isBlank()) {
            body.append("Территория/объект: ").append(task.getTerritory()).append('\n');
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(employee.getEmail());
        message.setSubject("Новое поручение: " + task.getMeeting().getTitle());
        message.setText(body.toString());
        mailSender.send(message);
    }
}
