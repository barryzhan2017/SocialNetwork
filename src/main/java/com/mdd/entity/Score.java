package com.mdd.entity;

import org.neo4j.ogm.annotation.*;

@NodeEntity
public class Score {

    @Id
    @GeneratedValue
    private Long id;

    @Property
    private Long sourceId;

    @Property
    private  Long targetId;

    @Property
    private double score;

    @Property
    private int trustLevel;

    @Relationship(type = "CreatedFrom")
    private RelationNode relationNode;

    public Score() {
    }

    public Score(long sourceId, long targetId, double score, int trustLevel, RelationNode relationNode) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.score = score;
        this.trustLevel = trustLevel;
        this.relationNode = relationNode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public RelationNode getRelationNode() {
        return relationNode;
    }

    public void setRelationNode(RelationNode relationNode) {
        this.relationNode = relationNode;
    }

    public int getTrustLevel() {
        return trustLevel;
    }

    public void setTrustLevel(int trustLevel) {
        this.trustLevel = trustLevel;
    }
}
