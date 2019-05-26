package com.mdd.entity;

import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type = "Trust")
public class TrustRelation  {


    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private Person source;

    @EndNode
    private Person target;

    @Property
    private int trustIndex;

    @Property
    private double probability;

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

}