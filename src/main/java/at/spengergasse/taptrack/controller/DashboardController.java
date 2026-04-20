package at.spengergasse.taptrack.controller;

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
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final ProjectService projectService;
    private final TimeEntryService timeEntryService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model, HttpServletRequest request) {
        User user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            return "redirect:/login?logout";
        }
        
        model.addAttribute("totalHours", timeEntryService.calculateTotalHours(user.getId()));
        
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeek = LocalDate.now().get(weekFields.weekOfWeekBasedYear());
        model.addAttribute("weeklyHours", timeEntryService.calculateWeeklyHours(user.getId(), currentWeek));
        
        model.addAttribute("projects", projectService.findProjectsByUser(user.getId()));
        model.addAttribute("recentEntries", timeEntryService.findRecentEntriesByUser(user.getId()));
        model.addAttribute("user", user);
        model.addAttribute("currentUri", request.getRequestURI());
        
        return "dashboard";
    }
}
