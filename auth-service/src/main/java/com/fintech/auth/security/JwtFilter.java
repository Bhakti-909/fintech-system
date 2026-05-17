package com.fintech.auth.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

// INTERVIEW: "This filter runs BEFORE every request hits the controller.
// Flow: Request → JwtFilter → SecurityContext populated → Controller
// OncePerRequestFilter ensures it runs exactly once per HTTP request."
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // strip "Bearer " prefix

            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractEmail(token);
                String role  = jwtUtil.extractRole(token);

                // Set authentication in Spring's SecurityContext
                // ROLE_ prefix required by Spring Security for hasRole() checks
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response); // always pass to next filter
    }
}
