package br.com.erudio.integrationtests.controller.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import br.com.erudio.integrationtests.vo.PersonVO;
import br.com.erudio.integrationtests.vo.TokenVO;
import br.com.erudio.integrationtests.vo.pagedmodels.PagedModelPerson;
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
public class PersonControllerYamlTest extends AbstractIntegrationTest{
	
	private static RequestSpecification specification;
	private static YmlMapper objectMapper;

	private static PersonVO person;
	
	@BeforeAll
	public static void setup() {
		objectMapper = new YmlMapper();
		
		person = new PersonVO();
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		var accessToken = given()
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
							.as(TokenVO.class, objectMapper)
						.getAccessToken();
		
		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}
	
	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		PersonVO persistedPerson = given()
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
				.body(person, objectMapper)
				.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.as(PersonVO.class, objectMapper);
		
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertNotNull(persistedPerson.getEnabled());
		
		assertTrue(persistedPerson.getId() > 0);
		
		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
		assertEquals(true, persistedPerson.getEnabled());
	}
	
	@Test
	@Order(2)
	public void testDisablePersonById() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		PersonVO persistedPerson = given()
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
				.pathParam("id", person.getId())
				.when()
					.patch("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
					.as(PersonVO.class, objectMapper);
		
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertNotNull(persistedPerson.getEnabled());
		
		assertTrue(persistedPerson.getId() > 0);
		
		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
		assertEquals(false, persistedPerson.getEnabled());
	}

	@Test
	@Order(3)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		PersonVO persistedPerson = given()
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
				.pathParam("id", person.getId())
				.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.as(PersonVO.class, objectMapper);
		
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertNotNull(persistedPerson.getEnabled());
		
		assertTrue(persistedPerson.getId() > 0);
		
		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
		assertEquals(false, persistedPerson.getEnabled());
	}
	
	@Test
	@Order(4)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		person.setLastName("Piquet Souto Maior");
		
		PersonVO persistedPerson = given()
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
				.body(person, objectMapper)
				.when()
					.put()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.as(PersonVO.class, objectMapper);
		
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertNotNull(persistedPerson.getEnabled());
		
		assertTrue(persistedPerson.getId() > 0);
		
		assertEquals(person.getId(), persistedPerson.getId());
		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
		assertEquals(false, persistedPerson.getEnabled());
	}
	
	@Test
	@Order(5)
	public void testDelete() {
		
		given()
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
			.pathParam("id", person.getId())
			.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}
	
	@Test
	@Order(6)
	public void testFindAll() throws JsonMappingException, JsonProcessingException  {
		Integer page = 3;
		Integer size = 10;
		String direction = "asc";
		
		var wrapper = given()
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
					.as(PagedModelPerson.class, objectMapper);
		
		
		List<PersonVO> resultList = wrapper.getContent();
		
		assertNotNull(resultList);
		
		PersonVO personOne = resultList.get(0);

		assertEquals(677, personOne.getId());
		assertEquals("Alic", personOne.getFirstName());
		assertEquals("Terbrug", personOne.getLastName());
		assertEquals("3 Eagle Crest Court", personOne.getAddress());
		assertEquals("Male", personOne.getGender());
		assertEquals(true, personOne.getEnabled());
		
		PersonVO personTwo = resultList.get(1);

		assertEquals(414, personTwo.getId());
		assertEquals("Alie", personTwo.getFirstName());
		assertEquals("Yeld", personTwo.getLastName());
		assertEquals("42 Messerschmidt Crossing", personTwo.getAddress());
		assertEquals("Female", personTwo.getGender());
		assertEquals(false, personTwo.getEnabled());
	}
	
	@Test
	@Order(7)
	public void testFindByName() throws JsonMappingException, JsonProcessingException  {
		String firstName = "ayr";
		Integer page = 0;
		Integer size = 10;
		String direction = "asc";
		
		var wrapper = given()
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
				.pathParam("firstName", firstName)
				.queryParam("page", page)
				.queryParam("size", size)
				.queryParam("direction", direction)
				.when()
					.get("findPersonByName/{firstName}")
				.then()
					.statusCode(200)
				.extract()
					.body()
					.as(PagedModelPerson.class, objectMapper);
		
		
		List<PersonVO> resultList = wrapper.getContent();
		
		assertNotNull(resultList);
		
		PersonVO personOne = resultList.get(0);
		
		assertEquals(1, personOne.getId());
		assertEquals("Ayrton", personOne.getFirstName());
		assertEquals("Senna", personOne.getLastName());
		assertEquals("São Paulo", personOne.getAddress());
		assertEquals("Male", personOne.getGender());
		assertEquals(true, personOne.getEnabled());
	}
	
	@Test
	@Order(8)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException  {
		
		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
		given()
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
			.spec(specificationWithoutToken)
			.contentType(TestConfigs.CONTENT_TYPE_YML)
			.accept(TestConfigs.CONTENT_TYPE_YML)
			.when()
				.get()
			.then()
				.statusCode(403)
					.extract()
					.body()
						.asString();
	}
	
	@Test
	@Order(9)
	public void testHATEOAS() {
		Integer page = 3;
		Integer size = 10;
		String direction = "asc";
		
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
		
		assertTrue(content.contains("href: \"http://localhost/api/person/v1/677\""));
		assertTrue(content.contains("href: \"http://localhost/api/person/v1/414\""));
		assertTrue(content.contains("href: \"http://localhost/api/person/v1/846\""));
		
		assertTrue(content.contains("rel: \"first\"  href: \"http://localhost/api/person/v1?direction=asc&page=0&size=10&sort=firstName,asc\""));
		assertTrue(content.contains("rel: \"prev\"  href: \"http://localhost/api/person/v1?direction=asc&page=2&size=10&sort=firstName,asc\""));
		assertTrue(content.contains("rel: \"self\"  href: \"http://localhost/api/person/v1?page=3&size=10&direction=asc\""));
		assertTrue(content.contains("rel: \"next\"  href: \"http://localhost/api/person/v1?direction=asc&page=4&size=10&sort=firstName,asc\""));
		assertTrue(content.contains("rel: \"last\"  href: \"http://localhost/api/person/v1?direction=asc&page=100&size=10&sort=firstName,asc\""));
		assertTrue(content.contains("page:  size: 10  totalElements: 1007  totalPages: 101  number: 3"));
	}

	private void mockPerson() {
		person.setFirstName("Nelson");
		person.setLastName("Piquet");
		person.setAddress("Brasília - DF - Brasil");
		person.setGender("Male");
		person.setEnabled(true);
	}

}
