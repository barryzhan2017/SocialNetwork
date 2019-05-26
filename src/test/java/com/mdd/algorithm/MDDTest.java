package com.mdd.algorithm;

import org.junit.Test;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.mdd.common.CommonTestConstant.checkParents;
import static org.junit.Assert.*;


public class MDDTest {


    // Number of trust level is 3
    // One mdd order is 1. State 0 and 2 -- sink node 0. State 1 -- sink node 1
    // The other mdd order is 2. State 0 -- sink node 0. State 1 and 2 -- sink node 1
    // Result mdd is order 1. State 0 -- sink node 0. State 1 -- the other mdd. State 2 -- sink node 1.
    // Check the commutativity for the operation.
    @Test
    public void shouldAndOperationWorkCorrectlyWhenApplyingToTwoMDDs() {
        Relationship relationship = new Relationship(3, 0, 1, new double[]{0.1, 0.1, 0.8});
        relationship.setOrder(1);
        RelationshipNode rootNode = new RelationshipNode(relationship);
        RelationshipNode sinkNode0ForState0 = new RelationshipNode(0, rootNode);
        RelationshipNode sinkNode0ForState2 = new RelationshipNode(0, rootNode);
        RelationshipNode sinkNode1ForState1 = new RelationshipNode(1, rootNode);
        rootNode.putNextNode(0, sinkNode0ForState0);
        rootNode.putNextNode(1, sinkNode1ForState1);
        rootNode.putNextNode(2, sinkNode0ForState2);
        MDD mdd = new MDD(rootNode);

        Relationship otherRelationship = new Relationship(3, 1, 2, new double[]{0.1, 0.2, 0.7});
        otherRelationship.setOrder(2);
        RelationshipNode otherRootNode = new RelationshipNode(otherRelationship);
        RelationshipNode otherSinkNode0ForState0 = new RelationshipNode(0, otherRootNode);
        RelationshipNode otherSinkNode1ForState2 = new RelationshipNode(1, otherRootNode);
        RelationshipNode otherSinkNode1ForState1 = new RelationshipNode(1, otherRootNode);
        otherRootNode.putNextNode(0, otherSinkNode0ForState0);
        otherRootNode.putNextNode(1, otherSinkNode1ForState1);
        otherRootNode.putNextNode(2, otherSinkNode1ForState2);
        MDD otherMDD = new MDD(otherRootNode);

        MDD resultMDD = mdd.and(otherMDD);

        RelationshipNode resultRootNode = resultMDD.getRootNode();
        assertEquals(relationship, resultRootNode.getRelationship());
        assertEquals(sinkNode0ForState0, resultRootNode.getNextNode(0));
        assertEquals(otherRootNode, resultRootNode.getNextNode(1));
        assertEquals(sinkNode0ForState2, resultRootNode.getNextNode(2));
        checkParents(resultRootNode, 3);
    }


    // Number of trust level is 3
    // One mdd order is 1. State 0 -- sink node 0. State 1 and 2 -- sink node 1
    // The other mdd order is 2. State 0 and 2-- sink node 0. State 1 -- sink node 1
    // Result mdd is order 1. State 0 -- sink node 0. State 1 and 2 -- the other mdd.
    // Check the commutativity for the operation.
    @Test
    public void shouldAndOperationWorkCorrectlyWhenApplyingToTwoMDDs1() {
        Relationship relationship = new Relationship(3, 0, 1, new double[]{0.1, 0.1, 0.8});
        relationship.setOrder(1);
        RelationshipNode rootNode = new RelationshipNode(relationship);
        RelationshipNode sinkNode0ForState0 = new RelationshipNode(0, rootNode);
        RelationshipNode sinkNode0ForState2 = new RelationshipNode(1, rootNode);
        RelationshipNode sinkNode1ForState1 = new RelationshipNode(1, rootNode);
        rootNode.putNextNode(0, sinkNode0ForState0);
        rootNode.putNextNode(1, sinkNode1ForState1);
        rootNode.putNextNode(2, sinkNode0ForState2);
        MDD mdd = new MDD(rootNode);

        Relationship otherRelationship = new Relationship(3, 1, 2, new double[]{0.1, 0.2, 0.7});
        otherRelationship.setOrder(2);
        RelationshipNode otherRootNode = new RelationshipNode(otherRelationship);
        RelationshipNode otherSinkNode0ForState0 = new RelationshipNode(0, otherRootNode);
        RelationshipNode otherSinkNode1ForState2 = new RelationshipNode(0, otherRootNode);
        RelationshipNode otherSinkNode1ForState1 = new RelationshipNode(1, otherRootNode);
        otherRootNode.putNextNode(0, otherSinkNode0ForState0);
        otherRootNode.putNextNode(1, otherSinkNode1ForState1);
        otherRootNode.putNextNode(2, otherSinkNode1ForState2);
        MDD otherMDD = new MDD(otherRootNode);

        MDD resultMDD = mdd.and(otherMDD);

        RelationshipNode resultRootNode = resultMDD.getRootNode();
        assertEquals(relationship, resultRootNode.getRelationship());
        assertEquals(sinkNode0ForState0, resultRootNode.getNextNode(0));
        assertEquals(otherRootNode, resultRootNode.getNextNode(1));
        assertEquals(otherRootNode, resultRootNode.getNextNode(2));
        checkParents(resultRootNode, 3);

    }

    // Number of trust level is 3
    // One mdd is the result from the first test
    // The other mdd is the result from the second test
    // Result mdd is order 1. State 0 -- sink node 0. State 1 -- other mdd from test 1. State 2 -- other mdd from test 2
    // Check the commutativity for the operation.
    @Test
    public void shouldOrOperationWorkCorrectlyWhenApplyingToTwoMDDs() {

        Relationship relationship = new Relationship(3, 0, 1, new double[]{0.1, 0.1, 0.8});
        relationship.setOrder(1);
        RelationshipNode rootNode = new RelationshipNode(relationship);
        RelationshipNode sinkNode0ForState0 = new RelationshipNode(0, rootNode);
        RelationshipNode sinkNode0ForState2 = new RelationshipNode(0, rootNode);
        RelationshipNode sinkNode1ForState1 = new RelationshipNode(1, rootNode);
        rootNode.putNextNode(0, sinkNode0ForState0);
        rootNode.putNextNode(1, sinkNode1ForState1);
        rootNode.putNextNode(2, sinkNode0ForState2);
        MDD mdd = new MDD(rootNode);

        Relationship otherRelationship = new Relationship(3, 1, 2, new double[]{0.1, 0.2, 0.7});
        otherRelationship.setOrder(2);
        RelationshipNode otherRootNode = new RelationshipNode(otherRelationship);
        RelationshipNode otherSinkNode0ForState0 = new RelationshipNode(0, otherRootNode);
        RelationshipNode otherSinkNode1ForState2 = new RelationshipNode(1, otherRootNode);
        RelationshipNode otherSinkNode1ForState1 = new RelationshipNode(1, otherRootNode);
        otherRootNode.putNextNode(0, otherSinkNode0ForState0);
        otherRootNode.putNextNode(1, otherSinkNode1ForState1);
        otherRootNode.putNextNode(2, otherSinkNode1ForState2);
        MDD otherMDD = new MDD(otherRootNode);
        MDD resultMDD = mdd.and(otherMDD);

        Relationship relationship1 = new Relationship(3, 0, 1, new double[]{0.1, 0.1, 0.8});
        relationship1.setOrder(1);
        RelationshipNode rootNode1 = new RelationshipNode(relationship1);
        RelationshipNode sinkNode0ForState01 = new RelationshipNode(0, rootNode1);
        RelationshipNode sinkNode0ForState21 = new RelationshipNode(1, rootNode1);
        RelationshipNode sinkNode1ForState11 = new RelationshipNode(1, rootNode1);
        rootNode1.putNextNode(0, sinkNode0ForState01);
        rootNode1.putNextNode(1, sinkNode1ForState11);
        rootNode1.putNextNode(2, sinkNode0ForState21);
        MDD mdd1 = new MDD(rootNode1);

        Relationship otherRelationship1 = new Relationship(3, 1, 2, new double[]{0.1, 0.2, 0.7});
        otherRelationship1.setOrder(2);
        RelationshipNode otherRootNode1 = new RelationshipNode(otherRelationship1);
        RelationshipNode otherSinkNode0ForState01 = new RelationshipNode(0, otherRootNode1);
        RelationshipNode otherSinkNode1ForState21 = new RelationshipNode(0, otherRootNode1);
        RelationshipNode otherSinkNode1ForState11 = new RelationshipNode(1, otherRootNode1);
        otherRootNode1.putNextNode(0, otherSinkNode0ForState01);
        otherRootNode1.putNextNode(1, otherSinkNode1ForState11);
        otherRootNode1.putNextNode(2, otherSinkNode1ForState21);
        MDD otherMDD1 = new MDD(otherRootNode1);
        MDD resultMDD1 = mdd1.and(otherMDD1);

        MDD orResultMDD = resultMDD1.or(resultMDD);

        RelationshipNode resultNodeForState1 = new RelationshipNode(otherRelationship1);
        resultNodeForState1.putNextNode(0, new RelationshipNode(0));
        resultNodeForState1.putNextNode(1, new RelationshipNode(1));
        resultNodeForState1.putNextNode(2, new RelationshipNode(1));

        RelationshipNode resultNodeForState2 = new RelationshipNode(otherRelationship1);
        resultNodeForState2.putNextNode(0, new RelationshipNode(0));
        resultNodeForState2.putNextNode(1, new RelationshipNode(1));
        resultNodeForState2.putNextNode(2, new RelationshipNode(0));

        RelationshipNode resultRootNode = orResultMDD.getRootNode();
        assertEquals(relationship, resultRootNode.getRelationship());
        assertEquals(sinkNode0ForState0, resultRootNode.getNextNode(0));
        assertEquals(resultNodeForState1, resultRootNode.getNextNode(1));
        assertEquals(resultNodeForState2, resultRootNode.getNextNode(2));
        checkParents(resultRootNode, 3);
    }

    // Number of trust level is 2
    // One mdd order is 1, state 0 is sink node 0, state 1 is mdd with order 2. This mdd's state 1 is sink node 1, state 0 is mdd with order 3.
    // Order 3 mdd has state 0 as sink node 0, state 1 as sink node 1.
    // The other mdd order is 4, state 0 is sink node 0 and state 1 is sink node 1
    // Result mdd is order 1. state 0 is other mdd and the state 1 is the same, besides the end state 0 links to the other mdd.
    // Check the commutativity for the operation.
    @Test
    public void shouldOrOperationWorkCorrectlyWhenApplyingToTwoMDDs1() {
        Relationship relationship = new Relationship(2, 0, 1, new double[]{0.1, 0.9});
        relationship.setOrder(1);
        RelationshipNode rootNode = new RelationshipNode(relationship);

        Relationship relationshipWithOrder2 = new Relationship(2, 1, 2, new double[]{0.1, 0.9});
        relationshipWithOrder2.setOrder(2);
        RelationshipNode relationshipNodeWithOrder2 = new RelationshipNode(relationshipWithOrder2);


        Relationship relationshipWithOrder3 = new Relationship(2, 0, 2, new double[]{0.1, 0.9});
        relationshipWithOrder3.setOrder(3);
        RelationshipNode relationshipNodeWithOrder3 = new RelationshipNode(relationshipWithOrder3);

        Relationship relationshipWithOrder4 = new Relationship(2, 3, 2, new double[]{0.1, 0.9});
        relationshipWithOrder4.setOrder(4);
        RelationshipNode relationshipNodeWithOrder4 = new RelationshipNode(relationshipWithOrder4);

        rootNode.putNextNode(0, new RelationshipNode(0, rootNode));
        rootNode.putNextNode(1, relationshipNodeWithOrder2);

        relationshipNodeWithOrder2.setParent(rootNode);
        relationshipNodeWithOrder2.putNextNode(0, relationshipNodeWithOrder3);
        relationshipNodeWithOrder2.putNextNode(1, new RelationshipNode(1, relationshipNodeWithOrder2));

        relationshipNodeWithOrder3.setParent(relationshipNodeWithOrder2);
        relationshipNodeWithOrder3.putNextNode(0, new RelationshipNode(0, relationshipNodeWithOrder3));
        relationshipNodeWithOrder3.putNextNode(1, new RelationshipNode(1, relationshipNodeWithOrder3));

        relationshipNodeWithOrder4.putNextNode(0, new RelationshipNode(0, relationshipNodeWithOrder4));
        relationshipNodeWithOrder4.putNextNode(1, new RelationshipNode(1, relationshipNodeWithOrder4));

        MDD mdd = new MDD(rootNode);

        MDD otherMDD = new MDD(relationshipNodeWithOrder4);
        MDD resultMDD = mdd.or(otherMDD);

        RelationshipNode resultMDDRootNode = resultMDD.getRootNode();

        RelationshipNode resultNode1 = new RelationshipNode(relationshipWithOrder2);
        RelationshipNode resultNode2 = new RelationshipNode(relationshipWithOrder3);
        RelationshipNode resultNode3 = new RelationshipNode(relationshipWithOrder4);

        resultNode1.putNextNode(1, new RelationshipNode(1));
        resultNode1.putNextNode(0, resultNode2);

        resultNode2.putNextNode(1, new RelationshipNode(1));
        resultNode2.putNextNode(0, resultNode3);

        resultNode3.putNextNode(1, new RelationshipNode(1));
        resultNode3.putNextNode(0, new RelationshipNode(0));

        assertEquals(relationship, resultMDDRootNode.getRelationship());
        assertEquals(resultNode3, resultMDDRootNode.getNextNode(0));
        assertEquals(resultNode1, resultMDDRootNode.getNextNode(1));
        checkParents(resultMDDRootNode, 2);
    }

    //The and or or operation should return null if the other mdd is null.
    @Test
    public void shouldReturnThisMDDWhenOtherMDDIsNull() {
        Relationship relationship = new Relationship(2, 0, 1, new double[]{0.1, 0.9});
        RelationshipNode relationshipNode = new RelationshipNode(relationship);
        MDD mdd = new MDD(relationshipNode);
        assertSame(mdd, mdd.and(null));
        assertSame(mdd, mdd.or(null));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNonePointerWhenPassNullToCreateMDD() {
        MDD mdd = new MDD(null);
    }


}