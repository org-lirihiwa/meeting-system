package lirihiwa.dev.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByMeetingId(Long meetingId);

    void deleteByMeetingId(Long meetingId);
}
