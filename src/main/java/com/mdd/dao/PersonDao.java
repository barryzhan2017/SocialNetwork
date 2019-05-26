package com.mdd.dao;

import com.mdd.entity.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonDao extends Neo4jRepository<Person, Long> {
    Iterable<Person> findPeopleByName(String name);


}
