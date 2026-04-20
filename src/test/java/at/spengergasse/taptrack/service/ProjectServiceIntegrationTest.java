package at.spengergasse.taptrack.service;

import at.spengergasse.taptrack.dto.ProjectDto;
import at.spengergasse.taptrack.model.User;
import at.spengergasse.taptrack.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProjectServiceIntegrationTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createProject_WithExistingUser_ShouldWorkCorrectly() {
        // Arrange
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(User.Role.USER)
                .build();
        userRepository.save(user);

        ProjectDto projectDto = ProjectDto.builder()
                .name("Test Project")
                .description("Test Description")
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> projectService.createProject(projectDto, "testuser"));
        
        var projects = projectService.findProjectsByUser(user.getId());
        assertEquals(1, projects.size());
        assertEquals("Test Project", projects.get(0).getName());
    }

    @Test
    void createProject_WithNonExistingUser_ShouldThrowException() {
        // Arrange
        ProjectDto projectDto = ProjectDto.builder()
                .name("Test Project")
                .build();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            projectService.createProject(projectDto, "nonexistent")
        );
        assertTrue(exception.getMessage().contains("Benutzer 'nonexistent' wurde nicht in der Datenbank gefunden"));
    }
}
