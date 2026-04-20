package at.spengergasse.taptrack.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeEntryDto {
    private Long id;
    private Long userId;
    private Long projectId;
    private String projectName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double duration;
    private Integer calendarWeek;
    private String projectPhase;
    private String workArea;
    private String description;
}
