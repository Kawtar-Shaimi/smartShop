package com.demo.smartShop.controller;

import com.demo.smartShop.entity.Client;
import com.demo.smartShop.entity.User;
import com.demo.smartShop.entity.enums.CustomerTier;
import com.demo.smartShop.entity.enums.UserRole;
import com.demo.smartShop.repository.ClientRepository;
import com.demo.smartShop.repository.UserRepository;
import com.demo.smartShop.util.PasswordUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour AuthController
 * 
 * Ces tests vérifient le flux complet d'authentification avec:
 * - La vraie base de données (H2 en mémoire)
 * - Les vrais services
 * - La gestion des sessions
 * 
 * Différence avec les tests unitaires:
 * - Tests unitaires: on mock les dépendances
 * - Tests d'intégration: on utilise les vraies dépendances
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("AuthController - Tests d'Intégration")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    private User testAdminUser;
    private User testClientUser;
    private Client testClient;

    @BeforeEach
    void setUp() {
        // Créer un utilisateur ADMIN pour les tests
        testAdminUser = User.builder()
                .username("testadmin")
                .password(PasswordUtil.hash("adminpass123"))
                .role(UserRole.ADMIN)
                .build();
        testAdminUser = userRepository.save(testAdminUser);

        // Créer un client et son utilisateur CLIENT
        testClient = Client.builder()
                .nom("Test Client")
                .email("testclient@example.com")
                .tier(CustomerTier.BASIC)
                .build();
        testClient = clientRepository.save(testClient);

        testClientUser = User.builder()
                .username("testclient")
                .password(PasswordUtil.hash("clientpass123"))
                .role(UserRole.CLIENT)
                .client(testClient)
                .build();
        testClientUser = userRepository.save(testClientUser);
    }

    @Nested
    @DisplayName("Tests de Login")
    class LoginIntegrationTests {

        @Test
        @DisplayName("Login admin réussi - devrait créer une session et retourner les infos utilisateur")
        void shouldLoginAdminSuccessfully() throws Exception {
            // Arrange
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", "testadmin");
            credentials.put("password", "adminpass123");

            // Act & Assert
            MvcResult result = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(credentials)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("testadmin"))
                    .andExpect(jsonPath("$.role").value("ADMIN"))
                    .andReturn();

            // Vérifier que la session est créée
            MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
            assertNotNull(session);
            assertEquals(testAdminUser.getId(), session.getAttribute("USER_ID"));
            assertEquals("testadmin", session.getAttribute("USERNAME"));
            assertEquals("ADMIN", session.getAttribute("USER_ROLE"));
        }

        @Test
        @DisplayName("Login client réussi - devrait inclure clientId dans la session")
        void shouldLoginClientSuccessfullyWithClientId() throws Exception {
            // Arrange
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", "testclient");
            credentials.put("password", "clientpass123");

            // Act & Assert
            MvcResult result = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(credentials)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("testclient"))
                    .andExpect(jsonPath("$.role").value("CLIENT"))
                    .andExpect(jsonPath("$.clientId").value(testClient.getId()))
                    .andReturn();

            // Vérifier que CLIENT_ID est dans la session
            MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
            assertEquals(testClient.getId(), session.getAttribute("CLIENT_ID"));
        }

        @Test
        @DisplayName("Login échoué - mot de passe incorrect")
        void shouldFailLoginWithWrongPassword() throws Exception {
            // Arrange
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", "testadmin");
            credentials.put("password", "wrongpassword");

            // Act & Assert
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(credentials)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Login échoué - utilisateur inexistant")
        void shouldFailLoginWithNonExistentUser() throws Exception {
            // Arrange
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", "nonexistent");
            credentials.put("password", "somepassword");

            // Act & Assert
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(credentials)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Tests de Logout")
    class LogoutIntegrationTests {

        @Test
        @DisplayName("Logout réussi après login")
        void shouldLogoutSuccessfully() throws Exception {
            // D'abord se connecter
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", "testadmin");
            credentials.put("password", "adminpass123");

            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(credentials)))
                    .andExpect(status().isOk())
                    .andReturn();

            MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();

            // Puis se déconnecter
            mockMvc.perform(post("/api/auth/logout")
                    .session(session))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Tests de /me (utilisateur actuel)")
    class GetCurrentUserIntegrationTests {

        @Test
        @DisplayName("Devrait retourner l'utilisateur actuel après login")
        void shouldReturnCurrentUserAfterLogin() throws Exception {
            // D'abord se connecter
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", "testadmin");
            credentials.put("password", "adminpass123");

            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(credentials)))
                    .andExpect(status().isOk())
                    .andReturn();

            MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();

            // Puis appeler /me
            mockMvc.perform(get("/api/auth/me")
                    .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("testadmin"))
                    .andExpect(jsonPath("$.role").value("ADMIN"));
        }

        @Test
        @DisplayName("Flux complet: Login -> /me -> Logout")
        void shouldCompleteFullAuthenticationFlow() throws Exception {
            // 1. Login
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", "testclient");
            credentials.put("password", "clientpass123");

            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(credentials)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.role").value("CLIENT"))
                    .andReturn();

            MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();

            // 2. Get current user
            mockMvc.perform(get("/api/auth/me")
                    .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("testclient"))
                    .andExpect(jsonPath("$.clientId").value(testClient.getId()));

            // 3. Logout
            mockMvc.perform(post("/api/auth/logout")
                    .session(session))
                    .andExpect(status().isOk());
        }
    }
}
