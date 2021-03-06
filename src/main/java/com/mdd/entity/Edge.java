package com.mdd.entity;

import org.neo4j.ogm.annotation.*;

//Have to use edge to connect relation node because there will be same node that cannot be identified
@RelationshipEntity(type = "Link")
public class Edge {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private RelationNode source;

    @EndNode
    private RelationNode target;

    @Property
    private double probability;

    @Property
    private int index;

    public Edge() {}

    public Edge(RelationNode source, RelationNode target, double probability, int index) {
        this.source = source;
        this.target = target;
        this.probability = probability;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RelationNode getSource() {
        return source;
    }

    public void setSource(RelationNode source) {
        this.source = source;
    }

    public RelationNode getTarget() {
        return target;
    }

    public void setTarget(RelationNode target) {
        this.target = target;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}
