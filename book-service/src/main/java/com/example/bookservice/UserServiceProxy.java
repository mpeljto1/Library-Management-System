package com.example.bookservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.bookservice.model.User;



@FeignClient("zuul-api-gateway")
public interface UserServiceProxy {

	@GetMapping("/user-service/api/users/username/{username}")
	public User getUserByUsername(@PathVariable(value="username") String username,
			@RequestHeader("Authorization") String authToken);
}
