package at.spengergasse.taptrack.controller;

import at.spengergasse.taptrack.model.User;
import at.spengergasse.taptrack.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model, HttpServletRequest request) {
        User user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            return "redirect:/login?logout";
        }
        model.addAttribute("user", user);
        model.addAttribute("currentUri", request.getRequestURI());
        return "profile";
    }

    @PostMapping("/delete")
    public String deleteAccount(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) throws Exception {
        User user = userService.findByUsername(userDetails.getUsername());
        if (user != null) {
            userService.deleteUser(user.getId());
            request.logout();
            return "redirect:/login?deleted";
        }
        return "redirect:/dashboard";
    }
}
