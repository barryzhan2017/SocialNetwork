package com.loading.neo4j.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;


@NodeEntity
public class Person {

    @GraphId
    private Long id;

    private int age;

    @Relationship(type = "Trust", direction = Relationship.UNDIRECTED)
    private Set<TrustRelation> trustedPeople;

    private String name;

    public Person(){
        // Empty constructor required as of Neo4j API 2.0.5
    }

    public Person(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Set<TrustRelation> getTrustedPeople() {
        return trustedPeople;
    }

    public void setTrustedPeople(Set<TrustRelation> trustedPeople) {
        this.trustedPeople = trustedPeople;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void trust(Person person, int trustIndex, double probability) {
        if (trustedPeople == null) {
            trustedPeople = new HashSet<>();
        }
        TrustRelation trustRelation = new TrustRelation(this, person, trustIndex, probability);
        trustedPeople.add(trustRelation);
    }


}
