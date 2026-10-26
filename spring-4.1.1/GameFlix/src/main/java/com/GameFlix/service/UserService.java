package com.GameFlix.service;

import com.GameFlix.model.User;
import com.GameFlix.repository.UserRepository;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	// registering a user
	public boolean register(User newUser) {
		// checks to see if the email already exists
		if (userRepository.existsByEmail(newUser.getEmail())) {
			return false;
		}
		User user = new User();
		user.setFirstName(newUser.getFirstName());
		user.setLastName(newUser.getLastName());
		user.setEmail(newUser.getEmail().trim().toLowerCase());
		user.setUsername(newUser.getUsername());
		user.setPassword(passwordEncoder.encode(newUser.getPassword()));
		try {
			userRepository.saveAndFlush(user);
			return true;
		} catch (DataIntegrityViolationException e) {
			return false;
		}
	}
	
	// user logging in
	public boolean login(String email, String password) {
		Optional<User> userOpt = userRepository.findByEmail(email);
		
		if (userOpt.isEmpty()) {
			return false;
		}
		User user = userOpt.get();
		return passwordEncoder.matches(password, user.getPassword());
		
	}
	
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
}
