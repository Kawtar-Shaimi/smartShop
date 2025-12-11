package com.demo.smartShop.controller;

import com.demo.smartShop.dto.response.UserDTO;
import com.demo.smartShop.entity.enums.UserRole;
import com.demo.smartShop.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private UserDTO adminUserDTO;
    private UserDTO clientUserDTO;
    private MockHttpSession mockSession;

    @BeforeEach
    void setUp() {
        // Créer un UserDTO pour un admin
        adminUserDTO = UserDTO.builder()
                .id(1L)
                .username("admin")
                .role(UserRole.ADMIN)
                .clientId(null)
                .build();

        // Créer un UserDTO pour un client
        clientUserDTO = UserDTO.builder()
                .id(2L)
                .username("clientuser")
                .role(UserRole.CLIENT)
                .clientId(10L)
                .build();

        mockSession = new MockHttpSession();
    }

    @Nested
    @DisplayName("POST /api/auth/login Tests")
    class LoginTests {

        @Test
        @DisplayName("Devrait retourner 200 OK lors d'un login admin réussi")
        void shouldReturnOkWhenAdminLoginSuccessful() throws Exception {
            // Arrange
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", "admin");
            credentials.put("password", "admin123");

            when(authService.login(eq("admin"), eq("admin123"), any(HttpSession.class)))
                    .thenReturn(adminUserDTO);

            // Act & Assert
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(credentials)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.username").value("admin"))
                    .andExpect(jsonPath("$.role").value("ADMIN"))
                    .andExpect(jsonPath("$.clientId").isEmpty());

            verify(authService).login(eq("admin"), eq("admin123"), any(HttpSession.class));
        }

        @Test
        @DisplayName("Devrait retourner 200 OK lors d'un login client réussi")
        void shouldReturnOkWhenClientLoginSuccessful() throws Exception {
            // Arrange
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", "clientuser");
            credentials.put("password", "password123");

            when(authService.login(eq("clientuser"), eq("password123"), any(HttpSession.class)))
                    .thenReturn(clientUserDTO);

            // Act & Assert
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(credentials)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2))
                    .andExpect(jsonPath("$.username").value("clientuser"))
                    .andExpect(jsonPath("$.role").value("CLIENT"))
                    .andExpect(jsonPath("$.clientId").value(10));

            verify(authService).login(eq("clientuser"), eq("password123"), any(HttpSession.class));
        }

        @Test
        @DisplayName("Devrait appeler authService avec les bons paramètres")
        void shouldCallAuthServiceWithCorrectParameters() throws Exception {
            // Arrange
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", "testuser");
            credentials.put("password", "testpass");

            when(authService.login(any(), any(), any())).thenReturn(adminUserDTO);

            // Act
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(credentials)));

            // Assert
            verify(authService).login(eq("testuser"), eq("testpass"), any(HttpSession.class));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/logout Tests")
    class LogoutTests {

        @Test
        @DisplayName("Devrait retourner 200 OK lors du logout")
        void shouldReturnOkWhenLogout() throws Exception {
            // Arrange
            doNothing().when(authService).logout(any());

            // Act & Assert
            mockMvc.perform(post("/api/auth/logout")
                    .session(mockSession))
                    .andExpect(status().isOk());

            verify(authService).logout(any());
        }

        @Test
        @DisplayName("Devrait appeler authService.logout")
        void shouldCallAuthServiceLogout() throws Exception {
            // Arrange
            doNothing().when(authService).logout(any());

            // Act
            mockMvc.perform(post("/api/auth/logout")
                    .session(mockSession));

            // Assert
            verify(authService, times(1)).logout(any());
        }
    }

    @Nested
    @DisplayName("GET /api/auth/me Tests")
    class GetCurrentUserTests {

        @Test
        @DisplayName("Devrait retourner l'utilisateur admin actuel")
        void shouldReturnCurrentAdminUser() throws Exception {
            // Arrange
            when(authService.getCurrentUser(any(HttpSession.class)))
                    .thenReturn(adminUserDTO);

            // Act & Assert
            mockMvc.perform(get("/api/auth/me")
                    .session(mockSession))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.username").value("admin"))
                    .andExpect(jsonPath("$.role").value("ADMIN"));

            verify(authService).getCurrentUser(any(HttpSession.class));
        }

        @Test
        @DisplayName("Devrait retourner l'utilisateur client actuel avec clientId")
        void shouldReturnCurrentClientUserWithClientId() throws Exception {
            // Arrange
            when(authService.getCurrentUser(any(HttpSession.class)))
                    .thenReturn(clientUserDTO);

            // Act & Assert
            mockMvc.perform(get("/api/auth/me")
                    .session(mockSession))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2))
                    .andExpect(jsonPath("$.username").value("clientuser"))
                    .andExpect(jsonPath("$.role").value("CLIENT"))
                    .andExpect(jsonPath("$.clientId").value(10));

            verify(authService).getCurrentUser(any(HttpSession.class));
        }
    }
}
