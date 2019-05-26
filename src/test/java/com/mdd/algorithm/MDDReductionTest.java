package com.mdd.algorithm;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.checkerframework.checker.units.qual.C;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class MDDReductionTest {

    @Test
    public void shouldLevelOrderTraversalWorkCorrectlyWhenThereIsJustOneNode() {
        Relationship relationship = new Relationship(3, 0, 1, new double[] {0.1, 0.1, 0.8});
        relationship.setOrder(1);
        RelationshipNode rootNode = new RelationshipNode(relationship);
        rootNode.putNextNode(0, new RelationshipNode(0, rootNode));
        rootNode.putNextNode(1, new RelationshipNode(0, rootNode));
        rootNode.putNextNode(2, new RelationshipNode(1, rootNode));
        MDDReduction mddReduction = new MDDReduction(3);
        Queue<RelationshipNode> queue = new LinkedList<>();
        queue.offer(rootNode);
        List<List<RelationshipNode>> relationshipNodeForEachOrder = new ArrayList<>();
        RelationshipNode commonSinkNode0 = new RelationshipNode(0);
        RelationshipNode commonSinkNode1 = new RelationshipNode(1);
        mddReduction.levelOrderTraversal(queue, relationshipNodeForEachOrder, commonSinkNode0, commonSinkNode1);
        assertEquals(1,  relationshipNodeForEachOrder.size());
        assertEquals(1,  relationshipNodeForEachOrder.get(0).size());
        assertEquals(rootNode, relationshipNodeForEachOrder.get(0).get(0));
        assertSame(relationshipNodeForEachOrder.get(0).get(0).getNextNode(0), commonSinkNode0);
        assertSame(relationshipNodeForEachOrder.get(0).get(0).getNextNode(1), commonSinkNode0);
        assertSame(relationshipNodeForEachOrder.get(0).get(0).getNextNode(2), commonSinkNode1);
    }

    //Root node is order 1. Two child nodes are order 2 and the other is order 4.
    @Test
    public void shouldLevelOrderTraversalWorkCorrectlyWhenThereAre4NodesToFormACompleteTree() {
        Relationship relationship = new Relationship(3, 0, 1, new double[] {0.1, 0.1, 0.8});
        relationship.setOrder(1);
        Relationship relationship2 = new Relationship(3, 1, 2, new double[] {0.1, 0.1, 0.8});
        relationship2.setOrder(2);
        RelationshipNode relationshipNode2 = new RelationshipNode(relationship2);
        RelationshipNode otherRelationshipNode2 = new RelationshipNode(relationship2);
        Relationship relationship3 = new Relationship(3, 0, 2, new double[] {0.1, 0.1, 0.8});
        relationship3.setOrder(4);
        RelationshipNode relationshipNode3 = new RelationshipNode(relationship3);
        RelationshipNode rootNode = new RelationshipNode(relationship);
        rootNode.putNextNode(0, relationshipNode2);
        rootNode.putNextNode(1, relationshipNode3);
        rootNode.putNextNode(2, otherRelationshipNode2);
        relationshipNode2.setParent(rootNode);
        relationshipNode3.setParent(rootNode);
        otherRelationshipNode2.setParent(rootNode);

        relationshipNode2.putNextNode(0, new RelationshipNode(0, relationshipNode2));
        relationshipNode2.putNextNode(1, new RelationshipNode(0, relationshipNode2));
        relationshipNode2.putNextNode(2, new RelationshipNode(1, relationshipNode2));

        otherRelationshipNode2.putNextNode(0, new RelationshipNode(0, otherRelationshipNode2));
        otherRelationshipNode2.putNextNode(1, new RelationshipNode(0, otherRelationshipNode2));
        otherRelationshipNode2.putNextNode(2, new RelationshipNode(1, otherRelationshipNode2));

        relationshipNode3.putNextNode(0, new RelationshipNode(0, relationshipNode3));
        relationshipNode3.putNextNode(1, new RelationshipNode(1, relationshipNode3));
        relationshipNode3.putNextNode(2, new RelationshipNode(1, relationshipNode3));

        MDDReduction mddReduction = new MDDReduction(3);
        Queue<RelationshipNode> queue = new LinkedList<>();
        queue.offer(rootNode);
        List<List<RelationshipNode>> relationshipNodeForEachOrder = new ArrayList<>();
        RelationshipNode commonSinkNode0 = new RelationshipNode(0);
        RelationshipNode commonSinkNode1 = new RelationshipNode(1);

        //Test if the corresponding nodes are in the specific queue and the common sink nodes are used.
        mddReduction.levelOrderTraversal(queue, relationshipNodeForEachOrder, commonSinkNode0, commonSinkNode1);
        assertEquals(4,  relationshipNodeForEachOrder.size());

        assertEquals(1,  relationshipNodeForEachOrder.get(0).size());
        assertEquals(rootNode, relationshipNodeForEachOrder.get(0).get(0));

        assertEquals(2,  relationshipNodeForEachOrder.get(1).size());
        assertEquals(relationshipNode2, relationshipNodeForEachOrder.get(1).get(0));
        assertEquals(otherRelationshipNode2, relationshipNodeForEachOrder.get(1).get(1));
        assertSame(relationshipNodeForEachOrder.get(1).get(0).getNextNode(0), commonSinkNode0);
        assertSame(relationshipNodeForEachOrder.get(1).get(0).getNextNode(1), commonSinkNode0);
        assertSame(relationshipNodeForEachOrder.get(1).get(0).getNextNode(2), commonSinkNode1);
        assertSame(relationshipNodeForEachOrder.get(1).get(1).getNextNode(0), commonSinkNode0);
        assertSame(relationshipNodeForEachOrder.get(1).get(1).getNextNode(1), commonSinkNode0);
        assertSame(relationshipNodeForEachOrder.get(1).get(1).getNextNode(2), commonSinkNode1);

        assertEquals(0,  relationshipNodeForEachOrder.get(2).size());

        assertEquals(1,  relationshipNodeForEachOrder.get(3).size());
        assertEquals(relationshipNode3, relationshipNodeForEachOrder.get(3).get(0));
        assertSame(relationshipNodeForEachOrder.get(3).get(0).getNextNode(0), commonSinkNode0);
        assertSame(relationshipNodeForEachOrder.get(3).get(0).getNextNode(1), commonSinkNode1);
        assertSame(relationshipNodeForEachOrder.get(3).get(0).getNextNode(2), commonSinkNode1);
    }

    //For a list of different nodes in order 2, check if they remain as before after applying mergeIsomorphic method
    @Test
    public void shouldMergeNoIsomorphicNodesForNoIsomorphicNodesMDD() {
        Relationship order1 = new Relationship(3, 0, 1, new double[]{0, 0, 1});
        order1.setOrder(1);
        RelationshipNode parent = new RelationshipNode(order1);
        Relationship order2 = new Relationship(3, 1, 2, new double[]{0, 0, 1});
        order2.setOrder(2);
        RelationshipNode child1 = new RelationshipNode(order2);
        child1.setParent(parent);
        child1.putNextNode(0, new RelationshipNode(0, child1));
        child1.putNextNode(1, new RelationshipNode(0, child1));
        child1.putNextNode(2, new RelationshipNode(1, child1));
        RelationshipNode child2 = new RelationshipNode(order2);
        child2.setParent(parent);
        child2.putNextNode(0, new RelationshipNode(0, child2));
        child2.putNextNode(1, new RelationshipNode(1, child2));
        child2.putNextNode(2, new RelationshipNode(1, child2));
        RelationshipNode child3 = new RelationshipNode(order2);
        child3.setParent(parent);
        child3.putNextNode(0, new RelationshipNode(1, child3));
        child3.putNextNode(1, new RelationshipNode(0, child3));
        child3.putNextNode(2, new RelationshipNode(1, child3));
        parent.putNextNode(0, child1);
        parent.putNextNode(1, child2);
        parent.putNextNode(2, child3);
        List<RelationshipNode> childNodes = Arrays.asList(child1, child2, child3);
        MDDReduction mddReduction = new MDDReduction(3);
        mddReduction.mergeIsomorphicChildNodes(childNodes, HashBiMap.create(3));
        assertSame(parent.getNextNode(0), child1);
        assertSame(parent.getNextNode(1), child2);
        assertSame(parent.getNextNode(2), child3);
    }

    //For a list of 2 same nodes in order 2, check if they merge after applying mergeIsomorphic method
    @Test
    public void shouldMergeTwoIsomorphicNodesForTwoIsomorphicNodesMDD() {
        Relationship order1 = new Relationship(3, 0, 1, new double[]{0, 0, 1});
        order1.setOrder(1);
        RelationshipNode parent = new RelationshipNode(order1);
        Relationship order2 = new Relationship(3, 1, 2, new double[]{0, 0, 1});
        order2.setOrder(2);
        RelationshipNode child1 = new RelationshipNode(order2);
        child1.setParent(parent);
        child1.putNextNode(0, new RelationshipNode(0, child1));
        child1.putNextNode(1, new RelationshipNode(0, child1));
        child1.putNextNode(2, new RelationshipNode(1, child1));
        RelationshipNode child2 = new RelationshipNode(order2);
        child2.setParent(parent);
        child2.putNextNode(0, new RelationshipNode(0, child2));
        child2.putNextNode(1, new RelationshipNode(1, child2));
        child2.putNextNode(2, new RelationshipNode(1, child2));
        RelationshipNode child3 = new RelationshipNode(order2);
        child3.setParent(parent);
        child3.putNextNode(0, new RelationshipNode(0, child3));
        child3.putNextNode(1, new RelationshipNode(0, child3));
        child3.putNextNode(2, new RelationshipNode(1, child3));
        parent.putNextNode(0, child1);
        parent.putNextNode(1, child2);
        parent.putNextNode(2, child3);
        List<RelationshipNode> childNodes = Arrays.asList(child1, child2, child3);
        MDDReduction mddReduction = new MDDReduction(3);
        mddReduction.mergeIsomorphicChildNodes(childNodes, HashBiMap.create(3));
        assertSame(parent.getNextNode(0), child1);
        assertSame(parent.getNextNode(1), child2);
        assertSame(parent.getNextNode(2), child1);
    }

    //For a list of 3 same nodes in order 2, check if they merge after applying mergeIsomorphic method
    @Test
    public void shouldMerge3IsomorphicNodesFor3IsomorphicNodesMDD() {
        Relationship order1 = new Relationship(3, 0, 1, new double[]{0, 0, 1});
        order1.setOrder(1);
        RelationshipNode parent = new RelationshipNode(order1);
        Relationship order2 = new Relationship(3, 1, 2, new double[]{0, 0, 1});
        order2.setOrder(2);
        RelationshipNode child1 = new RelationshipNode(order2);
        child1.setParent(parent);
        child1.putNextNode(0, new RelationshipNode(0, child1));
        child1.putNextNode(1, new RelationshipNode(0, child1));
        child1.putNextNode(2, new RelationshipNode(1, child1));
        RelationshipNode child2 = new RelationshipNode(order2);
        child2.setParent(parent);
        child2.putNextNode(0, new RelationshipNode(0, child2));
        child2.putNextNode(1, new RelationshipNode(0, child2));
        child2.putNextNode(2, new RelationshipNode(1, child2));
        RelationshipNode child3 = new RelationshipNode(order2);
        child3.setParent(parent);
        child3.putNextNode(0, new RelationshipNode(0, child3));
        child3.putNextNode(1, new RelationshipNode(0, child3));
        child3.putNextNode(2, new RelationshipNode(1, child3));
        parent.putNextNode(0, child1);
        parent.putNextNode(1, child2);
        parent.putNextNode(2, child3);
        List<RelationshipNode> childNodes = Arrays.asList(child1, child2, child3);
        MDDReduction mddReduction = new MDDReduction(3);
        mddReduction.mergeIsomorphicChildNodes(childNodes, HashBiMap.create(3));
        assertSame(parent.getNextNode(0), child1);
        assertSame(parent.getNextNode(1), child1);
        assertSame(parent.getNextNode(2), child1);
    }

    @Test
    public void shouldNotApplyShannonReductionWhenChildNodesAreAllSinkNodes() {
        Relationship order2 = new Relationship(3, 1, 2, new double[]{0, 0, 1});
        RelationshipNode parent = new RelationshipNode(order2);
        RelationshipNode sinkNode10 = new RelationshipNode(0, parent);
        RelationshipNode sinkNode20 = new RelationshipNode(0, parent);
        RelationshipNode sinkNode30 = new RelationshipNode(0, parent);
        parent.putNextNode(0, sinkNode10);
        parent.putNextNode(1, sinkNode20);
        parent.putNextNode(2, sinkNode30);
        MDDReduction mddReduction = new MDDReduction(3);
        mddReduction.applyShannonReductionToChildrenNodes(Arrays.asList(parent));
        assertSame(parent.getNextNode(0), sinkNode10);
        assertSame(parent.getNextNode(1), sinkNode20);
        assertSame(parent.getNextNode(2), sinkNode30);
        assertNull(sinkNode10.getTrustLevelToNextNode());
        assertNull(sinkNode20.getTrustLevelToNextNode());
        assertNull(sinkNode30.getTrustLevelToNextNode());
    }

    @Test
    public void shouldApplyShannonReductionWhenGrandchildNodesAreAllSameSinkNodes() {
        Relationship order1 = new Relationship(3, 2, 0, new double[]{0, 0, 1});
        order1.setOrder(1);
        RelationshipNode parent = new RelationshipNode(order1);
        Relationship order2 = new Relationship(3, 0, 1, new double[]{0, 0, 1});
        order2.setOrder(2);
        RelationshipNode child = new RelationshipNode(order2);
        child.setParent(parent);
        Relationship order3 = new Relationship(3, 1, 2, new double[]{0, 0, 1});
        order3.setOrder(3);
        RelationshipNode grandchild1 = new RelationshipNode(order3);
        grandchild1.setParent(child);
        grandchild1.putNextNode(0, new RelationshipNode(0, grandchild1));
        grandchild1.putNextNode(1, new RelationshipNode(0, grandchild1));
        grandchild1.putNextNode(2, new RelationshipNode(1, grandchild1));
        child.putNextNode(0, grandchild1);
        child.putNextNode(1, grandchild1);
        child.putNextNode(2, grandchild1);
        parent.putNextNode(0, child);
        parent.putNextNode(1, grandchild1);
        parent.putNextNode(2, grandchild1);
        MDDReduction mddReduction = new MDDReduction(3);
        mddReduction.applyShannonReductionToChildrenNodes(Arrays.asList(parent));
        assertSame(parent.getNextNode(0), grandchild1);
        assertSame(parent.getNextNode(1), grandchild1);
        assertSame(parent.getNextNode(2), grandchild1);
    }

    @Test
    public void shouldReduceWorkCorrectlyForTwoLevelMDDWith3TrustLevel() {
        Relationship order1 = new Relationship(3, 0, 1, new double[]{0, 0, 1});
        order1.setOrder(1);
        RelationshipNode parent = new RelationshipNode(order1);
        Relationship order2 = new Relationship(3, 1, 2, new double[]{0, 0, 1});
        order2.setOrder(2);
        RelationshipNode child1 = new RelationshipNode(0, parent);
        RelationshipNode child2 = new RelationshipNode(order2);
        child2.setParent(parent);
        child2.putNextNode(0, new RelationshipNode(0, child2));
        child2.putNextNode(1, new RelationshipNode(1, child2));
        child2.putNextNode(2, new RelationshipNode(1, child2));
        RelationshipNode child3 = new RelationshipNode(order2);
        child3.setParent(parent);
        child3.putNextNode(0, new RelationshipNode(0, child3));
        child3.putNextNode(1, new RelationshipNode(1, child3));
        child3.putNextNode(2, new RelationshipNode(0, child3));
        parent.putNextNode(0, child1);
        parent.putNextNode(1, child2);
        parent.putNextNode(2, child3);
        MDDReduction mddReduction = new MDDReduction(3);
        MDD mdd = new MDD(parent);
        RelationshipNode copiedRootNode = new RelationshipNode(0);
        parent.copyTo(copiedRootNode);
        mddReduction.reduce(mdd);
        RelationshipNode rootNode = mdd.getRootNode();
        RelationshipNode sinkNode0 = rootNode.getNextNode(0);
        RelationshipNode sinkNode1 = rootNode.getNextNode(1).getNextNode(1);
        assertEquals(copiedRootNode, rootNode);
        assertSame(rootNode.getNextNode(0), sinkNode0);
        assertSame(rootNode.getNextNode(1).getNextNode(0), sinkNode0);
        assertSame(rootNode.getNextNode(1).getNextNode(1), sinkNode1);
        assertSame(rootNode.getNextNode(1).getNextNode(2), sinkNode1);
        assertSame(rootNode.getNextNode(2).getNextNode(0), sinkNode0);
        assertSame(rootNode.getNextNode(2).getNextNode(1), sinkNode1);
        assertSame(rootNode.getNextNode(2).getNextNode(2), sinkNode0);
    }

    @Test
    public void shouldReduceWorkCorrectlyFor4LevelMDDWith2TrustLevel() {
        Relationship order1 = new Relationship(2, 0, 1, new double[]{0, 1});
        order1.setOrder(1);
        Relationship order2 = new Relationship(2, 1, 2, new double[]{1, 0});
        order2.setOrder(2);
        Relationship order3 = new Relationship(2, 2, 3, new double[]{0, 1});
        order3.setOrder(3);
        Relationship order4 = new Relationship(2, 1, 3, new double[]{0, 1});
        order4.setOrder(4);
        RelationshipNode a = new RelationshipNode(order1);
        RelationshipNode b = new RelationshipNode(order2);
        b.setParent(a);
        RelationshipNode c = new RelationshipNode(order3);
        c.setParent(b);
        RelationshipNode d1 = new RelationshipNode(order4);
        RelationshipNode d2 = new RelationshipNode(order4);
        d1.setParent(a);
        d2.setParent(c);
        a.putNextNode(0, d1);
        a.putNextNode(1, b);
        d1.putNextNode(0, new RelationshipNode(0, d1));
        d1.putNextNode(1, new RelationshipNode(1, d1));
        b.putNextNode(0, c);
        b.putNextNode(1, new RelationshipNode(1, b));
        c.putNextNode(0, d2);
        c.putNextNode(1, new RelationshipNode(1, c));
        d2.putNextNode(0, new RelationshipNode(0, d2));
        d2.putNextNode(1, new RelationshipNode(1, d2));
        MDD mdd = new MDD(a);
        RelationshipNode copiedRootNode = new RelationshipNode(0);
        a.copyTo(copiedRootNode);
        MDDReduction mddReduction = new MDDReduction(2);
        mddReduction.reduce(mdd);
        RelationshipNode rootNode = mdd.getRootNode();
        //Assert the structure is the same
        assertEquals(rootNode, copiedRootNode);
        //Assert the merged nodes are the same
        assertSame(d1, rootNode.getNextNode(1).getNextNode(0).getNextNode(0));
        assertSame(d1.getNextNode(1), c.getNextNode(1));
        assertSame(d1.getNextNode(1), b.getNextNode(1));
    }

    //This time, the root node should be reduced because it links two same child nodes.
    @Test
    public void shouldReduceWorkCorrectlyFor6LevelMDDWith2TrustLevel() {
        Relationship order1 = new Relationship(2, 0, 1, new double[]{0, 1});
        order1.setOrder(1);
        Relationship order2 = new Relationship(2, 1, 2, new double[]{1, 0});
        order2.setOrder(2);
        Relationship order3 = new Relationship(2, 2, 3, new double[]{0, 1});
        order3.setOrder(3);
        Relationship order4 = new Relationship(2, 1, 3, new double[]{0, 1});
        order4.setOrder(4);
        Relationship order5 = new Relationship(2, 1, 4, new double[]{0, 1});
        order5.setOrder(5);
        Relationship order6 = new Relationship(2, 3, 4, new double[]{0, 1});
        order6.setOrder(6);
        RelationshipNode a = new RelationshipNode(order1);
        RelationshipNode b = new RelationshipNode(order2);
        RelationshipNode c = new RelationshipNode(order3);
        RelationshipNode d = new RelationshipNode(order4);
        RelationshipNode e = new RelationshipNode(order5);
        RelationshipNode f = new RelationshipNode(order6);
        d.setParent(c);
        e.setParent(d);
        f.setParent(e);
        c.putNextNode(0, d);
        c.putNextNode(1, new RelationshipNode(1, c));
        d.putNextNode(0, e);
        d.putNextNode(1, new RelationshipNode(1, d));
        e.putNextNode(0, f);
        e.putNextNode(1, new RelationshipNode(1, e));
        f.putNextNode(0, new RelationshipNode(0, f));
        f.putNextNode(1, new RelationshipNode(1, f));
        RelationshipNode c1 = new RelationshipNode(0);
        RelationshipNode c2 = new RelationshipNode(0);
        RelationshipNode copiedC = new RelationshipNode(0);
        c.copyTo(c1);
        c.copyTo(c2);
        c.copyTo(copiedC);
        c.setParent(b);
        c1.setParent(b);
        c2.setParent(a);
        b.setParent(a);
        a.putNextNode(0, c2);
        a.putNextNode(1, b);
        b.putNextNode(0, c);
        b.putNextNode(1, c1);
        MDD mdd = new MDD(a);
        MDDReduction mddReduction = new MDDReduction(2);
        mddReduction.reduce(mdd);
        RelationshipNode rootNode = mdd.getRootNode();
        assertEquals(copiedC, rootNode);
        RelationshipNode nodeD = rootNode.getNextNode(0);
        RelationshipNode nodeE = nodeD.getNextNode(0);
        RelationshipNode nodeF = nodeE.getNextNode(0);
        assertSame(rootNode.getNextNode(1), nodeD.getNextNode(1));
        assertSame(rootNode.getNextNode(1), nodeE.getNextNode(1));
        assertSame(rootNode.getNextNode(1), nodeF.getNextNode(1));
    }

}
