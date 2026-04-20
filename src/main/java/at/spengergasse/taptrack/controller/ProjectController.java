package at.spengergasse.taptrack.controller;

import at.spengergasse.taptrack.dto.ProjectDto;
import at.spengergasse.taptrack.model.Project;
import at.spengergasse.taptrack.service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public String listProjects(Model model, HttpServletRequest request) {
        model.addAttribute("projects", projectService.findAllProjects());
        model.addAttribute("currentUri", request.getRequestURI());
        return "projects/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("project", new ProjectDto());
        return "projects/create";
    }

    @PostMapping("/create")
    public String createProject(@ModelAttribute("project") ProjectDto projectDto, 
                                @AuthenticationPrincipal UserDetails userDetails) {
        projectService.createProject(projectDto, userDetails.getUsername());
        return "redirect:/dashboard";
    }

    @PostMapping("/join/{id}")
    public String joinProject(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        projectService.joinProject(id, userDetails.getUsername());
        return "redirect:/dashboard";
    }

    @GetMapping("/{id}")
    public String viewProject(@PathVariable Long id, Model model) {
        Project project = projectService.findById(id);
        model.addAttribute("project", project);
        return "projects/view";
    }
}
