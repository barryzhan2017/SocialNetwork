package com.mdd.algorithm;

public class MDD {

    private RelationshipNode rootNode;
    private int order;

    public MDD (RelationshipNode rootNode) {
        this.rootNode = rootNode;
        order = this.rootNode.getRelationship().getOrder();
    }

    /**
     * And this mdd with the other one by using and operation for their sub-nodes
     * @param otherMDD The target mdd to and with
     * @return The mdd combined with the other one with the and operation
     */
    public MDD and(MDD otherMDD) {
        MDD mddAfterAndOperation;
        int otherMDDOrder = otherMDD.getOrder();
        if (otherMDDOrder >= order) {
            mddAfterAndOperation = new MDD(rootNode);
            rootNode.and(otherMDD.rootNode);
        }
        else {
            mddAfterAndOperation = new MDD(otherMDD.rootNode);
            otherMDD.rootNode.and(rootNode);
        }
        return mddAfterAndOperation;
    }

    /**
     * Or this mdd with the other one by using or operation for their sub-nodes
     * @param otherMDD The target mdd to or with
     * @return The mdd combined with the other one with the or operation
     */
    public MDD or(MDD otherMDD) {
        MDD mddAfterAndOperation;
        int otherMDDOrder = otherMDD.getOrder();
        if (otherMDDOrder >= order) {
            mddAfterAndOperation = new MDD(rootNode);
            rootNode.or(otherMDD.rootNode);
        }
        else {
            mddAfterAndOperation = new MDD(otherMDD.rootNode);
            otherMDD.rootNode.or(rootNode);
        }
        return mddAfterAndOperation;
    }


    public RelationshipNode getRootNode() {
        return rootNode;
    }


    public int getOrder() {
        return order;
    }

}
