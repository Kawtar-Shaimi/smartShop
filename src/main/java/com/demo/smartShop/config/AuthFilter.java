package com.demo.smartShop.config;

import com.demo.smartShop.dto.UserDTO;
import com.demo.smartShop.entity.enums.UserRole;
import com.demo.smartShop.exception.ForbiddenException;
import com.demo.smartShop.exception.UnauthorizedException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@Component
@Order(1)
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String url = req.getRequestURI();
        String method = req.getMethod();

        // 1) Login est public (tout le monde peut se connecter)
        if (url.startsWith("/api/auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        // 2) Vérifier que l'utilisateur est connecté
        UserDTO user = getConnectedUser(req);
        if (user == null) {
            throw new UnauthorizedException("Vous devez etre connecte");
        }

        // 3) ADMIN a tous les droits
        if (user.getRole() == UserRole.ADMIN) {
            chain.doFilter(request, response);
            return;
        }

        // 4) CLIENT a des droits limités
        if (user.getRole() == UserRole.CLIENT) {
            verifierAccesClient(url, method, user, request, response, chain);
            return;
        }

        // 5) Rôle inconnu = refus
        throw new ForbiddenException("Role non reconnu");
    }

    //Récupère l'utilisateur connecté (ou null si pas connecté)
    private UserDTO getConnectedUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        return (UserDTO) session.getAttribute("user");
    }


    private void verifierAccesClient(String url, String method, UserDTO user,
                                     ServletRequest request, ServletResponse response,
                                     FilterChain chain) throws IOException, ServletException {

        // Autorisation 1 : Déconnexion
        if (url.startsWith("/api/auth/logout")) {
            chain.doFilter(request, response);
            return;
        }

        // Interdiction 1 : Pas de modifications (POST/PUT/DELETE)
        if (isModification(method)) {
            throw new ForbiddenException("Les clients ne peuvent pas modifier les donnees");
        }

        // Autorisation 2 : Voir tous les produits
        if (url.startsWith("/api/products")) {
            chain.doFilter(request, response);
            return;
        }

        // Autorisation 3 : Voir SES commandes uniquement
        if (url.equals("/api/orders/my-orders")) {
            chain.doFilter(request, response);
            return;
        }

        // Interdiction 2 : Bloquer l'accès aux autres endpoints de commandes
        if (url.startsWith("/api/orders")) {
            throw new ForbiddenException("Utilisez /api/orders/my-orders");
        }

        // Autorisation 4 : Voir SON profil uniquement
        if (url.startsWith("/api/clients/")) {
            if (isOwnProfile(url, user)) {
                chain.doFilter(request, response);
                return;
            }
            throw new ForbiddenException("Vous ne pouvez voir que votre propre profil");
        }

        // Interdiction 3 : Bloquer la liste de tous les clients
        if (url.equals("/api/clients")) {
            throw new ForbiddenException("Vous ne pouvez pas voir tous les clients");
        }

        // Par défaut : refuser
        throw new ForbiddenException("Acces refuse");
    }


    // Vérifie si la méthode HTTP modifie des données
    private boolean isModification(String method) {
        return method.equals("POST") || method.equals("PUT") || method.equals("DELETE");
    }

    // Vérifie si l'URL correspond au profil du client connecté
    private boolean isOwnProfile(String url, UserDTO user) {
        try {
            String idStr = url.substring(url.lastIndexOf('/') + 1);
            Long id = Long.parseLong(idStr);
            return id.equals(user.getClientId());
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
