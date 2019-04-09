package com.mdd.algorithm;

import java.util.Objects;

import static com.mdd.common.CommonConstant.NO_ORDERED;

public class MDD {

    private RelationshipNode rootNode = null;
    private int order = NO_ORDERED;

    MDD (RelationshipNode rootNode) {
        if (rootNode == null) {
            throw new NullPointerException("Initialize the mdd with null pointer as the root node!");
        }
        this.rootNode = rootNode;
        order = this.rootNode.getRelationship().getOrder();
    }

    public void setRootNode(RelationshipNode rootNode) {
        this.rootNode = rootNode;
    }

    /**
     * And this mdd with the other one by using and operation for their sub-nodes
     * Time Complexity should be T^(log(T)(n))
     * @param otherMDD The target mdd to and with
     * @return The mdd combined with the other one with the and operation or null if the other mdd is null
     */
    public MDD and(MDD otherMDD) {
        if (otherMDD == null || rootNode == null) {
            return null;
        }
        MDD mddAfterAndOperation;
        int otherMDDOrder = otherMDD.getOrder();
        if (otherMDDOrder >= order) {
            mddAfterAndOperation = new MDD(rootNode);
        }
        else {
            mddAfterAndOperation = new MDD(otherMDD.getRootNode());
        }
        rootNode.and(otherMDD.getRootNode());
        return mddAfterAndOperation;
    }

    /**
     * Or this mdd with the other one by using or operation for their sub-nodes
     * @param otherMDD The target mdd to or with
     * @return The mdd combined with the other one with the or operation or null if the other mdd is null
     */
    public MDD or(MDD otherMDD) {
        if (otherMDD == null || rootNode == null) {
            return null;
        }
        MDD mddAfterAndOperation;
        int otherMDDOrder = otherMDD.getOrder();
        if (otherMDDOrder >= order) {
            mddAfterAndOperation = new MDD(rootNode);
            rootNode.or(otherMDD.getRootNode());
        }
        else {
            mddAfterAndOperation = new MDD(otherMDD.getRootNode());
            otherMDD.getRootNode().or(rootNode);
        }
        return mddAfterAndOperation;
    }


    public RelationshipNode getRootNode() {
        return rootNode;
    }


    private int getOrder() {
        return order;
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
