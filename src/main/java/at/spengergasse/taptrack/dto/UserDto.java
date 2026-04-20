package at.spengergasse.taptrack.dto;

import at.spengergasse.taptrack.model.User.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Role role;
}
