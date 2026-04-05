package com.example.finance.Security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.finance.entity.UserStatus;
import com.example.finance.entity.Users;
import com.example.finance.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RoleFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public RoleFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String username = request.getHeader("X-USER");

        if (username == null || username.isBlank()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("X-USER header is required");
            return;
        }

        Users user = userRepository.findByEmailIgnoreCase(username.trim()).orElse(null);

        if (user == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Invalid X-USER header");
            return;
        }

        if (user.isDeleted()) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.getWriter().write("User not found with email: " + username.trim());
            return;
        }

        if (user.getRole() == null) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("Invalid user configuration");
            return;
        }

        if (user.getStatus() == null) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("Invalid user configuration");
            return;
        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("User is inactive");
            return;
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                List.of(authority));
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
