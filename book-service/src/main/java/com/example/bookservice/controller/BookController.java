package com.example.bookservice.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.example.bookservice.UserServiceProxy;
import com.example.bookservice.exception.ResourceNotFoundException;
import com.example.bookservice.model.Book;
import com.example.bookservice.repository.BookRepository;



@CrossOrigin(origins = "https://localhost:4200")
@RestController
@RequestMapping("/api")
public class BookController {
	
	@Autowired
	BookRepository bookRepository;
	
	@Autowired
	UserServiceProxy userServiceProxy;
	
	// get all books
	@GetMapping("/books")
	public List<Book> getAllBooks() {
		
		return bookRepository.findAll();
	}
	
	// get specific book
	@GetMapping("/books/{bookId}")
	public Book getBookById(@PathVariable(value="bookId") Long id) {
		
		Book b = bookRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Book with id:" + id + " not found"));
		return b;
	}
	
	// create new book
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/books")
	public Book createBook(@Valid @RequestBody Book book) {
		
		return bookRepository.save(book);
	}
	
	// update a book
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/books/{bookId}")
	public Book updateBookById(@PathVariable(value="bookId") Long id, @Valid @RequestBody Book book) {
		
		Book b = bookRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Book with id:" + id + " not found"));
		
		
		b.setAuthor(book.getAuthor());
		b.setBranch(book.getBranch());
		b.setEdition(book.getEdition());
		b.setIsbn(book.getIsbn());
		b.setName(book.getName());
		b.setPrice(book.getPrice());
		b.setPublisher(book.getPublisher());
		b.setQuantity(book.getQuantity());
		
		Book updatedBook =  bookRepository.save(b);
		return updatedBook;
	}
	
	// delete book by id
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/books/{bookId}")
	public ResponseEntity<?> deleteBookById(@PathVariable(value="bookId") Long id) {
		
		Book b = bookRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Book with id:" + id + " not found"));
		
		
		bookRepository.delete(b);
		
		return ResponseEntity.ok().build();
	}
	
	// search for books by name containing 
	@GetMapping("/books/search/name/{bookName}")
	public List<Book> getBooksByName(@PathVariable(value="bookName") String name) {
		
		return bookRepository.findByNameIgnoreCaseContaining(name);
	}
	
	// check if book is available by id quantity > 0
	@GetMapping("/books/available/{id}")
	public boolean checkIfBookIsAvailable(@PathVariable(value="id") Long id, @RequestHeader("Authorization") String authToken) {
		
		Book b = bookRepository.findById(id)
		.orElseThrow(() -> new ResourceNotFoundException("Book with id: " + id + " not found"));
		
		return b.getQuantity() > 0;
	}
	
	// decrease quantity and change to unavailable if it hits zero
	@GetMapping("/books/decreseQuantity/{id}")
	public void decreseQuantity(@PathVariable(value="id") Long id, @RequestHeader("Authorization") String authToken) {
		
		Book b = bookRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Book with id: " + id + " not found"));
		
		if(b.getQuantity() > 0) {
			b.setQuantity(b.getQuantity() - 1);
			
			if (b.getQuantity() == 0) b.setStatus("Not available");
		}
		bookRepository.save(b);
	}
	
	// increase quantity and change to available if it is > 0
		@GetMapping("/books/increaseQuantity/{id}")
		public void increaseQuantity(@PathVariable(value="id") Long id) {
			
			Book b = bookRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Book with id: " + id + " not found"));
			
			
			b.setQuantity(b.getQuantity() + 1);
				
			if (b.getQuantity() > 0) b.setStatus("Available");
			
			bookRepository.save(b);
		}
	
	// get book price by book id
		@GetMapping("/books/price/{bookId}")
		public double getBookPriceById(@PathVariable(value="bookId") Long id, @RequestHeader("Authorization") String authToken) {
			
			Book b = bookRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Book with id: " + id + " not found"));
			return b.getPrice();
		}
		
		@PostMapping("/books/bookId")
		public List<String> findByBooksByIds(@RequestBody List<Long> ids) {
			
			List<Book> books = bookRepository.findByIdIn(ids);
			List<String> bookNames = new ArrayList<String>();
			
			for (int i = 0; i < books.size(); i++) {
				bookNames.add(books.get(i).getName());
			}
			
			return bookNames;
					
		}
		
		@GetMapping("/books/dateCreated/from/{from}/to/{to}")
		public List<Book> getBooksByDateCreated(@PathVariable(value="from") String from,
				@PathVariable(value="to") String to) {
			
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			List<Book> books = new ArrayList<>();
	        Date DateFrom;
	        Date DateTo;
			try {
				DateFrom = format.parse(from);
				DateTo = format.parse(to);
				
				books = bookRepository.findAllByCreatedAtLessThanEqualAndCreatedAtGreaterThanEqual(DateTo, DateFrom);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return books;
		}
		
		@GetMapping("/books/availableBooks")
		public List<Book> getAvailableBooks() {
			List<Book> books = bookRepository.findAllByQuantityGreaterThan(0);
			return books;
		}
}
