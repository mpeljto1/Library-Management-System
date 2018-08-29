package com.example.bookservice.repository;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.bookservice.UserServiceProxy;



@Service(value = "userService")
public class UserService implements UserDetailsService{

	@Autowired
	UserServiceProxy userServiceProxy;
	
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		com.example.bookservice.model.User user = userServiceProxy.getUserByUsername(userId,"");
		if(user == null){
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), 
				getAuthority(user.getRole().toUpperCase()));
	}

	private List<SimpleGrantedAuthority> getAuthority(String role) {
		return Arrays.asList(new SimpleGrantedAuthority(role));
	}
}
