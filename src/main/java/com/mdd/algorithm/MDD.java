package com.mdd.algorithm;

import com.mdd.entity.RelationNode;

import java.util.Objects;

import static com.mdd.common.CommonConstant.NO_ORDERED;

public class MDD {

    private RelationshipNode rootNode = null;

    MDD (RelationshipNode rootNode) {
        if (rootNode == null) {
            throw new NullPointerException("Initialize the mdd with null pointer as the root node!");
        }
        this.rootNode = rootNode;
    }

    void setRootNode(RelationshipNode rootNode) {
        this.rootNode = rootNode;
    }

    /**
     * And this mdd with the other one by using and operation for their sub-nodes
     * Time Complexity should be T^(log(T)(n))
     * @param otherMDD The target mdd to and with
     * @return The mdd combined with the other one with the and operation or null if the other mdd is null
     */
    public MDD and(MDD otherMDD) {
        return operate(otherMDD, true);
    }

    /**
     * Or this mdd with the other one by using or operation for their sub-nodes
     * @param otherMDD The target mdd to or with
     * @return The mdd combined with the other one with the or operation or null if the other mdd is null
     */
    MDD or(MDD otherMDD) {
        return operate(otherMDD, false);
    }

    private MDD operate(MDD otherMDD, boolean isAnd) {
        if (otherMDD == null)
            return this;
        MDD mddAfterAndOperation;
        int otherMDDOrder = otherMDD.getOrder();
        RelationshipNode relationshipNode = new RelationshipNode(-1);
        rootNode.copyTo(relationshipNode);
        RelationshipNode otherRelationshipNode = new RelationshipNode(-1);
        otherMDD.getRootNode().copyTo(otherRelationshipNode);
        if (otherMDDOrder >= getOrder())
            mddAfterAndOperation = new MDD(relationshipNode);
        else
            mddAfterAndOperation = new MDD(otherRelationshipNode);
        if (isAnd)
            relationshipNode.and(otherRelationshipNode);
        else
            relationshipNode.or(otherRelationshipNode);
        return mddAfterAndOperation;

    }

    public RelationshipNode getRootNode() {
        return rootNode;
    }


    private int getOrder() {
        return getRootNode().getRelationship().getOrder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MDD mdd = (MDD) o;
        return Objects.equals(rootNode, mdd.rootNode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(rootNode);
    }
}
