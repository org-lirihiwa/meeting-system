package lirihiwa.dev.task;

import jakarta.persistence.*;
import lirihiwa.dev.employee.Employee;
import lirihiwa.dev.meeting.Meeting;
import lirihiwa.dev.transcript.Transcript;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transcript_id")
    private Transcript transcript;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private String rawAssigneeText;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    private String topic;

    private String territory;

    private String deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column(nullable = false)
    private boolean emailSent;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Task() {
    }

    public Task(Meeting meeting, Transcript transcript, String rawAssigneeText, String text,
                String topic, String territory, String deadline) {
        this.meeting = meeting;
        this.transcript = transcript;
        this.rawAssigneeText = rawAssigneeText;
        this.text = text;
        this.topic = topic;
        this.territory = territory;
        this.deadline = deadline;
        this.status = TaskStatus.DRAFT;
        this.emailSent = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public Transcript getTranscript() {
        return transcript;
    }

    public void setTranscript(Transcript transcript) {
        this.transcript = transcript;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getRawAssigneeText() {
        return rawAssigneeText;
    }

    public void setRawAssigneeText(String rawAssigneeText) {
        this.rawAssigneeText = rawAssigneeText;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTerritory() {
        return territory;
    }

    public void setTerritory(String territory) {
        this.territory = territory;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
