package com.GameFlix.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	
	public JwtAuthFilter(JwtService jwtService) {
		// used to check the token
		this.jwtService = jwtService;
	}
	
	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // Expected format: "Authorization: Bearer <token>"
        String header = request.getHeader("Authorization");

        // only try to read the token if the header exists and starts with Bearer
        if (header != null && header.startsWith("Bearer ")) {
            try {
            	// will move Bearer, check the token, and return the email
                String subject = jwtService.parseUsername(header.substring(7));

                // token is valid so it's marked the request as logged in as the email
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(subject, null, List.of()));
            } catch (JwtException | IllegalArgumentException e) {
          }
        }
        // pass request along to the next step
        chain.doFilter(request, response);
	}
}
