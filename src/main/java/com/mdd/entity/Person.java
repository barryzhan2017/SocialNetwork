package com.mdd.entity;

import org.neo4j.ogm.annotation.*;

import java.util.*;


@NodeEntity
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    @Property
    private int age;

    @Relationship(type = "Trust")
    private List<TrustRelation> trustedPeople;

    @Property
    private String name;

    //Times recorded for being rated by someone
    @Property
    private List<Integer> ratedTimes;

    @Property
    private List<Long> ids;

    public Person(long id){
        this.id = id;
    }

    public Person() {}



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

    public List<TrustRelation> getTrustedPeople() {
        return trustedPeople;
    }

    public void setTrustedPeople(List<TrustRelation> trustedPeople) {
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
            trustedPeople = new ArrayList<>();
        }
        TrustRelation trustRelation = new TrustRelation(this, person, trustIndex, probability);
        trustedPeople.add(trustRelation);
    }

    public void trust(TrustRelation trustRelation) {
        if (trustedPeople == null) {
            trustedPeople = new ArrayList<>();
        }
        trustedPeople.add(trustRelation);
    }

    /**
     * Update the times it's been rated by some one
     * @param id Rating person's Id
     */
    public void ratedById(long id) {
        if (ratedTimes == null) {
            ratedTimes = new ArrayList<>();
            ids = new ArrayList<>();
            ratedTimes.add(1);
            ids.add(id);
        }
        else {
            int index = ids.indexOf(id);
            if (index == -1) {
                ratedTimes.add(1);
                ids.add(id);
            }
            else
                ratedTimes.set(index, ratedTimes.get(index) + 1);
        }
    }

    public List<Integer> getRatedTimes() {
        return ratedTimes;
    }

    public void setRatedTimes(List<Integer> ratedTimes) {
        this.ratedTimes = ratedTimes;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    /**
     * Get the times rated by the source person
     * @param id Id of the source person
     * @return Times rated
     */
    public int getRatedTimesById(long id) {
        if (ratedTimes == null || ids == null || ids.indexOf(id) == -1)
            throw new NullPointerException("Rating for this person " + this.id +
                    " from person " + id + "has not been established!");
        return ratedTimes.get(ids.indexOf(id));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", age=" + age +
                ", trustedPeople=" + trustedPeople +
                ", name='" + name + '\'' +
                ", ratedTimes=" + ratedTimes +
                ", ids=" + ids +
                '}';
    }
}