package com.example.rentservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.rentservice.model.User;


//@FeignClient(name="user-service")
@FeignClient("zuul-api-gateway")
public interface UserServiceProxy {
	
	@GetMapping("/user-service/api/users/exists/{id}")
	public boolean checkIfUserExistsById(@PathVariable(value="id") Long id, @RequestHeader("Authorization") String authToken);
	
	@GetMapping("/user-service/api/users/firstName/{first}/lastName/{last}")
	public Long getUserIdByFullName(@PathVariable(value="first") String firstName,
			@PathVariable(value="last") String lastName, @RequestHeader("Authorization") String authToken);
	
	@GetMapping("/user-service/api/users/username/{username}")
	public User getUserByUsername(@PathVariable(value="username") String username,
			@RequestHeader("Authorization") String authToken);
}
