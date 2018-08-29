package com.example.rentservice.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.JoinColumn;

import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "rents")
public class RentDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long user;
	
	@ElementCollection(targetClass=Long.class)
	//@ElementCollection(fetch = FetchType.LAZY)
	//@CollectionTable(name="Rented books", joinColumns=@JoinColumn(name="id"))
	private List<Long> rentedBooks;
	
	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
	private Date dateIssued;
	
	private Date dateReturned;
	
	
	private Date expireDate;
	
	
	public RentDetails() {
		
	}

	public RentDetails(Long id, Long user, List<Long> rentedBooks, Date dateIssued, Date dateReturned,
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
