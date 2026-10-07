package com.GameFlix;

import com.GameFlix.model.User;
import com.GameFlix.security.JwtService;
import com.GameFlix.service.UserService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
	private final UserService userService;
	private final JwtService jwtService;
	
	public AuthController(UserService userService, JwtService jwtService) {
		this.userService = userService;
		this.jwtService = jwtService;
	}
	
	// POST /auth/register
	@PostMapping("/auth/register")
	public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
		// checking to see if the email is empty or not a valid format
		if(user.getEmail() == null || !user.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
			return ResponseEntity.badRequest().body(Map.of("message", "Please enter a valid email"));
		}
		
		// will give 400 if the password is too short
		if(user.getPassword() == null || user.getPassword().length() < 8 || user.getPassword().length() > 64) {
			return ResponseEntity.badRequest().body(Map.of("message", "Password needs to be at least 8 characters long"));
		}
		
		if(!userService.register(user)) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Email is already registered"));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered"));
	}	
	
	
	// POST /auth/login
	@PostMapping("/auth/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
		// checking to see if the email matches the password in the database otherwise will throw error
		if (user.getEmail() == null || user.getPassword() == null || !userService.login(user.getEmail(), user.getPassword())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password"));
		}
		// generate token when the user successfully login
		return ResponseEntity.ok(Map.of("token", jwtService.generateToken(user.getEmail())));
	}
	
	// GET /api/users/me
	@GetMapping("/users/me")
	public ResponseEntity<?> me(Authentication auth) {
		// JwtAuthFilter will read the email that the token belongs to
		return userService.findByEmail((String) auth.getPrincipal())
				.<ResponseEntity<?>>map(u -> {
					// display the results from the token
					Map<String, Object> profile = new LinkedHashMap<>();
					profile.put("userId", u.getUserId());
					profile.put("firstName", u.getFirstName());
					profile.put("lastName", u.getLastName());
					profile.put("email", u.getEmail());
					return ResponseEntity.ok(profile);
				})
				.orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
	}
}