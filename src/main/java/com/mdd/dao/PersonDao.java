package com.mdd.dao;

import com.mdd.entity.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * desc:
 * Created on 2017/10/13.
 *
 * @author Lo_ading
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public interface PersonDao extends Neo4jRepository<Person, Long> {
    Iterable<Person> findPeopleByName(String name);
}
