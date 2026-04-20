package at.spengergasse.taptrack.repository;

import at.spengergasse.taptrack.model.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    List<TimeEntry> findByUserId(Long userId);
    List<TimeEntry> findByUserIdAndProjectId(Long userId, Long projectId);
    List<TimeEntry> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
