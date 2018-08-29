package com.example.paymentservice.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import com.example.paymentservice.BookServiceProxy;
import com.example.paymentservice.RentServiceProxy;
import com.example.paymentservice.exception.ResourceNotFoundException;
import com.example.paymentservice.model.PaymentDetails;
import com.example.paymentservice.model.RentBean;
import com.example.paymentservice.repository.PaymentRepository;

@CrossOrigin(origins = "https://localhost:4200")
@RestController
@RequestMapping("/api")
public class PaymentController {

	@Autowired
	PaymentRepository paymentRepository;
	
	@Autowired
	RentServiceProxy rentServiceProxy;
	
	@Autowired
	BookServiceProxy bookServiceProxy;
	
	// get all payments
	@GetMapping("/payments")
	public List<PaymentDetails> getAllPayments() {
			
		return paymentRepository.findAll();
	}
		
	// get payment by id
	@GetMapping("/payments/{id}")
	public PaymentDetails getPaymentById(@PathVariable(value="id") Long id) {
		
		return paymentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Payment with id: " + id + " not found"));
	}
	
	// create new payment
	@PostMapping("/payments")
	public PaymentDetails createPayment(@ Valid @RequestBody PaymentDetails payment) {
		
		return paymentRepository.save(payment);
	}
	
	// update payment by id
	@PutMapping("payments/{id}")
	public PaymentDetails updatePaymentById(@PathVariable(value="id") Long id,
			@ Valid @RequestBody PaymentDetails payment) {
		
		PaymentDetails p = paymentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Payment with id: " + id + " not found"));
		
		p.setAmount(payment.getAmount());
		p.setPaymentDate(payment.getPaymentDate());
		p.setPaymentType(payment.getPaymentType());
		p.setRent(payment.getRent());
		p.setUser(payment.getUser());
		
		PaymentDetails updatedPayment = paymentRepository.save(p);
		return updatedPayment;
	}
	
	// delete payment by id
	@DeleteMapping("/payments/{id}")
	public ResponseEntity<?> deletePaymentById(@PathVariable(value="id") Long id) {
		
		PaymentDetails p = paymentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Payment with id: " + id + " not found"));
		
		paymentRepository.deleteById(id);
		
		return ResponseEntity.ok().build();
	}
	
	// pay for rent call when user returns books
	@GetMapping("/payments/pay/{rentId}")
	public double payForRent(@PathVariable(value="rentId") Long id, @RequestHeader("Authorization") String authToken) {
		
		RentBean rent = rentServiceProxy.getRentsById(id,authToken);
		
		double amount = 0;
		List<Double> book_prices = new ArrayList<Double>();
		
		Long issued = rent.getDateIssued().getTime();
		Long expired = rent.getExpireDate().getTime();
		Long returned = rent.getDateReturned().getTime();
		Long diff = Math.abs(expired-issued);
		Long diffInDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		
		Long diff_Returned_issued = TimeUnit.DAYS.convert(Math.abs(returned - issued), TimeUnit.MILLISECONDS);
		Long diff_Returned_expired = TimeUnit.DAYS.convert(Math.abs(returned - expired), TimeUnit.MILLISECONDS);
		Long diff_Expired_issued = TimeUnit.DAYS.convert(Math.abs(expired-issued), TimeUnit.MILLISECONDS);
		
		for (int i = 0; i < rent.getRentedBooks().size(); i++) {
				
			book_prices.add(bookServiceProxy.getBookPriceById(rent.getRentedBooks().get(i),authToken));
				
			if(rent.getDateReturned().compareTo(rent.getExpireDate()) <= 0) {
				amount += book_prices.get(i) * diff_Returned_issued;
			}
			else {
				amount += book_prices.get(i)*diff_Expired_issued + (book_prices.get(i) + book_prices.get(i)*0.15)*diff_Returned_expired;
			}
		}	
		PaymentDetails p = new PaymentDetails();
		p.setAmount(amount);
		p.setPaymentDate(new Date());
		p.setRent(rent.getId());
		p.setUser(rent.getUser());
		
	    paymentRepository.save(p);
	    return p.getAmount();
	}
	
}
