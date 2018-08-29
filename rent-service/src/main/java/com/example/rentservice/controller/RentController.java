package com.example.rentservice.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rentservice.BookServiceProxy;
import com.example.rentservice.PaymentServiceProxy;
import com.example.rentservice.UserServiceProxy;
import com.example.rentservice.exception.ResourceNotFoundException;
import com.example.rentservice.exception.AppException;
import com.example.rentservice.model.RentDetails;
import com.example.rentservice.model.ReturnModel;
import com.example.rentservice.repository.RentRepository;


//@Component
@CrossOrigin(origins = "https://localhost:4200")
@RestController
@RequestMapping("/api")
public class RentController {

	@Autowired
	RentRepository rentRepository;
	
	@Autowired
	UserServiceProxy userServiceProxy;
	
	@Autowired
	BookServiceProxy bookServiceProxy;
	
	@Autowired
	PaymentServiceProxy paymentServiceProxy;
	
	// rent book(s)
	@PostMapping("/rents")
	public RentDetails rentABook(@RequestBody RentDetails rent, @RequestHeader("Authorization") String authToken) {
		
		
		// provjeriti da li postoji user i knjige
		if(!userServiceProxy.checkIfUserExistsById(rent.getUser(), authToken))
			throw new ResourceNotFoundException("User with id: " + rent.getUser() + " not found");
		
		if (rent.getRentedBooks().size() > 2) 
			throw new AppException("Maximum number of books to rent can't be more than 2");
		
		if(rentRepository.countByUserAndDateReturnedIsNull(rent.getUser()) >= 3)
			throw new AppException("Maximum number of rents is 3!");
		
		for (int i = 0; i < rent.getRentedBooks().size(); i++) {
			
			if (!bookServiceProxy.checkIfBookIsAvailable(rent.getRentedBooks().get(i), authToken))
				throw new AppException("Book with id: " + rent.getRentedBooks().get(i) + " is not available");	
		}
		
		for (int i = 0; i < rent.getRentedBooks().size(); i++) {
			
			bookServiceProxy.decreseQuantity(rent.getRentedBooks().get(i), authToken);
		}
		
		return rentRepository.save(rent);
	}
	
	// returns  rented books - set returned date
	@PostMapping("/rents/return-rented")
	public ResponseEntity<?> returnABook(@RequestBody RentDetails rent, @RequestHeader("Authorization") String authToken) {
		
		Date d = new Date();
		
		rent.setDateReturned(d);
		
		for (int i = 0; i < rent.getRentedBooks().size(); i++) {
			
			bookServiceProxy.increaseQuantity(rent.getRentedBooks().get(i), authToken);
		}
		
		rentRepository.save(rent);
		double totalPrice = paymentServiceProxy.payForRent(rent.getId(),authToken);
		return ResponseEntity.ok(totalPrice);
		
	}
	
	// get all rents
	@GetMapping("/rents")
	public List<RentDetails> getAllRents() {
		
		return rentRepository.findAll();
	}
	
	// get rent by rent id
		@GetMapping("/rents/{id}")
		public RentDetails getRentsById(@PathVariable(value="id") Long id, @RequestHeader("Authorization") String authToken) {
			
			return rentRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Rent with id: " + id + " not found"));
		}
	
	// get rent by user id
	@GetMapping("/rents/user/{id}")
	public List<RentDetails> getRentsByUserId(@PathVariable(value="id") Long id, @RequestHeader("Authorization") String authToken) {
		
		if(!userServiceProxy.checkIfUserExistsById(id, authToken))
			throw new ResourceNotFoundException("User with id: " + id + " not found");
		
		List<RentDetails> user_rents = rentRepository.findByUser(id);
		
		// if date_returned not null delete from list (rent is returned)
		for (int i = 0; i < user_rents.size(); i++) {
			
			if (user_rents.get(i).getDateReturned() != null)
				user_rents.remove(i);
		}
	
		return user_rents;
	}
	
	// get rents of user by user full name 
		@PostMapping("/rents/user")
		public List<RentDetails> getRentsByUserFullName(@RequestBody ReturnModel rm, @RequestHeader("Authorization") String authToken) {
			
			Long user_id = userServiceProxy.getUserIdByFullName(rm.getFirstName(), rm.getLastName(),authToken);
			
			if(user_id == null) throw new AppException("User with name " + rm.getFirstName() + " "
			+ rm.getLastName() + " not found");
			
			List<RentDetails> user_rents = rentRepository.findByUser(user_id);
			
			// if date_returned not null delete from list (rent is returned)
			for (int i = 0; i < user_rents.size(); i++) {
				
				if (user_rents.get(i).getDateReturned() != null)
					user_rents.remove(i);
			}
		
			return user_rents;
		}
	
	// delete rents by user id
	@DeleteMapping("/rents/user/{id}")
	public ResponseEntity<?> deleteRentsByUserId(@PathVariable(value="id") Long id, @RequestHeader("Authorization") String authToken) {
		
		if(!userServiceProxy.checkIfUserExistsById(id,authToken))
			throw new ResourceNotFoundException("User with id: " + id + " not found");
		
		List<RentDetails> l = rentRepository.findByUser(id);
		
		
		for (int i = 0; i < l.size(); i++) {
			
			rentRepository.delete(l.get(i));
		}
		
		return ResponseEntity.ok().build();
	}
	
	// update rents by user id  ne radi bas najbolje mozda kontati kako update raditi
	@PutMapping("/rents/user/{id}")
	public RentDetails updateRentByUserId(@PathVariable(value="id") Long id, @RequestBody RentDetails rent,
			@RequestHeader("Authorization") String authToken) {
		
		if(!userServiceProxy.checkIfUserExistsById(id,authToken))
			throw new ResourceNotFoundException("User with id: " + id + " not found");
		
		List<RentDetails> l = rentRepository.findByUser(id);
		RentDetails r = new RentDetails();
		
		for (int i = 0; i < l.size(); i++) {
			// ako sadrzi sve knjige i ako im je ista duzina
			if(rent.getRentedBooks().containsAll(l.get(i).getRentedBooks()) 
					&& rent.getRentedBooks().size() == l.get(i).getRentedBooks().size()) {
				
				
				r.setDateReturned(rent.getDateReturned());
				r.setDateIssued(rent.getDateIssued());
				r.setExpireDate(rent.getExpireDate());
				r.setRentedBooks(rent.getRentedBooks());
				r.setUser(rent.getUser());
				
				rentRepository.save(r);
			}
		}
		return r;
	}
	
	@GetMapping("/rents/un-returned")
	public List<RentDetails> getUnReturned() {
		
		List<RentDetails> unReturned = new ArrayList<RentDetails>();
		List<RentDetails> rents = rentRepository.findAll();
		for (int i = 0; i < rents.size(); i++) {
			if(rents.get(i).getDateReturned() == null) {
				unReturned.add(rents.get(i));
			}	
		}
		
		return unReturned;
	}
	
	@GetMapping("/rents/issuedBooks/from/{from}/to/{to}")
	public List<RentDetails> getNumberOfIssuedBooks(@PathVariable(value="from") String from,
			@PathVariable(value="to") String to) {
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		List<RentDetails> l = new ArrayList<>();
        Date DateFrom;
        Date DateTo;
		try {
			DateFrom = format.parse(from);
			DateTo = format.parse(to);
			
			l = rentRepository.findAllByDateIssuedLessThanEqualAndDateIssuedGreaterThanEqual(DateTo, DateFrom);
		} catch (ParseException e) {
			e.printStackTrace();
		}/*
		int count = 0;
		for (int i = 0; i < l.size(); i++) {
			
			count += l.get(i).getRentedBooks().size();
		}
		
		return count;*/
		return l;
	}
	
	@GetMapping("/rents/returnedBooks/from/{from}/to/{to}")
	public List<RentDetails> getNumberOfReturnedBooks(@PathVariable(value="from") String from,
			@PathVariable(value="to") String to) {
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		List<RentDetails> l = new ArrayList<>();
        Date DateFrom;
        Date DateTo;
		try {
			DateFrom = format.parse(from);
			DateTo = format.parse(to);
			
			l = rentRepository.findAllByDateReturnedLessThanEqualAndDateReturnedGreaterThanEqual(DateTo, DateFrom);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return l;
	}
}
