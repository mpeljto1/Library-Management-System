package com.example.userservice.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.userservice.model.User;

//@CrossOrigin(origins = "http://localhost:4200")
public interface UserRepository extends JpaRepository<User, Long>{

	User findByFirstNameAndLastName(String firstName, String lastName);
	User findByUsername(String username);
	List<User> findAllByCreatedAtLessThanEqualAndCreatedAtGreaterThanEqual(Date endDate, Date startDate);
	User findByEmail(String email);
}
