package com.example.paymentservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

//@FeignClient("book-service")
@FeignClient("zuul-api-gateway")
public interface BookServiceProxy {

	@GetMapping("/book-service/api/books/price/{bookId}")
	public double getBookPriceById(@PathVariable(value="bookId") Long id, @RequestHeader("Authorization") String authToken);
}
