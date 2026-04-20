package at.spengergasse.taptrack.service;

import at.spengergasse.taptrack.dto.UserDto;
import at.spengergasse.taptrack.model.User;
import at.spengergasse.taptrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(UserDto userDto) {
        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .role(User.Role.USER)
                .build();
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            // Remove user from projects to avoid constraint violations in project_users table
            for (at.spengergasse.taptrack.model.Project project : new java.util.HashSet<>(user.getProjects())) {
                project.removeUser(user);
            }
            userRepository.delete(user);
        }
    }
}
