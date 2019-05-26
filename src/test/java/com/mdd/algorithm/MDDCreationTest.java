package com.mdd.algorithm;

import com.mdd.common.CommonTestConstant;

import org.junit.Before;
import org.junit.Test;


import java.util.*;

import static com.mdd.common.CommonConstant.NO_ORDERED;
import static com.mdd.common.CommonTestConstant.checkParents;
import static org.junit.Assert.*;


public class MDDCreationTest {

    private int indexOfGrey = 0;
    private int indexOfRoy = 1;
    private int indexOfCraig = 2;
    private int indexOfMike = 3;
    private MDDCreation mddCreation;
    private List<List<Relationship>> socialNetwork;
    private Relationship relationshipFromGreyToRoy = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfGrey, indexOfRoy);

    private Relationship relationshipFromRoyToGrey = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfRoy, indexOfGrey);

    private Relationship relationshipFromGreyToCraig = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfGrey, indexOfCraig);

    private Relationship relationshipFromCraigToRoy = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfCraig, indexOfRoy);

    private Relationship relationshipFromCraigToMike = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfCraig, indexOfMike);

    private Relationship relationshipFromMikeToRoy = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfMike, indexOfRoy);

    private Relationship relationshipFromRoyToMike = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfRoy, indexOfMike);

    //Initialize the 3 layer trust level social network graph.
    @Before
    public void initializeMDDCreation() {
        socialNetwork = new ArrayList<>(4);
        socialNetwork.add(new ArrayList<>());
        socialNetwork.add(new ArrayList<>());
        socialNetwork.add(new ArrayList<>());
        socialNetwork.add(new ArrayList<>());

        relationshipFromGreyToRoy.setTrustProbability(0.3, 0);
        relationshipFromGreyToRoy.setTrustProbability(0.5, 1);
        relationshipFromGreyToRoy.setTrustProbability(0.2, 2);

        relationshipFromRoyToGrey.setTrustProbability(0, 0);
        relationshipFromRoyToGrey.setTrustProbability(0, 1);
        relationshipFromRoyToGrey.setTrustProbability(1, 2);

        relationshipFromGreyToCraig.setTrustProbability(0.2, 0);
        relationshipFromGreyToCraig.setTrustProbability(0.8, 1);
        relationshipFromGreyToCraig.setTrustProbability(0, 2);

        relationshipFromCraigToRoy.setTrustProbability(0.2, 0);
        relationshipFromCraigToRoy.setTrustProbability(0.6, 1);
        relationshipFromCraigToRoy.setTrustProbability(0.2, 2);

        relationshipFromCraigToMike.setTrustProbability(0.6, 0);
        relationshipFromCraigToMike.setTrustProbability(0.2, 1);
        relationshipFromCraigToMike.setTrustProbability(0.2, 2);

        relationshipFromRoyToMike.setTrustProbability(0.1, 0);
        relationshipFromRoyToMike.setTrustProbability(0.8, 1);
        relationshipFromRoyToMike.setTrustProbability(0.1, 2);

        relationshipFromMikeToRoy.setTrustProbability(0.1, 0);
        relationshipFromMikeToRoy.setTrustProbability(0.8, 1);
        relationshipFromMikeToRoy.setTrustProbability(0.1, 2);

        //Have to keep the order of adding to make the ordering deterministic
        socialNetwork.get(indexOfGrey).add(relationshipFromGreyToRoy);
        socialNetwork.get(indexOfGrey).add(relationshipFromGreyToCraig);
        socialNetwork.get(indexOfCraig).add(relationshipFromCraigToRoy);
        socialNetwork.get(indexOfCraig).add(relationshipFromCraigToMike);
        socialNetwork.get(indexOfMike).add(relationshipFromMikeToRoy);
        socialNetwork.get(indexOfRoy).add(relationshipFromRoyToGrey);
        socialNetwork.get(indexOfRoy).add(relationshipFromRoyToMike);


        mddCreation = new MDDCreation(socialNetwork);
        Map<Long, Integer> map = new HashMap<>();
        map.put((long)0, 0);
        map.put((long)1, 1);
        map.put((long)2, 2);
        map.put((long)3, 3);
        mddCreation.setPersonIdToGraphIndex(map);

    }


    //Construct the order from the source node Grey to Mike. The path should be build from smaller index
    //There should be 3 paths (Grey--Roy--Mike, Grey--Craig--Roy--Mike, Grey--Craig--Mike)
    @Test
    public void shouldOrderOfMDDBeSetCorrectlyWhenFindingPathFromRoyToCraig() {
        mddCreation.orderRelationship(indexOfGrey, indexOfMike);
        List<List<Relationship>> relationships = mddCreation.getSocialNetwork();
        //To Roy
        assertEquals(NO_ORDERED, relationships.get(indexOfMike).get(0).getOrder());
        //To Grey
        assertEquals(NO_ORDERED, relationships.get(indexOfRoy).get(0).getOrder());
        //To Roy
        assertEquals(1, relationships.get(indexOfGrey).get(0).getOrder());
        //To Craig
        assertEquals(3, relationships.get(indexOfGrey).get(1).getOrder());
        //To Roy
        assertEquals(4, relationships.get(indexOfCraig).get(0).getOrder());
        //To Mike
        assertEquals(5, relationships.get(indexOfCraig).get(1).getOrder());
        //To Mike
        assertEquals(2, relationships.get(indexOfRoy).get(1).getOrder());
    }

    //Construct the order from the source node Grey to Roy. The path should be build from smaller index
    //It should avoid path with loop. There should be 3 paths (Grey--Roy, Grey--Craig--Roy, Grey--Craig--Mike--roy)
    @Test
    public void shouldOrderOfMDDBeSetCorrectlyWhenFindingPathFromGreyToRoy() {
        mddCreation.orderRelationship(indexOfGrey, indexOfRoy);
        List<List<Relationship>> relationships = mddCreation.getSocialNetwork();
        //To Roy
        assertEquals(5, relationships.get(indexOfMike).get(0).getOrder());
        //To Grey
        assertEquals(NO_ORDERED, relationships.get(indexOfRoy).get(0).getOrder());
        //To Roy
        assertEquals(1, relationships.get(indexOfGrey).get(0).getOrder());
        //To Craig
        assertEquals(2, relationships.get(indexOfGrey).get(1).getOrder());
        //To Roy
        assertEquals(3, relationships.get(indexOfCraig).get(0).getOrder());
        //To Mike
        assertEquals(4, relationships.get(indexOfCraig).get(1).getOrder());
        //To Mike
        assertEquals(NO_ORDERED, relationships.get(indexOfRoy).get(1).getOrder());

    }

    //To create a mdd from path from gery to roy at level 1.
    //Check if the path is correct.
    @Test
    public void shouldMDDCreatedCorrectlyForOneNodePathAtTrustLevel1() {
        List<List<Relationship>> paths = mddCreation.orderRelationship(indexOfGrey, indexOfRoy);
        List<Relationship> pathFromGreyToRoy = paths.get(0);
        MDD mdd = mddCreation.createMDDForPathAtSomeTrustLevel(pathFromGreyToRoy, 1);
        RelationshipNode rootNode = mdd.getRootNode();
        assertEquals(pathFromGreyToRoy.get(0), rootNode.getRelationship());
        assertEquals(new RelationshipNode(0), rootNode.getNextNode(0));
        assertEquals(new RelationshipNode(1), rootNode.getNextNode(1));
        assertEquals(new RelationshipNode(0), rootNode.getNextNode(2));
        checkParents(rootNode, 3);
    }

    //To create a mdd from path from gery to roy to mike at level 0.
    //Check if the path is correct.
    @Test
    public void shouldMDDCreatedCorrectlyForPath0AtTrustLevel0() {
        List<List<Relationship>> paths = mddCreation.orderRelationship(indexOfGrey, indexOfMike);
        List<Relationship> pathFromGreyToRoyToMike = paths.get(0);
        MDD mdd = mddCreation.createMDDForPathAtSomeTrustLevel(pathFromGreyToRoyToMike, 0);
        RelationshipNode rootNode = mdd.getRootNode();
        assertEquals(pathFromGreyToRoyToMike.get(0), rootNode.getRelationship());
        RelationshipNode relationshipNode1 = new RelationshipNode(pathFromGreyToRoyToMike.get(1));
        relationshipNode1.putNextNode(0, new RelationshipNode(1));
        relationshipNode1.putNextNode(1, new RelationshipNode(1));
        relationshipNode1.putNextNode(2, new RelationshipNode(1));
        RelationshipNode relationshipNode2 = new RelationshipNode(pathFromGreyToRoyToMike.get(1));
        relationshipNode2.putNextNode(0, new RelationshipNode(1));
        relationshipNode2.putNextNode(1, new RelationshipNode(0));
        relationshipNode2.putNextNode(2, new RelationshipNode(0));
        assertEquals(relationshipNode1, rootNode.getNextNode(0));
        assertEquals(relationshipNode2, rootNode.getNextNode(1));
        assertEquals(relationshipNode2, rootNode.getNextNode(2));
        checkParents(rootNode, 3);
    }

    //To create a mdd from path from gery to roy to mike at level 1.
    //Check if the path is correct.
    @Test
    public void shouldMDDCreatedCorrectlyForPath0AtTrustLevel1() {
        List<List<Relationship>> paths = mddCreation.orderRelationship(indexOfGrey, indexOfMike);
        List<Relationship> pathFromGreyToRoyToMike = paths.get(0);
        MDD mdd = mddCreation.createMDDForPathAtSomeTrustLevel(pathFromGreyToRoyToMike, 1);
        RelationshipNode rootNode = mdd.getRootNode();
        assertEquals(pathFromGreyToRoyToMike.get(0), rootNode.getRelationship());
        RelationshipNode relationshipNode1 = new RelationshipNode(pathFromGreyToRoyToMike.get(1));
        relationshipNode1.putNextNode(0, new RelationshipNode(0));
        relationshipNode1.putNextNode(1, new RelationshipNode(1));
        relationshipNode1.putNextNode(2, new RelationshipNode(1));
        RelationshipNode relationshipNode2 = new RelationshipNode(pathFromGreyToRoyToMike.get(1));
        relationshipNode2.putNextNode(0, new RelationshipNode(0));
        relationshipNode2.putNextNode(1, new RelationshipNode(1));
        relationshipNode2.putNextNode(2, new RelationshipNode(0));
        assertEquals(new RelationshipNode(0), rootNode.getNextNode(0));
        assertEquals(relationshipNode1, rootNode.getNextNode(1));
        assertEquals(relationshipNode2, rootNode.getNextNode(2));
        checkParents(rootNode, 3);
    }

    //To create a mdd from path from gery to roy to mike at level 2.
    //Check if the path is correct.
    @Test
    public void shouldMDDCreatedCorrectlyForPath0AtTrustLevel2() {
        List<List<Relationship>> paths = mddCreation.orderRelationship(indexOfGrey, indexOfMike);
        List<Relationship> pathFromGreyToRoyToMike = paths.get(0);
        MDD mdd = mddCreation.createMDDForPathAtSomeTrustLevel(pathFromGreyToRoyToMike, 2);
        RelationshipNode rootNode = mdd.getRootNode();
        assertEquals(pathFromGreyToRoyToMike.get(0), rootNode.getRelationship());
        RelationshipNode relationshipNode1 = new RelationshipNode(pathFromGreyToRoyToMike.get(1));
        relationshipNode1.putNextNode(0, new RelationshipNode(0));
        relationshipNode1.putNextNode(1, new RelationshipNode(0));
        relationshipNode1.putNextNode(2, new RelationshipNode(1));
        assertEquals(new RelationshipNode(0), rootNode.getNextNode(0));
        assertEquals(new RelationshipNode(0), rootNode.getNextNode(1));
        assertEquals(relationshipNode1, rootNode.getNextNode(2));
        checkParents(rootNode, 3);
    }

    //To create a mdd from path from gery to craig to roy to mike at level 0.
    //Check if the path is correct.
    @Test
    public void shouldMDDCreatedCorrectlyForPath1AtTrustLevel0() {
        List<List<Relationship>> paths = mddCreation.orderRelationship(indexOfGrey, indexOfMike);
        List<Relationship> pathFromGreyToRoyToMike = paths.get(1);
        MDD mdd = mddCreation.createMDDForPathAtSomeTrustLevel(pathFromGreyToRoyToMike, 0);
        RelationshipNode rootNode = mdd.getRootNode();
        assertEquals(pathFromGreyToRoyToMike.get(2), rootNode.getRelationship());
        RelationshipNode relationshipNodeFor1 = new RelationshipNode(pathFromGreyToRoyToMike.get(1));
        relationshipNodeFor1.putNextNode(0, new RelationshipNode(1));
        relationshipNodeFor1.putNextNode(1, new RelationshipNode(1));
        relationshipNodeFor1.putNextNode(2, new RelationshipNode(1));
        RelationshipNode relationshipNode1 = new RelationshipNode(pathFromGreyToRoyToMike.get(0));
        relationshipNode1.putNextNode(0, relationshipNodeFor1);
        relationshipNode1.putNextNode(1, relationshipNodeFor1);
        relationshipNode1.putNextNode(2, relationshipNodeFor1);
        RelationshipNode relationshipNodeFor2 = new RelationshipNode(pathFromGreyToRoyToMike.get(1));
        relationshipNodeFor2.putNextNode(0, new RelationshipNode(1));
        relationshipNodeFor2.putNextNode(1, new RelationshipNode(0));
        relationshipNodeFor2.putNextNode(2, new RelationshipNode(0));
        RelationshipNode relationshipNode2 = new RelationshipNode(pathFromGreyToRoyToMike.get(0));
        relationshipNode2.putNextNode(0, relationshipNodeFor1);
        relationshipNode2.putNextNode(1, relationshipNodeFor2);
        relationshipNode2.putNextNode(2, relationshipNodeFor2);
        assertEquals(relationshipNode1, rootNode.getNextNode(0));
        assertEquals(relationshipNode2, rootNode.getNextNode(1));
        assertEquals(relationshipNode2, rootNode.getNextNode(2));
        checkParents(rootNode, 3);
    }

    @Test
    public void shouldMDDCreatedAsNullWhenThePathIsNull() {
        assertNull(mddCreation.createMDDForPathAtSomeTrustLevel(null, 0));
    }

    @Test
    public void shouldMDDCreatedAsNullWhenThePathIsEmpty() {
        assertNull(mddCreation.createMDDForPathAtSomeTrustLevel(new ArrayList<>(), 0));
    }

    @Test
    public void shouldCreateMDDForCraigToMikeAtTrustLevel1Correctly() {
        MDD mdd = mddCreation.createMDD(indexOfCraig, indexOfMike, 1);
        //Create mdd with same structure to verify
        RelationshipNode order1 = new RelationshipNode(relationshipFromCraigToRoy);
        relationshipFromCraigToRoy.setOrder(1);
        RelationshipNode firstOrder2 = new RelationshipNode(relationshipFromRoyToMike);
        relationshipFromRoyToMike.setOrder(2);
        RelationshipNode secondOrder2 = new RelationshipNode(relationshipFromRoyToMike);
        RelationshipNode firstOrder3 = new RelationshipNode(relationshipFromCraigToMike);
        relationshipFromCraigToMike.setOrder(3);
        RelationshipNode secondOrder3 = new RelationshipNode(relationshipFromCraigToMike);
        firstOrder3.putNextNode(0, new RelationshipNode(0));
        firstOrder3.putNextNode(1, new RelationshipNode(1));
        firstOrder3.putNextNode(2, new RelationshipNode(0));
        secondOrder3.putNextNode(0, new RelationshipNode(1));
        secondOrder3.putNextNode(1, new RelationshipNode(1));
        secondOrder3.putNextNode(2, new RelationshipNode(0));
        firstOrder2.putNextNode(0, firstOrder3);
        firstOrder2.putNextNode(1, secondOrder3);
        firstOrder2.putNextNode(2, secondOrder3);
        secondOrder2.putNextNode(0, firstOrder3);
        secondOrder2.putNextNode(1, secondOrder3);
        secondOrder2.putNextNode(2, new RelationshipNode(0));
        order1.putNextNode(0, firstOrder3);
        order1.putNextNode(1, firstOrder2);
        order1.putNextNode(2, secondOrder2);
        RelationshipNode rootNode = mdd.getRootNode();
        assertEquals(rootNode, order1);
        RelationshipNode childNode0 = rootNode.getNextNode(0);
        RelationshipNode childNode1 = rootNode.getNextNode(1);
        RelationshipNode childNode2 = rootNode.getNextNode(2);
        assertSame(childNode0, childNode1.getNextNode(0));
        assertSame(childNode0, childNode2.getNextNode(0));
        assertSame(childNode1.getNextNode(1), childNode1.getNextNode(2));
        assertSame(childNode1.getNextNode(1), childNode2.getNextNode(1));
        //assert sink node 0
        assertSame(childNode0.getNextNode(0), childNode0.getNextNode(2));
        assertSame(childNode0.getNextNode(0), childNode1.getNextNode(0).getNextNode(2));
        assertSame(childNode0.getNextNode(0), childNode2.getNextNode(2));
        //assert sink node 1
        assertSame(childNode0.getNextNode(1), childNode1.getNextNode(1).getNextNode(0));
        assertSame(childNode0.getNextNode(1), childNode1.getNextNode(1).getNextNode(1));
    }

    @Test
    public void shouldCreateMDDForGregToMikeAtTrustLevel0Correctly() {
        // Delete the trust relation from craig to mike
        // Disable this path to comply with the thesis
        socialNetwork.get(indexOfCraig).remove(1);
        MDDCreation mddCreation = new MDDCreation(socialNetwork);
        Map<Long, Integer> map = new HashMap<>();
        map.put((long)0, 0);
        map.put((long)1, 1);
        map.put((long)2, 2);
        map.put((long)3, 3);
        mddCreation.setPersonIdToGraphIndex(map);
        MDD mdd = mddCreation.createMDD(indexOfGrey, indexOfMike, 0);
        //Create mdd with same structure to verify
        RelationshipNode order1 = new RelationshipNode(relationshipFromGreyToRoy);
        relationshipFromGreyToRoy.setOrder(1);
        RelationshipNode firstOrder2 = new RelationshipNode(relationshipFromRoyToMike);
        relationshipFromRoyToMike.setOrder(2);
        RelationshipNode secondOrder2 = new RelationshipNode(relationshipFromRoyToMike);
        RelationshipNode order3 = new RelationshipNode(relationshipFromGreyToCraig);
        relationshipFromGreyToCraig.setOrder(3);
        RelationshipNode order4 = new RelationshipNode(relationshipFromCraigToRoy);
        relationshipFromCraigToRoy.setOrder(4);
        order4.putNextNode(0, new RelationshipNode(1));
        order4.putNextNode(1, new RelationshipNode(0));
        order4.putNextNode(2, new RelationshipNode(0));
        order3.putNextNode(0, new RelationshipNode(1));
        order3.putNextNode(1, order4);
        order3.putNextNode(2, order4);
        firstOrder2.putNextNode(0, new RelationshipNode(1));
        firstOrder2.putNextNode(1, order3);
        firstOrder2.putNextNode(2, order3);
        secondOrder2.putNextNode(0, new RelationshipNode(1));
        secondOrder2.putNextNode(1, new RelationshipNode(0));
        secondOrder2.putNextNode(2, new RelationshipNode(0));
        order1.putNextNode(0, firstOrder2);
        order1.putNextNode(1, secondOrder2);
        order1.putNextNode(2, secondOrder2);
        RelationshipNode rootNode = mdd.getRootNode();
        assertEquals(rootNode, order1);
        RelationshipNode childNode0 = rootNode.getNextNode(0);
        RelationshipNode childNode1 = rootNode.getNextNode(1);
        RelationshipNode childNode2 = rootNode.getNextNode(2);
        RelationshipNode order3Node = childNode0.getNextNode(1);
        assertSame(childNode1, childNode2);
        assertSame(order3Node, childNode0.getNextNode(2));
        assertSame(order3Node.getNextNode(1), order3Node.getNextNode(2));
        //assert sink node 0
        assertSame(order3Node.getNextNode(1).getNextNode(1), order3Node.getNextNode(1).getNextNode(2));
        assertSame(order3Node.getNextNode(1).getNextNode(1), childNode2.getNextNode(1));
        assertSame(order3Node.getNextNode(1).getNextNode(1), childNode2.getNextNode(2));
        //assert sink node 1
        assertSame(childNode0.getNextNode(0), order3Node.getNextNode(1).getNextNode(0));
        assertSame(childNode0.getNextNode(0), childNode2.getNextNode(0));
        assertSame(childNode0.getNextNode(0), order3Node.getNextNode(0));
    }

    @Test
    public void shouldCreateMDDForGregToMikeAtTrustLevel1Correctly() {
        // Delete the trust relation from craig to mike
        // Disable this path to comply with the thesis
        socialNetwork.get(indexOfCraig).remove(1);
        MDDCreation mddCreation = new MDDCreation(socialNetwork);
        Map<Long, Integer> map = new HashMap<>();
        map.put((long)0, 0);
        map.put((long)1, 1);
        map.put((long)2, 2);
        map.put((long)3, 3);
        mddCreation.setPersonIdToGraphIndex(map);
        MDD mdd = mddCreation.createMDD(indexOfGrey, indexOfMike, 1);
        //Create mdd with same structure to verify
        RelationshipNode order1 = new RelationshipNode(relationshipFromGreyToRoy);
        relationshipFromGreyToRoy.setOrder(1);
        RelationshipNode firstOrder2 = new RelationshipNode(relationshipFromRoyToMike);
        relationshipFromRoyToMike.setOrder(2);
        RelationshipNode secondOrder2 = new RelationshipNode(relationshipFromRoyToMike);
        RelationshipNode thirdOrder2 = new RelationshipNode(relationshipFromRoyToMike);
        RelationshipNode firstOrder3 = new RelationshipNode(relationshipFromGreyToCraig);
        relationshipFromGreyToCraig.setOrder(3);
        RelationshipNode secondOrder3 = new RelationshipNode(relationshipFromGreyToCraig);
        RelationshipNode thirdOrder3 = new RelationshipNode(relationshipFromGreyToCraig);
        RelationshipNode firstOrder4 = new RelationshipNode(relationshipFromCraigToRoy);
        relationshipFromCraigToRoy.setOrder(4);
        RelationshipNode secondOrder4 = new RelationshipNode(relationshipFromCraigToRoy);
        RelationshipNode thirdOrder4 = new RelationshipNode(relationshipFromCraigToRoy);
        firstOrder4.putNextNode(0, new RelationshipNode(0));
        firstOrder4.putNextNode(1, new RelationshipNode(1));
        firstOrder4.putNextNode(2, new RelationshipNode(1));
        secondOrder4.putNextNode(0, new RelationshipNode(0));
        secondOrder4.putNextNode(1, new RelationshipNode(1));
        secondOrder4.putNextNode(2, new RelationshipNode(0));
        thirdOrder4.putNextNode(0, new RelationshipNode(1));
        thirdOrder4.putNextNode(1, new RelationshipNode(1));
        thirdOrder4.putNextNode(2, new RelationshipNode(0));
        firstOrder3.putNextNode(0, new RelationshipNode(0));
        firstOrder3.putNextNode(1, firstOrder4);
        firstOrder3.putNextNode(2, firstOrder4);
        secondOrder3.putNextNode(0, new RelationshipNode(0));
        secondOrder3.putNextNode(1, firstOrder4);
        secondOrder3.putNextNode(2, secondOrder4);
        thirdOrder3.putNextNode(0, new RelationshipNode(1));
        thirdOrder3.putNextNode(1, new RelationshipNode(1));
        thirdOrder3.putNextNode(2, thirdOrder4);
        firstOrder2.putNextNode(0, new RelationshipNode(0));
        firstOrder2.putNextNode(1, firstOrder3);
        firstOrder2.putNextNode(2, secondOrder3);
        secondOrder2.putNextNode(0, new RelationshipNode(0));
        secondOrder2.putNextNode(1, new RelationshipNode(1));
        secondOrder2.putNextNode(2, thirdOrder3);
        thirdOrder2.putNextNode(0, new RelationshipNode(0));
        thirdOrder2.putNextNode(1, new RelationshipNode(1));
        thirdOrder2.putNextNode(2, new RelationshipNode(0));
        order1.putNextNode(0, firstOrder2);
        order1.putNextNode(1, secondOrder2);
        order1.putNextNode(2, thirdOrder2);
        RelationshipNode rootNode = mdd.getRootNode();
        assertEquals(rootNode, order1);
        RelationshipNode childNode0 = rootNode.getNextNode(0);
        assertSame(childNode0.getNextNode(1).getNextNode(2), childNode0.getNextNode(1).getNextNode(1));
        assertSame(childNode0.getNextNode(1).getNextNode(2), childNode0.getNextNode(2).getNextNode(1));
    }

    @Test
    public void shouldCreateMDDForGregToMikeAtTrustLevel2Correctly() {
        // Delete the trust relation from craig to mike
        // Disable this path to comply with the thesis
        socialNetwork.get(indexOfCraig).remove(1);
        MDDCreation mddCreation = new MDDCreation(socialNetwork);
        Map<Long, Integer> map = new HashMap<>();
        map.put((long)0, 0);
        map.put((long)1, 1);
        map.put((long)2, 2);
        map.put((long)3, 3);
        mddCreation.setPersonIdToGraphIndex(map);
        MDD mdd = mddCreation.createMDD(indexOfGrey, indexOfMike, 2);
        //Create mdd with same structure to verify
        RelationshipNode order1 = new RelationshipNode(relationshipFromGreyToRoy);
        relationshipFromGreyToRoy.setOrder(1);
        RelationshipNode firstOrder2 = new RelationshipNode(relationshipFromRoyToMike);
        relationshipFromRoyToMike.setOrder(2);
        RelationshipNode secondOrder2 = new RelationshipNode(relationshipFromRoyToMike);
        RelationshipNode order3 = new RelationshipNode(relationshipFromGreyToCraig);
        relationshipFromGreyToCraig.setOrder(3);
        RelationshipNode order4 = new RelationshipNode(relationshipFromCraigToRoy);
        relationshipFromCraigToRoy.setOrder(4);
        order4.putNextNode(0, new RelationshipNode(0));
        order4.putNextNode(1, new RelationshipNode(0));
        order4.putNextNode(2, new RelationshipNode(1));
        order3.putNextNode(0, new RelationshipNode(0));
        order3.putNextNode(1, new RelationshipNode(0));
        order3.putNextNode(2, order4);
        firstOrder2.putNextNode(0, new RelationshipNode(0));
        firstOrder2.putNextNode(1, new RelationshipNode(0));
        firstOrder2.putNextNode(2, order3);
        secondOrder2.putNextNode(0, new RelationshipNode(0));
        secondOrder2.putNextNode(1, new RelationshipNode(0));
        secondOrder2.putNextNode(2, new RelationshipNode(1));
        order1.putNextNode(0, firstOrder2);
        order1.putNextNode(1, firstOrder2);
        order1.putNextNode(2, secondOrder2);
        RelationshipNode rootNode = mdd.getRootNode();
        assertEquals(rootNode, order1);
        RelationshipNode childNode0 = rootNode.getNextNode(0);
        RelationshipNode childNode1 = rootNode.getNextNode(1);
        assertSame(childNode0, childNode1);
    }
}
