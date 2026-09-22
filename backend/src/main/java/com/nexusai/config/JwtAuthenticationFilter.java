package com.nexusai.config;

import com.nexusai.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            UserRepository userRepository
    ) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        System.out.println(
                "JWT FILTER → " +
                request.getMethod() +
                " " +
                request.getRequestURI()
        );

        System.out.println(
                "AUTH HEADER EXISTS → " +
                (authHeader != null)
        );

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("JWT FILTER → NO BEARER TOKEN");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            System.out.println("JWT FILTER → INVALID TOKEN");
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("JWT FILTER → TOKEN VALID");

        String email = jwtUtil.extractEmail(token);

        System.out.println(
                "JWT FILTER → EMAIL = " + email
        );

        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            userRepository.findByEmail(email).ifPresentOrElse(

                    user -> {

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        user.getEmail(),
                                        null,
                                        AuthorityUtils.NO_AUTHORITIES
                                );

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource()
                                        .buildDetails(request)
                        );

                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);

                        System.out.println(
                                "JWT FILTER → USER AUTHENTICATED"
                        );
                    },

                    () -> {
                        System.out.println(
                                "JWT FILTER → USER NOT FOUND: " + email
                        );
                    }
            );
        }

        filterChain.doFilter(request, response);
    }
}