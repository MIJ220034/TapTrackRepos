package at.spengergasse.taptrack.controller;

import at.spengergasse.taptrack.dto.TimeEntryDto;
import at.spengergasse.taptrack.model.TimeEntry;
import at.spengergasse.taptrack.model.User;
import at.spengergasse.taptrack.service.ProjectService;
import at.spengergasse.taptrack.service.TimeEntryService;
import at.spengergasse.taptrack.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/time-entries")
@RequiredArgsConstructor
public class TimeEntryController {

    private final TimeEntryService timeEntryService;
    private final ProjectService projectService;
    private final UserService userService;

    @GetMapping("/create")
    public String showCreateForm(@RequestParam(required = false) Long projectId, 
                                 @AuthenticationPrincipal UserDetails userDetails, 
                                 Model model,
                                 HttpServletRequest request) {
        User user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            return "redirect:/login?logout";
        }
        TimeEntryDto dto = TimeEntryDto.builder()
                .date(LocalDate.now())
                .startTime(LocalTime.now().withSecond(0).withNano(0))
                .projectId(projectId)
                .build();
        
        model.addAttribute("entry", dto);
        model.addAttribute("projects", projectService.findProjectsByUser(user.getId()));
        model.addAttribute("currentUri", request.getRequestURI());
        return "time-entries/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, 
                               @AuthenticationPrincipal UserDetails userDetails, 
                               Model model,
                               HttpServletRequest request) {
        User user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            return "redirect:/login?logout";
        }
        TimeEntry entry = timeEntryService.findById(id);
        
        if (!entry.getUser().getId().equals(user.getId())) {
            return "redirect:/dashboard";
        }

        TimeEntryDto dto = TimeEntryDto.builder()
                .id(entry.getId())
                .projectId(entry.getProject().getId())
                .date(entry.getDate())
                .startTime(entry.getStartTime())
                .endTime(entry.getEndTime())
                .projectPhase(entry.getProjectPhase())
                .workArea(entry.getWorkArea())
                .description(entry.getDescription())
                .build();

        model.addAttribute("entry", dto);
        model.addAttribute("projects", projectService.findProjectsByUser(user.getId()));
        model.addAttribute("currentUri", request.getRequestURI());
        return "time-entries/form";
    }

    @PostMapping("/save")
    public String saveEntry(@ModelAttribute("entry") TimeEntryDto dto, 
                            @AuthenticationPrincipal UserDetails userDetails) {
        timeEntryService.saveEntry(dto, userDetails.getUsername());
        return "redirect:/dashboard";
    }

    @GetMapping("/delete/{id}")
    public String deleteEntry(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        timeEntryService.deleteEntry(id, userDetails.getUsername());
        return "redirect:/dashboard";
    }
}
