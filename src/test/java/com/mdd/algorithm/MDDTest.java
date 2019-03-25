package com.mdd.algorithm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class MDDTest {


    // Number of trust level is 3
    // One mdd order is 1. State 0 and 2 -- sink node 0. State 1 -- sink node 1
    // The other mdd order is 2. State 0 -- sink node 0. State 1 and 2 -- sink node 1
    // Result mdd is order 1. State 0 -- sink node 0. State 1 -- the other mdd. State 2 -- sink node 1.
    @Test
    public void shouldAndOperationWorkCorrectlyWhenApplyingToTwoMDDs() {
        Relationship relationship = new Relationship(3, 0, 1, new double[] {0.1, 0.1, 0.8});
        relationship.setOrder(1);
        RelationshipNode rootNode = new RelationshipNode(relationship);
        RelationshipNode sinkNode0ForState0 = new RelationshipNode(0);
        RelationshipNode sinkNode0ForState2 = new RelationshipNode(0);
        RelationshipNode sinkNode1ForState1 = new RelationshipNode(1);
        rootNode.putNextNode(0, sinkNode0ForState0);
        rootNode.putNextNode(1, sinkNode1ForState1);
        rootNode.putNextNode(2, sinkNode0ForState2);
        MDD mdd = new MDD(rootNode);

        Relationship otherRelationship = new Relationship(3, 1, 2, new double[] {0.1, 0.2, 0.7});
        otherRelationship.setOrder(2);
        RelationshipNode otherRootNode = new RelationshipNode(otherRelationship);
        RelationshipNode otherSinkNode0ForState0 = new RelationshipNode(0);
        RelationshipNode otherSinkNode1ForState2 = new RelationshipNode(1);
        RelationshipNode otherSinkNode1ForState1 = new RelationshipNode(1);
        otherRootNode.putNextNode(0, otherSinkNode0ForState0);
        otherRootNode.putNextNode(1, otherSinkNode1ForState1);
        otherRootNode.putNextNode(2, otherSinkNode1ForState2);
        MDD otherMDD = new MDD(otherRootNode);

        otherRootNode.setParentNode(rootNode);
        MDD resultMDD = mdd.and(otherMDD);
        RelationshipNode resultRootNode = resultMDD.getRootNode();
        assertEquals(relationship, resultRootNode.getRelationship());
        assertEquals(sinkNode0ForState0, resultRootNode.getNextNode(0));
        assertEquals(otherRootNode, resultRootNode.getNextNode(1));
        assertEquals(sinkNode0ForState2, resultRootNode.getNextNode(2));
    }
}
