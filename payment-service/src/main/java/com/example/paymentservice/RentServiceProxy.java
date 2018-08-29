package com.example.paymentservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.paymentservice.model.RentBean;

//@FeignClient("rent-service")
@FeignClient("zuul-api-gateway")
public interface RentServiceProxy {

	@GetMapping("/rent-service/api/rents/{id}")
	public RentBean getRentsById(@PathVariable(value="id") Long id, @RequestHeader("Authorization") String authToken);
}
