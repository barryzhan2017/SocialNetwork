package com.loading.neo4j.entity;

import com.loading.neo4j.entity.Basic.BasicNodeInterface;
import com.loading.neo4j.entity.Basic.InvestNode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * desc:
 * Created on 2017/10/10.
 *
 * @author Lo_ading
 * @version 1.0.0
 * @since 1.0.0
 */
@NodeEntity
public class Person extends InvestNode implements BasicNodeInterface{

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

    public void trust(Person person) {
        if (trustedPeople == null) {
            trustedPeople = new HashSet<>();
        }
        TrustRelation trustRelation = new TrustRelation(this, person);
        trustedPeople.add(trustRelation);
    }


}
