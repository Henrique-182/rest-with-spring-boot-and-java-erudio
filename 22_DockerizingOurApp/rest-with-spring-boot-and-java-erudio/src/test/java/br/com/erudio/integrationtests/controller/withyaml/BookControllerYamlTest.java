package br.com.erudio.integrationtests.controller.withyaml;

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
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.integrationtests.controller.withyaml.mapper.YmlMapper;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.BookVO;
import br.com.erudio.integrationtests.vo.TokenVO;
import br.com.erudio.integrationtests.vo.pagedmodels.PagedModelBook;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerYamlTest extends AbstractIntegrationTest {

	private static YmlMapper objectMapper;
	private static RequestSpecification specification;
	
	private static Date date;
	
	private static BookVO book;
	
	private static TokenVO tokenVO;
	
	@BeforeAll
	public static void setUp() throws ParseException {
		objectMapper = new YmlMapper();
		
		date = new SimpleDateFormat("yyyy-MM-dd").parse("1949-06-08");

		book = new BookVO();
	}
	
	@Test
	@Order(0)
	public void authorization() {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		tokenVO = given()
				.config(
					RestAssuredConfig
					.config()
					.encoderConfig(
						EncoderConfig
						.encoderConfig()
						.encodeContentTypeAs(
							TestConfigs.CONTENT_TYPE_YML, 
							ContentType.TEXT
						)
					)
				)
				.basePath("/auth/signin")
				.port(TestConfigs.SERVER_PORT)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.body(user, objectMapper)
				.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.as(TokenVO.class, objectMapper);
		
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
		
		BookVO createdBook = given()
				.spec(specification)
				.config(
					RestAssuredConfig
					.config()
					.encoderConfig(
						EncoderConfig
						.encoderConfig()
						.encodeContentTypeAs(
							TestConfigs.CONTENT_TYPE_YML, 
							ContentType.TEXT
						)
					)
				)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.body(book, objectMapper)
				.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.as(BookVO.class, objectMapper);
		
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
		
		BookVO persistedBook = given()
				.spec(specification)
				.config(
					RestAssuredConfig
					.config()
					.encoderConfig(
						EncoderConfig
						.encoderConfig()
						.encodeContentTypeAs(
							TestConfigs.CONTENT_TYPE_YML, 
							ContentType.TEXT
						)
					)
				)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.pathParam("id", book.getId())
				.when()
					.get("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
					.as(BookVO.class, objectMapper);
		
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
		
		BookVO updatedBook = given()
				.spec(specification)
				.config(
					RestAssuredConfig
					.config()
					.encoderConfig(
						EncoderConfig
						.encoderConfig()
						.encodeContentTypeAs(
							TestConfigs.CONTENT_TYPE_YML, 
							ContentType.TEXT
						)
					)
				)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.body(book, objectMapper)
				.when()
					.put()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.as(BookVO.class, objectMapper);
		
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
		
		given()
			.spec(specification)
			.config(
				RestAssuredConfig
				.config()
				.encoderConfig(
					EncoderConfig
					.encoderConfig()
					.encodeContentTypeAs(
						TestConfigs.CONTENT_TYPE_YML, 
						ContentType.TEXT
					)
				)
			)
			.contentType(TestConfigs.CONTENT_TYPE_YML)
			.accept(TestConfigs.CONTENT_TYPE_YML)
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
		
		var wrapper = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.queryParam("page", page)
				.queryParam("size", size)
				.queryParam("direction", direction)
				.when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.as(PagedModelBook.class, objectMapper);
		
		List<BookVO> bookList = wrapper.getContent();
		
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
			.setContentType(TestConfigs.CONTENT_TYPE_YML)
			.setAccept(TestConfigs.CONTENT_TYPE_YML)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
		
		given()
			.spec(specificationWithoutToken)
			.config(
				RestAssuredConfig
				.config()
				.encoderConfig(
					EncoderConfig
					.encoderConfig()
					.encodeContentTypeAs(
						TestConfigs.CONTENT_TYPE_YML, 
						ContentType.TEXT
					)
				)
			)
			.contentType(TestConfigs.CONTENT_TYPE_YML)
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
		
		var unthreadtedContent = given()
				.config(
						RestAssuredConfig
						.config()
						.encoderConfig(
							EncoderConfig
							.encoderConfig()
							.encodeContentTypeAs(
								TestConfigs.CONTENT_TYPE_YML, 
								ContentType.TEXT
							)
						)
					)
					.spec(specification)
					.contentType(TestConfigs.CONTENT_TYPE_YML)
					.accept(TestConfigs.CONTENT_TYPE_YML)
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
			
		
		var content = unthreadtedContent.replace("\n", "").replace("\r", "");
		
		assertTrue(content.contains("href: \"http://localhost/api/book/v1/8\""));
		assertTrue(content.contains("href: \"http://localhost/api/book/v1/2\""));
		assertTrue(content.contains("href: \"http://localhost/api/book/v1/5\""));
		
		assertTrue(content.contains("rel: \"first\"  href: \"http://localhost/api/book/v1?direction=asc&page=0&size=10&sort=title,desc\""));
		assertTrue(content.contains("rel: \"prev\"  href: \"http://localhost/api/book/v1?direction=asc&page=0&size=10&sort=title,desc\""));
		assertTrue(content.contains("rel: \"self\"  href: \"http://localhost/api/book/v1?page=1&size=10&direction=asc\""));
		assertTrue(content.contains("rel: \"last\"  href: \"http://localhost/api/book/v1?direction=asc&page=1&size=10&sort=title,desc\""));
		assertTrue(content.contains("page:  size: 10  totalElements: 15  totalPages: 2  number: 1"));
	
	}
	
	public static void mockBook() {
		book.setTitle("1984");
		book.setAuthor("George Orwell");
		book.setLaunchDate(date);
		book.setPrice(50.0);
	}
}
