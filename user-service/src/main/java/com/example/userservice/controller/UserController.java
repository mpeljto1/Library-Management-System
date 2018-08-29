package com.example.userservice.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.exception.ResourceNotFoundException;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;

@CrossOrigin(origins = "https://localhost:4200")
@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	UserRepository userRepository;
	
	

	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public UserController(UserRepository applicationUserRepository,
			BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = applicationUserRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	

	// get all users
	@GetMapping("/users")
	//@PreAuthorize("hasAuthority('ADMIN')")
	public List<User> getAllUsers() {

		return userRepository.findAll();
	}

	// get specific user
	//@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/users/{userId}")
	public User getUserById(@PathVariable(value = "userId") Long id) {
	
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));
	}

	// create new user
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/users")
	public User createUser(@Valid @RequestBody User user) {
		User u = userRepository.findByUsername(user.getUsername());
		if (u != null) throw new ResourceNotFoundException("Username taken, please choose different username!");
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	// update a user
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/users/{userId}")
	public User updateUserById(@PathVariable(value = "userId") Long id, @Valid @RequestBody User user) {

		User u = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));

		u.setAddress(user.getAddress());
		u.setFirstName(user.getFirstName());
		u.setGender(user.getGender());
		u.setLastName(user.getLastName());
		u.setPassword(user.getPassword());
		u.setUsername(user.getUsername());
		u.setPhone(user.getPhone());
		u.setRole(user.getRole());
		u.setEmail(user.getEmail());

		User updatedUser = userRepository.save(u);
		return updatedUser;
	}

	// delete user by id
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/users/{userId}")
	public ResponseEntity<?> deleteUserById(@PathVariable(value = "userId") Long id) {

		User u = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));

		userRepository.deleteById(id);

		return ResponseEntity.ok().build();
	}

	// check if user with id exists
	@GetMapping("/users/exists/{id}")
	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	public boolean checkIfUserExistsById(@PathVariable(value = "id") Long id, @RequestHeader("Authorization") String authToken) {

		User u = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));

		if (u != null)
			return true;
		return false;
	}

	// get user id by first and last name
	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/users/firstName/{first}/lastName/{last}")
	public Long getUserIdByFullName(@PathVariable(value = "first") String firstName,
			@PathVariable(value = "last") String lastName, @RequestHeader("Authorization") String authToken) {

		return userRepository.findByFirstNameAndLastName(firstName, lastName).getId();
	}

	@GetMapping("/users/username/{username}")
	public User getUserByUsername(@PathVariable(value = "username") String username,
			@RequestHeader("Authorization") String cookie) {
		
		return userRepository.findByUsername(username);
	}

	@PostMapping("/users/sign-up")
	public void signUp(@RequestBody User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}
	
	@GetMapping("/users/dateCreated/from/{from}/to/{to}")
	public List<User> getUsersByDateCreated(@PathVariable(value="from") String from,
			@PathVariable(value="to") String to) {
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		List<User> users = new ArrayList<>();
		Date DateFrom;
		Date DateTo;
		try {
			DateFrom = format.parse(from);
			DateTo = format.parse(to);
			users = userRepository.findAllByCreatedAtLessThanEqualAndCreatedAtGreaterThanEqual(DateTo, DateFrom);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return users;
	}
	
	@PostMapping("/users/register")
	public User registerUser(@Valid @RequestBody User user) {
		
		User u = userRepository.findByUsername(user.getUsername());
		if (u != null) throw new ResourceNotFoundException("Username taken, please choose different username!");
		
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}
	
	@GetMapping("/users/email/{email}")
	public User getUserByEmail(@PathVariable(value = "email") String email,
			@RequestHeader("Authorization") String cookie) {
		
		return userRepository.findByEmail(email);
	}

}
