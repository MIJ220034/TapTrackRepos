package at.spengergasse.taptrack.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToMany
    @JoinTable(
        name = "project_users",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> users = new HashSet<>();

    public void removeUser(User user) {
        this.users.remove(user);
        user.getProjects().remove(this);
    }

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<TimeEntry> timeEntries = new HashSet<>();

    public double getTotalDuration() {
        return timeEntries.stream()
                .filter(e -> e.getDuration() != null)
                .mapToDouble(TimeEntry::getDuration)
                .sum();
    }
}
