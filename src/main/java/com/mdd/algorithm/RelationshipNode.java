package com.mdd.algorithm;

import com.rits.cloning.Cloner;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.mdd.common.CommonConstant.NO_SUCH_CHILD_NODE;

public class RelationshipNode{

    private HashMap<Integer, RelationshipNode> trustLevelToNextNode = null;
    //The value for the sink node, which should be 1 or 0
    private int value = -1;
    private Relationship relationship = null;
    private RelationshipNode parent = null;

    RelationshipNode(Relationship relationship) {
        this.relationship = relationship;
        trustLevelToNextNode = new HashMap<>();
    }

    RelationshipNode(Relationship relationship, RelationshipNode parent) {
        this.relationship = relationship;
        trustLevelToNextNode = new HashMap<>();
        this.parent = parent;
    }

    //For sink node that has value to set
    RelationshipNode(int value) {
        this.value = value;
    }

    //For sink node that has value to set
    RelationshipNode(int value, RelationshipNode parent) {
        this.value = value;
        this.parent = parent;
    }

    public RelationshipNode getParent() {
        return parent;
    }

    public void setParent(RelationshipNode parent) {
        this.parent = parent;
    }


    /**
     * Get the index of the current node pointed from the parent
     * @return The number of trust level or -1 for not finding this child condition.
     */
    public int getTrustLevelFromParentToThis() {
        if (getParent() == null) throw new NullPointerException("Parent Node is null!");
        for (Map.Entry<Integer, RelationshipNode> entry : getParent().getTrustLevelToNextNode().entrySet()) {
            if (entry.getValue() == this) return entry.getKey();
        }
        throw new IllegalArgumentException("Cannot find the child node in the parent's map!");
    }

    /**
     * Deep copy the current relationship node to the input node.
     * @param relationshipNode Other relationship node
     */
    void copyTo(RelationshipNode relationshipNode) {
        if (getRelationship() == null) {
            relationshipNode.setRelationship(null);
        }
        else {
            relationshipNode.setRelationship(new Relationship(getRelationship()));
        }
        Cloner cloner = new Cloner();
        cloner.setDumpClonedClasses(true);
        HashMap<Integer, RelationshipNode> newTrustLevelToNextNode
                = (HashMap<Integer, RelationshipNode>)cloner.deepClone(getTrustLevelToNextNode());
        relationshipNode.setTrustLevelToNextNode(newTrustLevelToNextNode);
        //To avoid the sink node condition in which the children nodes are null.
        if (newTrustLevelToNextNode != null) {
            for (RelationshipNode childNodes : relationshipNode.getTrustLevelToNextNode().values()) {
                childNodes.setParent(relationshipNode);
            }
        }
        relationshipNode.setValue(getValue());
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
            trustLevelToNextNode.put(trustLevel, nextRelationshipNode);
        }
        else {
            throw new NullPointerException("Map for trustLevelToNextNode is null!");
        }
    }

    /**
     * Replace with new relationship node at the specific trust level
     * @param trustLevel State of the node to go
     * @param newRelationshipNode The new relationship node to be placed at the trust level
     */
    public void replaceNextNode(int trustLevel, RelationshipNode newRelationshipNode) {
        if (trustLevelToNextNode != null && trustLevelToNextNode.containsKey(trustLevel)) {
            trustLevelToNextNode.replace(trustLevel, newRelationshipNode);
        }
        else {
            throw new NullPointerException("Map for trustLevelToNextNode is null or this trust level has not been set!");
        }
    }

    int getValue() {
        return value;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    private void setTrustLevelToNextNode(HashMap<Integer, RelationshipNode> trustLevelToNextNode) {
        this.trustLevelToNextNode = trustLevelToNextNode;
    }

    private void setValue(int value) {
        this.value = value;
    }

    private void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    /**
     * Recursive call to build up the new MDD by using and operation for the other node based on the comparison of two nodes
     * Set the smaller order node as the root node (For the same order situation, choose the current node because of changing child node strategy) 
     * and use it to replace next smaller order node to the current mdd if needed. Continue the and operation
     * If the current node reaches the sink node, change the sink node to the other node if the value is 1
     * Otherwise remain it as the 0 sink node. The rule applies to the other node when it reaches the sink node.
     * Notice that the changing process should be deep copy so that there will not be any conflict with the copied part.
     * @param otherNode The other node in other mdd.
     */
    public void and(RelationshipNode otherNode) {
        if (isLast()) {
            // 0 and x = 0
            if (getValue() == 0) {

            }
            // 1 and x = x
            else if (getValue() == 1) {
                otherNode.copyTo(this);
            }
            else {
                throw new IllegalArgumentException("Sink node's value is not 1 or 0!");
            }
        }
        else if (otherNode.isLast()) {
            int otherValue = otherNode.getValue();
            // x and 1 = x
            if (otherValue == 1) {

            }
            // x and 0 = 0
            else if (otherValue == 0) {
                otherNode.copyTo(this);
            }
        }
        else {
            int numberOfTrustLevel = getTrustLevelToNextNode().size();
            int thisNodeOrder = getRelationship().getOrder();
            int otherNodeOrder = otherNode.getRelationship().getOrder();
            //Parent node when getting into the next step
            RelationshipNode nextRootNode;
            if (thisNodeOrder == otherNodeOrder) {
                nextRootNode = this;
                for (int i = 0; i < numberOfTrustLevel; i++) {
                    RelationshipNode nextThisNode =  nextRootNode.getTrustLevelToNextNode().get(i);
                    RelationshipNode nextOtherNode = otherNode.getTrustLevelToNextNode().get(i);
                    changeChildNodeIfOrderIsLarger(nextRootNode, nextOtherNode, i);
                    nextThisNode.and(nextOtherNode);
                }
            }
            else if (thisNodeOrder < otherNodeOrder) {
                nextRootNode = this;
                for (int i = 0; i < numberOfTrustLevel; i++) {
                    RelationshipNode nextThisNode =  nextRootNode.getTrustLevelToNextNode().get(i);
                    changeChildNodeIfOrderIsLarger(nextRootNode, otherNode, i);
                    nextThisNode.and(otherNode);
                }
            }
            else {
                //Next root node should be the other node because the order is smaller
                nextRootNode = otherNode;
                for (int i = 0; i < numberOfTrustLevel; i++) {
                    RelationshipNode nextOtherNode =  nextRootNode.getTrustLevelToNextNode().get(i);
                    changeChildNodeIfOrderIsLarger(nextRootNode, this, i);
                    nextOtherNode.and(this);
                }
            }
        }
    }
    

    /**
     * Recursive call to build up the new MDD by using or operation for the other node based on the comparison of two nodes
     * Set the smaller order node as the root node (For the same order situation, choose the current node) 
     * and use it to replace next smaller order node to the current mdd if needed. Continue the or operation
     * If the current node reaches the sink node, change the sink node to the other node if the value is 0
     * Otherwise remain it as the 1 sink node. The rule applies to the other node when it reaches the sink node.
     * Notice that the changing process should be deep copy so that there will not be any conflict with the copied part.
     * @param otherNode The other node in other mdd.
     */
    public void or(RelationshipNode otherNode) {
        if (isLast()) {
            // 0 or x = x
            if (getValue() == 0) {
                otherNode.copyTo(this);
            }
            // 1 or x = 1
            else if (getValue() == 1) {

            }
            else {
                throw new IllegalArgumentException("Sink node's value is not 1 or 0!");
            }
        }
        else if (otherNode.isLast()) {
            int otherValue = otherNode.getValue();
            // x or 0 = x
            if (otherValue == 0) {

            }
            // x or 1 = 1
            else if (otherValue == 1) {
                otherNode.copyTo(this);
            }
        }
        else {
            int numberOfTrustLevel = trustLevelToNextNode.size();
            int thisNodeOrder = relationship.getOrder();
            int otherNodeOrder = otherNode.relationship.getOrder();
            //Parent node when getting into the next step
            RelationshipNode nextRootNode;
            if (thisNodeOrder == otherNodeOrder) {
                nextRootNode = this;
                for (int i = 0; i < numberOfTrustLevel; i++) {
                    RelationshipNode nextThisNode =  nextRootNode.getTrustLevelToNextNode().get(i);
                    RelationshipNode nextOtherNode = otherNode.getTrustLevelToNextNode().get(i);
                    changeChildNodeIfOrderIsLarger(nextRootNode, nextOtherNode, i);
                    nextThisNode.or(nextOtherNode);
                }
            }
            else if (thisNodeOrder < otherNodeOrder) {
                nextRootNode = this;
                for (int i = 0; i < numberOfTrustLevel; i++) {
                    RelationshipNode nextThisNode =  nextRootNode.getTrustLevelToNextNode().get(i);
                    changeChildNodeIfOrderIsLarger(nextRootNode, otherNode, i);
                    nextThisNode.or(otherNode);
                }
            }
            else {
                //Next root node should be the other node because the order is smaller
                nextRootNode = otherNode;
                for (int i = 0; i < numberOfTrustLevel; i++) {
                    RelationshipNode nextOtherNode =  nextRootNode.getTrustLevelToNextNode().get(i);
                    changeChildNodeIfOrderIsLarger(nextRootNode, this, i);
                    nextOtherNode.or(this);
                }
            }
        }
    }
    

    /**
     * Change the root node's child node to the next other node if this node's order is larger
     * Not change the pointer to the child node when this node is sink node 0 or 1
     * Because the next step will copy the value from the other node to this node
     * @param newRootNode This parent node for the next step
     * @param nextOtherNode The next other node to be applied and operation with next this node
     * @param trustLevel The trust level leads to the next this node
     */
    private void changeChildNodeIfOrderIsLarger(RelationshipNode newRootNode, RelationshipNode nextOtherNode, int trustLevel) {
        RelationshipNode nextThisNode = newRootNode.getTrustLevelToNextNode().get(trustLevel);
        if (!nextThisNode.isLast() && !nextOtherNode.isLast()
                && nextOtherNode.getRelationship().getOrder() < nextThisNode.getRelationship().getOrder()) {
            newRootNode.getTrustLevelToNextNode().replace(trustLevel, nextOtherNode);
            nextOtherNode.setParent(newRootNode);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationshipNode that = (RelationshipNode) o;
        return value == that.value &&
                Objects.equals(trustLevelToNextNode, that.trustLevelToNextNode) &&
                Objects.equals(relationship, that.relationship);
    }

    @Override
    public int hashCode() {

        return Objects.hash(trustLevelToNextNode, value, relationship);
    }
    
    Map<Integer, RelationshipNode> getTrustLevelToNextNode() {
        return trustLevelToNextNode;
    }
}
