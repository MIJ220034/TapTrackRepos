package at.spengergasse.taptrack.service;

import at.spengergasse.taptrack.dto.ProjectDto;
import at.spengergasse.taptrack.model.Project;
import at.spengergasse.taptrack.model.User;
import at.spengergasse.taptrack.repository.ProjectRepository;
import at.spengergasse.taptrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public List<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> findProjectsByUser(Long userId) {
        return projectRepository.findByUsers_Id(userId);
    }

    @Transactional
    public void createProject(ProjectDto projectDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Benutzer '" + username + "' wurde nicht in der Datenbank gefunden. Bitte loggen Sie sich erneut ein."));
        
        Project project = Project.builder()
                .name(projectDto.getName())
                .description(projectDto.getDescription())
                .build();
        
        project.getUsers().add(user);
        projectRepository.save(project);
    }

    @Transactional
    public void joinProject(Long projectId, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projekt mit ID " + projectId + " wurde nicht gefunden."));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Benutzer '" + username + "' wurde nicht in der Datenbank gefunden. Bitte loggen Sie sich erneut ein."));
        
        project.getUsers().add(user);
        projectRepository.save(project);
    }

    public Project findById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }
}
