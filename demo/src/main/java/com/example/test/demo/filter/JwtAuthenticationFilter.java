package com.example.test.demo.filter;

import com.example.test.demo.config.CustomUserDetails;
import com.example.test.demo.entity.User;
import com.example.test.demo.repository.UserRepository;
import com.example.test.demo.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository UserRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtUtil.getUsername(token);
                User user = UserRepository.findByUsername(username).orElse(null);

                if (user != null) {

                    CustomUserDetails customUserDetails = new CustomUserDetails(user);

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            customUserDetails, null, customUserDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
