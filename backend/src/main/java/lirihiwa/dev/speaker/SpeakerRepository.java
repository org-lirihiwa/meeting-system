package lirihiwa.dev.speaker;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpeakerRepository extends JpaRepository<Speaker, Long> {

    List<Speaker> findByMeetingId(Long meetingId);

    void deleteByMeetingId(Long meetingId);
}
