package com.demo.smartShop.service.impl;

import com.demo.smartShop.entity.Client;
import com.demo.smartShop.entity.User;
import com.demo.smartShop.entity.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpSession;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour SessionServiceImpl
 * 
 * Ces tests vérifient le bon fonctionnement de la gestion des sessions
 * utilisateur.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SessionServiceImpl Tests")
class SessionServiceImplTest {

    @InjectMocks
    private SessionServiceImpl sessionService;

    @Mock
    private HttpSession session;

    private User adminUser;
    private User clientUser;
    private Client client;

    @BeforeEach
    void setUp() {
        // Créer un client pour les tests
        client = Client.builder()
                .id(10L)
                .nom("Test Client")
                .email("client@test.com")
                .build();

        // Créer un utilisateur ADMIN (sans client associé)
        adminUser = User.builder()
                .id(1L)
                .username("admin")
                .password("hashedPassword")
                .role(UserRole.ADMIN)
                .client(null)
                .build();

        // Créer un utilisateur CLIENT (avec client associé)
        clientUser = User.builder()
                .id(2L)
                .username("clientuser")
                .password("hashedPassword")
                .role(UserRole.CLIENT)
                .client(client)
                .build();
    }

    @Nested
    @DisplayName("createSession() Tests")
    class CreateSessionTests {

        @Test
        @DisplayName("Devrait créer une session pour un ADMIN sans client")
        void shouldCreateSessionForAdmin() {
            // Act
            sessionService.createSession(session, adminUser);

            // Assert
            verify(session).setAttribute("USER_ID", 1L);
            verify(session).setAttribute("USERNAME", "admin");
            verify(session).setAttribute("USER_ROLE", "ADMIN");
            verify(session, never()).setAttribute(eq("CLIENT_ID"), any());
        }

        @Test
        @DisplayName("Devrait créer une session pour un CLIENT avec client_id")
        void shouldCreateSessionForClientWithClientId() {
            // Act
            sessionService.createSession(session, clientUser);

            // Assert
            verify(session).setAttribute("USER_ID", 2L);
            verify(session).setAttribute("USERNAME", "clientuser");
            verify(session).setAttribute("USER_ROLE", "CLIENT");
            verify(session).setAttribute("CLIENT_ID", 10L);
        }
    }

    @Nested
    @DisplayName("invalidateSession() Tests")
    class InvalidateSessionTests {

        @Test
        @DisplayName("Devrait invalider la session")
        void shouldInvalidateSession() {
            // Act
            sessionService.invalidateSession(session);

            // Assert
            verify(session).invalidate();
        }
    }

    @Nested
    @DisplayName("getCurrentUserId() Tests")
    class GetCurrentUserIdTests {

        @Test
        @DisplayName("Devrait retourner l'ID utilisateur si présent (Long)")
        void shouldReturnUserIdWhenPresentAsLong() {
            // Arrange
            when(session.getAttribute("USER_ID")).thenReturn(1L);

            // Act
            Optional<Long> result = sessionService.getCurrentUserId(session);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(1L, result.get());
        }

        @Test
        @DisplayName("Devrait retourner l'ID utilisateur si présent (Integer)")
        void shouldReturnUserIdWhenPresentAsInteger() {
            // Arrange
            when(session.getAttribute("USER_ID")).thenReturn(1);

            // Act
            Optional<Long> result = sessionService.getCurrentUserId(session);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(1L, result.get());
        }

        @Test
        @DisplayName("Devrait retourner Optional.empty si pas d'ID utilisateur")
        void shouldReturnEmptyWhenNoUserId() {
            // Arrange
            when(session.getAttribute("USER_ID")).thenReturn(null);

            // Act
            Optional<Long> result = sessionService.getCurrentUserId(session);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getCurrentClientId() Tests")
    class GetCurrentClientIdTests {

        @Test
        @DisplayName("Devrait retourner l'ID client si présent (Long)")
        void shouldReturnClientIdWhenPresentAsLong() {
            // Arrange
            when(session.getAttribute("CLIENT_ID")).thenReturn(10L);

            // Act
            Optional<Long> result = sessionService.getCurrentClientId(session);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(10L, result.get());
        }

        @Test
        @DisplayName("Devrait retourner l'ID client si présent (Integer)")
        void shouldReturnClientIdWhenPresentAsInteger() {
            // Arrange
            when(session.getAttribute("CLIENT_ID")).thenReturn(10);

            // Act
            Optional<Long> result = sessionService.getCurrentClientId(session);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(10L, result.get());
        }

        @Test
        @DisplayName("Devrait retourner Optional.empty si pas d'ID client")
        void shouldReturnEmptyWhenNoClientId() {
            // Arrange
            when(session.getAttribute("CLIENT_ID")).thenReturn(null);

            // Act
            Optional<Long> result = sessionService.getCurrentClientId(session);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getCurrentUserRole() Tests")
    class GetCurrentUserRoleTests {

        @Test
        @DisplayName("Devrait retourner le rôle ADMIN")
        void shouldReturnAdminRole() {
            // Arrange
            when(session.getAttribute("USER_ROLE")).thenReturn("ADMIN");

            // Act
            Optional<UserRole> result = sessionService.getCurrentUserRole(session);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(UserRole.ADMIN, result.get());
        }

        @Test
        @DisplayName("Devrait retourner le rôle CLIENT")
        void shouldReturnClientRole() {
            // Arrange
            when(session.getAttribute("USER_ROLE")).thenReturn("CLIENT");

            // Act
            Optional<UserRole> result = sessionService.getCurrentUserRole(session);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(UserRole.CLIENT, result.get());
        }

        @Test
        @DisplayName("Devrait retourner Optional.empty si rôle invalide")
        void shouldReturnEmptyForInvalidRole() {
            // Arrange
            when(session.getAttribute("USER_ROLE")).thenReturn("INVALID_ROLE");

            // Act
            Optional<UserRole> result = sessionService.getCurrentUserRole(session);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Devrait retourner Optional.empty si pas de rôle")
        void shouldReturnEmptyWhenNoRole() {
            // Arrange
            when(session.getAttribute("USER_ROLE")).thenReturn(null);

            // Act
            Optional<UserRole> result = sessionService.getCurrentUserRole(session);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("isAuthenticated() Tests")
    class IsAuthenticatedTests {

        @Test
        @DisplayName("Devrait retourner true si utilisateur authentifié")
        void shouldReturnTrueWhenAuthenticated() {
            // Arrange
            when(session.getAttribute("USER_ID")).thenReturn(1L);

            // Act
            boolean result = sessionService.isAuthenticated(session);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Devrait retourner false si utilisateur non authentifié")
        void shouldReturnFalseWhenNotAuthenticated() {
            // Arrange
            when(session.getAttribute("USER_ID")).thenReturn(null);

            // Act
            boolean result = sessionService.isAuthenticated(session);

            // Assert
            assertFalse(result);
        }
    }
}
