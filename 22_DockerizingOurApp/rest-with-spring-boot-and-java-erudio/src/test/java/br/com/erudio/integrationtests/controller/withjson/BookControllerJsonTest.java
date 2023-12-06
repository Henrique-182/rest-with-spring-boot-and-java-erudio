package br.com.erudio.integrationtests.controller.withjson;

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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.BookVO;
import br.com.erudio.integrationtests.vo.TokenVO;
import br.com.erudio.integrationtests.vo.wrappers.WrapperBookVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest {

	private static ObjectMapper objectMapper;
	private static RequestSpecification specification;
	
	private static Date date;
	
	private static BookVO book;
	
	private static TokenVO tokenVO;
	
	@BeforeAll
	public static void setUp() throws ParseException {
		objectMapper = new ObjectMapper();
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
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
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
				.setContentType(TestConfigs.CONTENT_TYPE_JSON)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
					
	}
	
	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockBook();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
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
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", book.getId())
				.when()
					.get("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
					.asString();
		
		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
		
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
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.body(book)
				.when()
					.put()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.asString();
		
		BookVO createdBook = objectMapper.readValue(content, BookVO.class);
		book = createdBook;
		
		assertNotNull(createdBook);
		
		assertTrue(createdBook.getId() > 0);
		
		assertEquals("Revolução dos Bichos", createdBook.getTitle());
		assertEquals("George Orwell", createdBook.getAuthor());
		assertEquals(date, createdBook.getLaunchDate());
		assertEquals(75.0, createdBook.getPrice());
	}
	
	@Test
	@Order(4)
	public void testDelete() {
		
		given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
			.pathParam("id", book.getId())
			.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}
	
	@Test
	@Order(5)
	public void testFindAll() throws JsonMappingException, JsonProcessingException, ParseException {
		Integer page = 1;
		Integer size = 10;
		String direction = "desc";
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.queryParam("page", page)
				.queryParam("size", size)
				.queryParam("direction", direction)
				.when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.asString();
		
		WrapperBookVO wrapper = objectMapper.readValue(content, WrapperBookVO.class);
		
		List<BookVO> bookList = wrapper.getEmbedded().getBooks();
		
		BookVO bookOne = bookList.get(0);
		
		assertEquals(8, bookOne.getId());
		assertEquals("Domain Driven Design", bookOne.getTitle());
		assertEquals("Eric Evans", bookOne.getAuthor());
		assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2017-11-07"), bookOne.getLaunchDate());
		assertEquals(92.0, bookOne.getPrice());
		
		BookVO bookTwo = bookList.get(1);
		
		assertEquals(2, bookTwo.getId());
		assertEquals("Design Patterns", bookTwo.getTitle());
		assertEquals("Ralph Johnson, Erich Gamma, John Vlissides e Richard Helm", bookTwo.getAuthor());
		assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2017-11-29"), bookTwo.getLaunchDate());
		assertEquals(45.0, bookTwo.getPrice());
	}
	
	@Test
	@Order(6)
	public void testFindAllWithoutToken(){
		
		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
			.setBasePath("/api/book/v1")
			.setPort(TestConfigs.SERVER_PORT)
			.setContentType(TestConfigs.CONTENT_TYPE_JSON)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
		
		given().spec(specificationWithoutToken)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
			.when()
				.get()
			.then()
				.statusCode(403);
		
	}
	
	@Test
	@Order(7)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException, ParseException {
		Integer page = 1;
		Integer size = 10;
		String direction = "desc";
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.queryParam("page", page)
				.queryParam("size", size)
				.queryParam("direction", direction)
				.when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.asString();
		
		assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost/api/book/v1/8\"}}},"));
		assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost/api/book/v1/2\"}}},"));
		assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost/api/book/v1/5\"}}},"));
		
		assertTrue(content.contains("\"_links\":{\"first\":{\"href\":\"http://localhost/api/book/v1?direction=asc&page=0&size=10&sort=title,desc\"},"));
		assertTrue(content.contains("\"prev\":{\"href\":\"http://localhost/api/book/v1?direction=asc&page=0&size=10&sort=title,desc\"},"));
		assertTrue(content.contains("\"self\":{\"href\":\"http://localhost/api/book/v1?page=1&size=10&direction=asc\"},"));
		assertTrue(content.contains("\"last\":{\"href\":\"http://localhost/api/book/v1?direction=asc&page=1&size=10&sort=title,desc\"}},"));
		assertTrue(content.contains("\"page\":{\"size\":10,\"totalElements\":15,\"totalPages\":2,\"number\":1}}"));
	}
	
	public static void mockBook() {
		book.setTitle("1984");
		book.setAuthor("George Orwell");
		book.setLaunchDate(date);
		book.setPrice(50.0);
	}
}
