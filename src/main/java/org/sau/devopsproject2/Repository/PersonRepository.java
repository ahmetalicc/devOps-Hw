package org.sau.devopsproject2.Repository;


import org.sau.devopsproject2.Entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person,Integer> {
}
