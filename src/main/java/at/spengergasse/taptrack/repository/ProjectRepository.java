package at.spengergasse.taptrack.repository;

import at.spengergasse.taptrack.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUsers_Id(Long userId);
}
