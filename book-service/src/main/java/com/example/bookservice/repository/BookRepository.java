package com.example.bookservice.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bookservice.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

	List<Book> findByNameIgnoreCaseContaining(String name);

	List<Book> findByAuthorIgnoreCaseContaining(String author);
	
	Book findByIsbn(int isbn);
	
	List<Book> findByIdIn(List<Long> ids);
	
	List<Book> findAllByCreatedAtLessThanEqualAndCreatedAtGreaterThanEqual(Date endDate, Date startDate);
	
	List<Book> findAllByQuantityGreaterThan(int quantitiy);
}
