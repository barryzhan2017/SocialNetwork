package com.loading.neo4j.entity;

import org.neo4j.ogm.annotation.*;

import java.util.Date;

@RelationshipEntity(type = "TRUST")
public class TrustRelation  {


    @GraphId
    private Long id;

    @StartNode
    private Person source;

    @EndNode
    private Person target;

    @Property
    private String relationName = "trust";

    @Property
    private Long added = new Date().getTime();

    @Property
    private int trustIndex;

    @Property
    private double probability;

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public int getTrustIndex() {
        return trustIndex;
    }

    public void setTrustIndex(int trustIndex) {
        this.trustIndex = trustIndex;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public TrustRelation(){

    }

    public TrustRelation(Person source, Person target, int trustIndex
    , double probability) {
        this.source = source;
        this.target = target;
        this.trustIndex = trustIndex;
        this.probability = probability;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Person getSource() {
        return source;
    }


    public void setSource(Person source) {
        this.source = source;
    }


    public Person getTarget() {
        return target;
    }


    public void setTarget(Person target) {
        this.target = target;
    }


    public Long getAdded() {
        return added;
    }


    public void setAdded(Long added) {
        this.added = added;
    }


    public String getRelationName() {
        return relationName;
    }






}
