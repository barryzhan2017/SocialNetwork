package com.mdd.algorithm;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RelationshipNode{

    private Map<Integer, RelationshipNode> trustLevelToNextNode = null;
    //The value for the sink node, which should be 1 or 0
    private int value = -1;
    private Relationship relationship = null;
    private RelationshipNode parentNode = null;

    public RelationshipNode(Relationship relationship) {
        this.relationship = relationship;
        trustLevelToNextNode = new HashMap<>();
    }

    //For sink node that has value to set
    public RelationshipNode(int value) {
        this.value = value;
    }

    private int findTrustLevelFromRelationshipNode(RelationshipNode relationshipNode) {
        for (Map.Entry<Integer, RelationshipNode> entry: trustLevelToNextNode.entrySet()) {
            if (entry.getValue().equals(relationshipNode)) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Cannot find this relationship node in the map!");
     }

    public void setParentNode(RelationshipNode parentNode) {
        this.parentNode = parentNode;
    }

    /**
     * Get the next node according to the value
     * @param value trust level of the current node
     * @return Node when choosing the specific value
     */
    public RelationshipNode getNextNode(int value) {
        if (trustLevelToNextNode != null) {
            return trustLevelToNextNode.get(value);
        }
        else {
            return null;
        }
    }

    /**
     * Check if the current node is the last node
     * @return True if the node is the last one, false if not.
     */
    private boolean isLast() {
        return value != -1;
    }

    /**
     * Put next relationship node referred by the trust level
     * @param trustLevel State of the current node to go
     * @param nextRelationshipNode The next node when choosing the state
     */
    public void putNextNode(int trustLevel, RelationshipNode nextRelationshipNode) {
        if (trustLevelToNextNode != null) {
            nextRelationshipNode.parentNode = this;
            trustLevelToNextNode.put(trustLevel, nextRelationshipNode);
        }
        else {
            throw new NullPointerException("Map for trustLevelToNextNode is null!");
        }
    }

    public Map<Integer, RelationshipNode> getTrustLevelToNextNode() {
        return trustLevelToNextNode;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    /**
     * Recursive call to build up the new MDD by using and operation for the other node based on the comparison of two nodes
     * If the other node's order is smaller, let parent node point to the other node and continue the and operation
     * If the current node reaches the sink node, change the sink node to the other node if the value is 1
     * Otherwise remain it as the 0 sink node
     * @param otherNode The other node in other mdd.
     */
    public void and(RelationshipNode otherNode) {
        if (isLast()) {
            // 0 and x = 0
            if (value == 0) {
            }
            // 1 and x = x
            else if (value == 1) {
                trustLevelToNextNode = otherNode.trustLevelToNextNode;
                relationship = otherNode.relationship;
                value = otherNode.value;
                parentNode = otherNode.parentNode;
            }
            else {
                throw new IllegalArgumentException("Sink node's value is not 1 or 0!");
            }
        }
        else {
            int numberOfTrustLevel = trustLevelToNextNode.size();
            int thisNodeOrder = relationship.getOrder();
            int otherNodeOrder = otherNode.relationship.getOrder();
            if (thisNodeOrder == otherNodeOrder) {
                for (int i = 0; i < numberOfTrustLevel; i++) {
                    trustLevelToNextNode.get(i).and(otherNode.trustLevelToNextNode.get(i));
                }
            }
            else if (thisNodeOrder < otherNodeOrder) {
                for (int i = 0; i < numberOfTrustLevel; i++) {
                    trustLevelToNextNode.get(i).and(otherNode);
                }
            }
            else {
                int valueOfThisNode = parentNode.findTrustLevelFromRelationshipNode(this);
                parentNode.trustLevelToNextNode.replace(valueOfThisNode, otherNode);
                for (int i = 0; i < numberOfTrustLevel; i++) {
                    otherNode.trustLevelToNextNode.get(i).and(this);
                }
            }
        }
    }

    /**
     * Recursive call to build up the new MDD by using or operation for the other node based on the comparison of two nodes
     * If the other node's order is smaller, let parent node point to the other node and continue the or operation
     * If the current node reaches the sink node, change the sink node to the other node if the value is 0
     * Otherwise remain it as the 1 sink node
     * @param otherNode The other node in other mdd.
     */
    public void or(RelationshipNode otherNode) {
        if (isLast()) {
            // 0 or x = x
            if (value == 0) {
                trustLevelToNextNode = otherNode.trustLevelToNextNode;
                relationship = otherNode.relationship;
                value = otherNode.value;
                parentNode = otherNode.parentNode;
            }
            // 1 or x = 1
            else if (value == 1) {

            }
            else {
                throw new IllegalArgumentException("Sink node's value is not 1 or 0!");
            }
        }
        else {
            int numberOfTrustLevel = trustLevelToNextNode.size();
            int thisNodeOrder = relationship.getOrder();
            int otherNodeOrder = otherNode.relationship.getOrder();
            if (thisNodeOrder == otherNodeOrder) {
                for (int i = 0; i < numberOfTrustLevel; i++) {
                    trustLevelToNextNode.get(i).or(otherNode.trustLevelToNextNode.get(i));
                }
            }
            else if (thisNodeOrder < otherNodeOrder) {
                for (int i = 0; i < numberOfTrustLevel; i++) {
                    trustLevelToNextNode.get(i).or(otherNode);
                }
            }
            else {
                int valueOfThisNode = parentNode.findTrustLevelFromRelationshipNode(this);
                parentNode.trustLevelToNextNode.replace(valueOfThisNode, otherNode);
                for (int i = 0; i < numberOfTrustLevel; i++) {
                    otherNode.trustLevelToNextNode.get(i).or(this);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationshipNode that = (RelationshipNode) o;
        return value == that.value &&
                Objects.equals(trustLevelToNextNode, that.trustLevelToNextNode) &&
                Objects.equals(relationship, that.relationship) &&
                Objects.equals(parentNode, that.parentNode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(trustLevelToNextNode, value, relationship, parentNode);
    }
}
