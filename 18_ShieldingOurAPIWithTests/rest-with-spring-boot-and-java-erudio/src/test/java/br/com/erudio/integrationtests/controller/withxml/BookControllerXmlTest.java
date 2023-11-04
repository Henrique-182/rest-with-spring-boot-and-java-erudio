package br.com.erudio.integrationtests.controller.withxml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.BookVO;
import br.com.erudio.integrationtests.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerXmlTest extends AbstractIntegrationTest {

	private static XmlMapper objectMapper;
	private static RequestSpecification specification;
	
	private static Date date;
	
	private static BookVO book;
	
	private static TokenVO tokenVO;
	
	@BeforeAll
	public static void setUp() throws ParseException {
		objectMapper = new XmlMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		date = new SimpleDateFormat("yyyy-MM-dd").parse("1949-06-08");

		book = new BookVO();
	}
	
	@Test
	@Order(0)
	public void authorization() {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		tokenVO = given()
				.basePath("/auth/signin")
				.port(TestConfigs.SERVER_PORT)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
				.body(user)
				.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.as(TokenVO.class);
		
		specification = new RequestSpecBuilder()
				.setBasePath("/api/book/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getAccessToken())
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
					
	}
	
	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockBook();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
				.body(book)
				.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.asString();
		
		BookVO createdBook = objectMapper.readValue(content, BookVO.class);
		book = createdBook;
		
		assertNotNull(createdBook);
		
		assertTrue(createdBook.getId() > 0);
		
		assertEquals("1984", createdBook.getTitle());
		assertEquals("George Orwell", createdBook.getAuthor());
		assertEquals(date, createdBook.getLaunchDate());
		assertEquals(50.0, createdBook.getPrice());
	}
	
	@Test
	@Order(2)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
				.pathParam("id", book.getId())
				.when()
					.get("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
					.asString();
		
		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
		book = persistedBook;
		
		assertNotNull(persistedBook);
		
		assertTrue(persistedBook.getId() > 0);
		
		assertEquals(book.getId(), persistedBook.getId());
		assertEquals("1984", persistedBook.getTitle());
		assertEquals("George Orwell", persistedBook.getAuthor());
		assertEquals(date, persistedBook.getLaunchDate());
		assertEquals(50.0, persistedBook.getPrice());
	}
	
	@Test
	@Order(3)
	public void testUpdate() throws JsonMappingException, JsonProcessingException, ParseException {
		book.setTitle("Revolução dos Bichos");
		date = new SimpleDateFormat("yyyy-MM-dd").parse("1945-01-01");
		book.setLaunchDate(date);
		book.setPrice(75.0);
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
				.body(book)
				.when()
					.put()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.asString();
		
		BookVO updatedBook = objectMapper.readValue(content, BookVO.class);
		book = updatedBook;
		
		assertNotNull(updatedBook);
		
		assertTrue(updatedBook.getId() > 0);
		
		assertEquals("Revolução dos Bichos", updatedBook.getTitle());
		assertEquals("George Orwell", updatedBook.getAuthor());
		assertEquals(date, updatedBook.getLaunchDate());
		assertEquals(75.0, updatedBook.getPrice());
	}
	
	@Test
	@Order(4)
	public void testDelete() {
		
		given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_XML)
			.accept(TestConfigs.CONTENT_TYPE_XML)
			.pathParam("id", book.getId())
			.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}
	
	@Test
	@Order(5)
	public void testFindAll() throws JsonMappingException, JsonProcessingException, ParseException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
				.when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.asString();
		
		List<BookVO> bookList = objectMapper.readValue(content, new TypeReference<List<BookVO>>() {});
		
		BookVO bookOne = bookList.get(0);
		
		assertNotNull(bookOne);
		
		assertTrue(bookOne.getId() > 0);
		
		assertEquals(1, bookOne.getId());
		assertEquals("Working effectively with legacy code", bookOne.getTitle());
		assertEquals("Michael C. Feathers", bookOne.getAuthor());
		assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2017-11-29"), bookOne.getLaunchDate());
		assertEquals(49.0, bookOne.getPrice());
		
		BookVO bookTwo = bookList.get(1);
		
		assertNotNull(bookTwo);
		
		assertTrue(bookTwo.getId() > 0);
		
		assertEquals(2, bookTwo.getId());
		assertEquals("Design Patterns", bookTwo.getTitle());
		assertEquals("Ralph Johnson, Erich Gamma, John Vlissides e Richard Helm", bookTwo.getAuthor());
		assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2017-11-29"), bookTwo.getLaunchDate());
		assertEquals(45.0, bookTwo.getPrice());
	}
	
	@Test
	@Order(5)
	public void testFindAllWithoutToken(){
		
		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
			.setBasePath("/api/book/v1")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_XML)
			.setAccept(TestConfigs.CONTENT_TYPE_XML)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
		
		given().spec(specificationWithoutToken)
			.contentType(TestConfigs.CONTENT_TYPE_XML)
			.when()
				.get()
			.then()
				.statusCode(403);
		
	}
	
	public static void mockBook() {
		book.setTitle("1984");
		book.setAuthor("George Orwell");
		book.setLaunchDate(date);
		book.setPrice(50.0);
	}
}
