package lirihiwa.dev.transcript;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranscriptRepository extends JpaRepository<Transcript, Long> {

    List<Transcript> findByMeetingIdOrderByStartOffsetAsc(Long meetingId);
}
