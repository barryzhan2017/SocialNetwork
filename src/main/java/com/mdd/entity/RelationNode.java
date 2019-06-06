package com.mdd.entity;

import com.mdd.algorithm.RelationshipNode;
import org.neo4j.ogm.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NodeEntity
public class RelationNode {

    @Id
    @GeneratedValue
    private Long id;

    @Property
    private Long sourceId;

    @Property
    private Long targetId;

    @Relationship
    private List<Edge> edges;

    @Property
    private int value;

    @Property
    private int order;

    public RelationNode() {}
    public RelationNode(Long sourceId, Long targetId, List<Edge> edges, int value, int order) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.edges = edges;
        this.value = value;
        this.order = order;
    }

    //Recursively initialize the relation node by relationship node
    public RelationNode(RelationshipNode rootNode, Map<RelationshipNode, RelationNode> map) {
        map.put(rootNode, this);
        value = rootNode.getValue();
        com.mdd.algorithm.Relationship relationship = rootNode.getRelationship();
        // For none sink-node, put the relationships into the relationNode
        if (relationship != null) {
            sourceId = relationship.getStartNode();
            targetId = relationship.getEndNode();
            double[] trustProbability = relationship.getTrustProbability();
            edges = new ArrayList<>();
            order = relationship.getOrder();
            int i = 0;
            for (RelationshipNode relationshipNode : rootNode.getTrustLevelToNextNode().values()) {
                //Check for duplicate nodes, if so just add the existing relation node
                // Don't use get or default because it will replace the value node in the map by calling new.
                RelationNode relationNode = map.get(relationshipNode);
                if (relationNode == null) relationNode = new RelationNode(relationshipNode, map);
                edges.add(new Edge(this, relationNode, trustProbability[i], i++));
            }
        }
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
