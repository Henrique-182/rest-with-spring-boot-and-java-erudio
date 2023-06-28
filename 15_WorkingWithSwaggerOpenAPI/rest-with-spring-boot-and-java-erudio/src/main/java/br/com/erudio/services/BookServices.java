package br.com.erudio.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.erudio.controllers.BookController;
import br.com.erudio.data.vo.v1.BookVO;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.exception.ResourceNotFoundException;
import br.com.erudio.mapper.DozerMapper;
import br.com.erudio.model.Book;
import br.com.erudio.repositories.BookRepository;

@Service
public class BookServices { 

	@Autowired
	private BookRepository repository;
	
	public BookVO findById(Long id) {
		var book = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));
		
		BookVO vo = DozerMapper.parseObject(book, BookVO.class);
		
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		
		return vo;
	}
	
	public List<BookVO> findAll() {
		List<BookVO> booksVO = DozerMapper.parseListObjects(repository.findAll(), BookVO.class);
		
		booksVO.stream().forEach(b -> b.add(linkTo(methodOn(BookController.class).findById(b.getKey())).withSelfRel()));
		
		return booksVO;
	}
	
	public BookVO create(BookVO book) {
		if(book == null) throw new RequiredObjectIsNullException();
		
		var entity = DozerMapper.parseObject(book, Book.class);
		
		BookVO vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		
		return vo;
	}
	
	public BookVO update(BookVO book) {
		if(book == null) throw new RequiredObjectIsNullException();
		
		var entity = repository.findById(book.getKey()).orElseThrow(() -> new ResourceNotFoundException("No recourds found for this id"));
	
		entity.setAuthor(book.getAuthor());
		entity.setTitle(book.getTitle());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());
		
		repository.save(entity);
		
		BookVO vo = DozerMapper.parseObject(entity, BookVO.class);
		
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		
		return vo;
	}
	
	public void delete(Long id) {
		var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));
	
		repository.delete(entity);
	}
}
