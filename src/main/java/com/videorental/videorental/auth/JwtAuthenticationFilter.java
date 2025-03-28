package com.videorental.videorental.auth;

import com.videorental.videorental.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, IOException {

        // Extract JWT from Authorization header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) { // checks if its jwt token
            token = authHeader.substring(7); // Remove "Bearer " prefix
            try {
                email = jwtTokenProvider.getEmailFromToken(token);        // we will check for email and not username as username doesnot exist in our system
            } catch (ExpiredJwtException e) {
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"error\": \"Token has expired\", \"message\": \"Your session has ended. Please log in again.\"}"
                );
                return;
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"error\": \"Invalid token\", \"message\": \"The provided token is invalid.\"}"
                );
                return;
            }
        }

        // Validate token and authenticate user
        // if the user is not already authenticated by some other filter
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtTokenProvider.validateToken(token)) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(  // creating authentication object
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // Token is invalid - send unauthorized response
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"error\": \"Invalid token\", \"message\": \"Authentication failed. Please log in again.\"}"
                );
                return; // Stopping further processing
            }
        }
            // If no token or already authenticated, continue normal filter chain
            filterChain.doFilter(request, response);
    }
}