package com.GameFlix.model;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
	
	@Column(length = 255)
	private String firstName;
	
	@Column(length = 255)
	private String lastName;
	
	@Column(nullable = false, unique = true, length = 255)
	private String email;
	
	@Column(length = 255)
	private String username;
	
	@Column(nullable = false, length = 255)
	private String password;
	
	// getters
	public Long getUserId() {
		return userId;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	// setters
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
