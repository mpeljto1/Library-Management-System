package com.example.rentservice.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;

import com.example.rentservice.model.RentDetails;

public interface RentRepository extends JpaRepository<RentDetails, Long>{
	
	List<RentDetails> findByUser(Long id);
	
	int countByUserAndDateReturnedIsNull(Long id);
	
	List<RentDetails> findAllByDateIssuedLessThanEqualAndDateIssuedGreaterThanEqual(Date endDate, Date startDate);
	
	List<RentDetails> findAllByDateReturnedLessThanEqualAndDateReturnedGreaterThanEqual(Date endDate, Date startDate);
}
