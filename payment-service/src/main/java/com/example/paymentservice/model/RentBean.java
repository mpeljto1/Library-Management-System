package com.example.paymentservice.model;

import java.util.Date;
import java.util.List;

public class RentBean {

	private Long id;
	
	private Long user;
	
	private List<Long> rentedBooks;
	
	private Date dateIssued;
	
	private Date dateReturned;
	
	private Date expireDate;
	
	public RentBean() {
		
	}

	public RentBean(Long id, Long user, List<Long> rentedBooks, Date dateIssued, Date dateReturned,
			Date expireDate) {
		super();
		this.id = id;
		this.user = user;
		this.rentedBooks = rentedBooks;
		this.dateIssued = dateIssued;
		this.dateReturned = dateReturned;
		this.expireDate = expireDate;
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

	public List<Long> getRentedBooks() {
		return rentedBooks;
	}

	public void setRentedBooks(List<Long> rentedBooks) {
		this.rentedBooks = rentedBooks;
	}

	public Date getDateIssued() {
		return dateIssued;
	}

	public void setDateIssued(Date dateIssued) {
		this.dateIssued = dateIssued;
	}

	public Date getDateReturned() {
		return dateReturned;
	}

	public void setDateReturned(Date dateReturned) {
		this.dateReturned = dateReturned;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}
}
