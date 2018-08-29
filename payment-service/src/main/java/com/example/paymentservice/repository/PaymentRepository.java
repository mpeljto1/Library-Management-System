package com.example.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.paymentservice.model.PaymentDetails;

public interface PaymentRepository extends JpaRepository<PaymentDetails, Long>{

	
}
