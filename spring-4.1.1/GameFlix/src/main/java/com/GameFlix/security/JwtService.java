package com.GameFlix.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
	
	private SecretKey secretKey;
	private long expirationMs;
	
	// value will come from configuration and needs to be at least 32 bytes or an error will be thrown
	public JwtService(@Value("${jwt.secret}") String secret, 
					  @Value("${jwt.expiration-ms}") long expirationMs) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expirationMs = expirationMs;
	}
	
	// builds the signed token 
	public String generateToken(String subject) {
		Date now = new Date();
		return Jwts.builder()
				.subject(subject)
				.issuedAt(now)
				.expiration(new Date(now.getTime() + expirationMs))
				.signWith(secretKey)
				.compact();
	}
	
	// verifies the token and returns its subject, but signature is checked
	// any tampering will throw a JwtException
	public String parseUsername(String token) {
		return Jwts.parser().verifyWith(secretKey).build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}
}
