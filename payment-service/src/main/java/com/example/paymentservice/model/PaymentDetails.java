package com.example.paymentservice.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "payments")
public class PaymentDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long user;
	
	private double amount;
	
	private Date paymentDate;
	
	private String paymentType;
	
	private Long rent;
	
	public PaymentDetails() {
		
	}

	public PaymentDetails(Long id, Long user, double amount, Date paymentDate, String paymentType, Long rent) {
		super();
		this.id = id;
		this.user = user;
		this.amount = amount;
		this.paymentDate = paymentDate;
		this.paymentType = paymentType;
		this.rent = rent;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUser() {
		return user;
	}

	public void setUser(Long user) {
		this.user = user;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public Long getRent() {
		return rent;
	}

	public void setRent(Long rent) {
		this.rent = rent;
	}
	
	
}
