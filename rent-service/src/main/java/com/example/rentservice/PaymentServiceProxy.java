package com.example.rentservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


//@FeignClient("payment-service")
@FeignClient("zuul-api-gateway")
public interface PaymentServiceProxy {

	@GetMapping("/payment-service/api/payments/pay/{rentId}")
	public double payForRent(@PathVariable(value="rentId") Long id, @RequestHeader("Authorization") String authToken);
}
