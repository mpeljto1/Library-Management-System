package com.example.rentservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

//@FeignClient("book-service")
@FeignClient("zuul-api-gateway")
public interface BookServiceProxy {

	@GetMapping("/book-service/api/books/available/{id}")
	public boolean checkIfBookIsAvailable(@PathVariable(value="id") Long id, @RequestHeader("Authorization") String authToken);
	
	@GetMapping("/book-service/api/books/decreseQuantity/{id}")
	public void decreseQuantity(@PathVariable(value="id") Long id, @RequestHeader("Authorization") String authToken);
	
	@GetMapping("/book-service/api/books/increaseQuantity/{id}")
	public void increaseQuantity(@PathVariable(value="id") Long id, @RequestHeader("Authorization") String authToken);
}
