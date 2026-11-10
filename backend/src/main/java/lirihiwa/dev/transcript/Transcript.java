package lirihiwa.dev.transcript;

import jakarta.persistence.*;
import lirihiwa.dev.meeting.Meeting;
import lirihiwa.dev.speaker.Speaker;

@Entity
@Table(name = "transcripts")
public class Transcript {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "speaker_id", nullable = false)
    private Speaker speaker;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false)
    private double startOffset;

    @Column(nullable = false)
    private double endOffset;

    private Double confidence;

    public Transcript() {
    }

    public Transcript(Meeting meeting, Speaker speaker, String text,
                      double startOffset, double endOffset, Double confidence) {
        this.meeting = meeting;
        this.speaker = speaker;
        this.text = text;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.confidence = confidence;
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

    public Speaker getSpeaker() {
        return speaker;
    }

    public void setSpeaker(Speaker speaker) {
        this.speaker = speaker;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(double startOffset) {
        this.startOffset = startOffset;
    }

    public double getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(double endOffset) {
        this.endOffset = endOffset;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}
