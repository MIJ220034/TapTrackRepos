package at.spengergasse.taptrack.service;

import at.spengergasse.taptrack.dto.TimeEntryDto;
import at.spengergasse.taptrack.model.Project;
import at.spengergasse.taptrack.model.TimeEntry;
import at.spengergasse.taptrack.model.User;
import at.spengergasse.taptrack.repository.ProjectRepository;
import at.spengergasse.taptrack.repository.TimeEntryRepository;
import at.spengergasse.taptrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public List<TimeEntry> findEntriesByUser(Long userId) {
        return timeEntryRepository.findByUserId(userId);
    }

    public List<TimeEntry> findRecentEntriesByUser(Long userId) {
        // Simple implementation: return all and let the controller handle limiting or implement a custom query
        return timeEntryRepository.findByUserId(userId);
    }

    @Transactional
    public void saveEntry(TimeEntryDto dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Benutzer '" + username + "' wurde nicht gefunden."));
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Projekt mit ID " + dto.getProjectId() + " wurde nicht gefunden."));

        TimeEntry entry;
        if (dto.getId() != null) {
            entry = timeEntryRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Zeiteintrag mit ID " + dto.getId() + " wurde nicht gefunden."));
            // Security check: only own entries
            if (!entry.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Zugriff verweigert.");
            }
        } else {
            entry = new TimeEntry();
            entry.setUser(user);
        }

        entry.setProject(project);
        entry.setDate(dto.getDate());
        entry.setStartTime(dto.getStartTime());
        entry.setEndTime(dto.getEndTime());
        entry.setProjectPhase(dto.getProjectPhase());
        entry.setWorkArea(dto.getWorkArea());
        entry.setDescription(dto.getDescription());

        // Calculations
        if (entry.getStartTime() != null && entry.getEndTime() != null) {
            long minutes = Duration.between(entry.getStartTime(), entry.getEndTime()).toMinutes();
            entry.setDuration(minutes / 60.0);
        }
        
        if (entry.getDate() != null) {
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            entry.setCalendarWeek(entry.getDate().get(weekFields.weekOfWeekBasedYear()));
        }

        timeEntryRepository.save(entry);
    }

    @Transactional
    public void deleteEntry(Long id, String username) {
        TimeEntry entry = timeEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));
        if (!entry.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Access denied");
        }
        timeEntryRepository.delete(entry);
    }

    public TimeEntry findById(Long id) {
        return timeEntryRepository.findById(id).orElse(null);
    }

    public double calculateTotalHours(Long userId) {
        return timeEntryRepository.findByUserId(userId).stream()
                .filter(e -> e.getDuration() != null)
                .mapToDouble(TimeEntry::getDuration)
                .sum();
    }

    public double calculateWeeklyHours(Long userId, int week) {
        return timeEntryRepository.findByUserId(userId).stream()
                .filter(e -> e.getCalendarWeek() != null && e.getCalendarWeek() == week)
                .filter(e -> e.getDuration() != null)
                .mapToDouble(TimeEntry::getDuration)
                .sum();
    }
}
