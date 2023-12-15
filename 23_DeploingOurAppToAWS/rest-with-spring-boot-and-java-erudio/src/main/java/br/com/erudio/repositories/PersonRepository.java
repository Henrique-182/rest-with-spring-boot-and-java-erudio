package br.com.erudio.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.erudio.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
	
	@Modifying
	@Query("UPDATE Person P SET P.enabled = false WHERE P.id = :id")
	void disablePerson(@Param("id") Long id);
	
	@Query("SELECT P FROM Person P WHERE P.firstName LIKE LOWER(CONCAT('%', :firstName, '%'))")
	Page<Person> findPersonByName(@Param("firstName") String firstName, Pageable pageable);
}