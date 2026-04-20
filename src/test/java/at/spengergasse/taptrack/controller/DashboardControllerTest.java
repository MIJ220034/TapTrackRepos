package at.spengergasse.taptrack.controller;

import at.spengergasse.taptrack.service.ProjectService;
import at.spengergasse.taptrack.service.TimeEntryService;
import at.spengergasse.taptrack.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class DashboardControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private TimeEntryService timeEntryService;

    @Test
    @WithMockUser(username = "nonexistentuser")
    void dashboard_WithNonExistentUser_ShouldRedirectToLogin() throws Exception {
        // Arrange
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        when(userService.findByUsername(anyString())).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    void index_ShouldReturnLandingPage() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}
