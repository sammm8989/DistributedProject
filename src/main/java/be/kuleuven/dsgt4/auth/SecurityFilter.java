package be.kuleuven.dsgt4.auth;

import be.kuleuven.dsgt4.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // TODO: (level 1) decode Identity Token and assign correct email and role
        // TODO: (level 2) verify Identity Token

        String token = extractToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Inside your doFilterInternal method

        try {
            Claims claims = Jwts.parser()
                    .parseClaimsJwt(token)
                    .getBody();

            String email = claims.getSubject(); // Extract email from the subject
            String role = (String) claims.get("role"); // Extract role from claims

            if (email == null || role == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            User user = new User(email, role);

            // Log user information to server logs
            logger.info("Authenticated user: Email - " + email + ", Role - " + role);

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new FirebaseAuthentication(user));

            // Add custom header containing user information
            response.setHeader("X-Authenticated-User", "Email: " + email + ", Role: " + role);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // Extract token without "Bearer " prefix
        }
        return null;
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return !path.startsWith("/api");
    }

    private static class FirebaseAuthentication implements Authentication {
        private final User user;

        FirebaseAuthentication(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            if (user.isManager()) {
                return List.of(new SimpleGrantedAuthority("manager"));
            } else {
                return new ArrayList<>();
            }
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public User getPrincipal() {
            return this.user;
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }

        @Override
        public void setAuthenticated(boolean b) throws IllegalArgumentException {

        }

        @Override
        public String getName() {
            return null;
        }
    }
}

