package com.mdd.algorithm;

import com.mdd.common.CommonTestConstant;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static com.mdd.common.CommonConstant.NO_ROOT_NODE;
import static com.mdd.common.CommonConstant.NO_SUCH_RELATIONSHIP;
import static junit.framework.TestCase.assertEquals;

public class MDDEvaluationTest {

    @Test
    public void shouldGettingProbabilityFromSinkNodeMDDFail() {
        MDD mdd = new MDD(new RelationshipNode(1));
        MDDEvaluation mddEvaluation = new MDDEvaluation();
        assertEquals(NO_ROOT_NODE, mddEvaluation.getProbability(mdd));
    }

    @Test
    public void shouldGetProbabilityFromOneLayerMDDWith3TrustLevelCorrectly() {
        Relationship relationship = new Relationship(3, 0, 1,
                new double[] {0.1, 0.8, 0.1});
        RelationshipNode relationshipNode = new RelationshipNode(relationship);
        relationshipNode.putNextNode(0, new RelationshipNode(0));
        relationshipNode.putNextNode(1, new RelationshipNode(1));
        relationshipNode.putNextNode(2, new RelationshipNode(1));
        MDD mdd = new MDD(relationshipNode);
        MDDEvaluation mddEvaluation = new MDDEvaluation();
        assertEquals(0.9, mddEvaluation.getProbability(mdd));
    }

    @Test
    public void shouldGetProbabilityFromTwoLayerMDDWith3TrustLevelCorrectly() {
        Relationship relationship = new Relationship(3, 0, 1,
                new double[] {0.1, 0.8, 0.1});
        RelationshipNode relationshipNode = new RelationshipNode(relationship);
        Relationship relationship1 = new Relationship(3, 2, 1,
                new double[] {0.2, 0.8, 0});
        RelationshipNode layer2Node1 = new RelationshipNode(relationship1);
        layer2Node1.putNextNode(0, new RelationshipNode(0));
        layer2Node1.putNextNode(1, new RelationshipNode(1));
        layer2Node1.putNextNode(2, new RelationshipNode(1));
        RelationshipNode layer2Node2 = new RelationshipNode(relationship1);
        layer2Node2.putNextNode(0, new RelationshipNode(0));
        layer2Node2.putNextNode(1, new RelationshipNode(1));
        layer2Node2.putNextNode(2, new RelationshipNode(0));
        relationshipNode.putNextNode(0, new RelationshipNode(1));
        relationshipNode.putNextNode(1, layer2Node1);
        relationshipNode.putNextNode(2, layer2Node2);
        MDD mdd = new MDD(relationshipNode);
        MDDEvaluation mddEvaluation = new MDDEvaluation();
        assertEquals(0.1 + 0.8*0.8 + 0.1*0.8, mddEvaluation.getProbability(mdd));
    }

    @Test
    public void shouldGetProbabilityFromGivenModelCorrectly() {
        List<List<Relationship>> socialNetwork = new ArrayList<>(4);
        int indexOfGrey = 0;
        int indexOfRoy = 1;
        int indexOfCraig = 2;
        int indexOfMike = 3;
        socialNetwork.add(new ArrayList<>());
        socialNetwork.add(new ArrayList<>());
        socialNetwork.add(new ArrayList<>());
        socialNetwork.add(new ArrayList<>());

        Relationship relationshipFromGreyToRoy = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfGrey, indexOfRoy);

        Relationship relationshipFromGreyToCraig = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfGrey, indexOfCraig);

        Relationship relationshipFromCraigToRoy = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfCraig, indexOfRoy);

        Relationship relationshipFromRoyToMike = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfRoy, indexOfMike);

        relationshipFromGreyToRoy.setTrustProbability(0.1, 0);
        relationshipFromGreyToRoy.setTrustProbability(0.5, 1);
        relationshipFromGreyToRoy.setTrustProbability(0.4, 2);

        relationshipFromGreyToCraig.setTrustProbability(0, 0);
        relationshipFromGreyToCraig.setTrustProbability(0.6, 1);
        relationshipFromGreyToCraig.setTrustProbability(0.4, 2);

        relationshipFromCraigToRoy.setTrustProbability(0, 0);
        relationshipFromCraigToRoy.setTrustProbability(0, 1);
        relationshipFromCraigToRoy.setTrustProbability(1, 2);

        relationshipFromRoyToMike.setTrustProbability(0.2, 0);
        relationshipFromRoyToMike.setTrustProbability(0.5, 1);
        relationshipFromRoyToMike.setTrustProbability(0.3, 2);


        socialNetwork.get(indexOfGrey).add(relationshipFromGreyToRoy);
        socialNetwork.get(indexOfGrey).add(relationshipFromGreyToCraig);
        socialNetwork.get(indexOfCraig).add(relationshipFromCraigToRoy);
        socialNetwork.get(indexOfRoy).add(relationshipFromRoyToMike);

        MDDCreation mddCreation = new MDDCreation(socialNetwork);
        Map<Long, Integer> map = new HashMap<>();
        map.put((long)0, 0);
        map.put((long)1, 1);
        map.put((long)2, 2);
        map.put((long)3, 3);
        mddCreation.setPersonIdToGraphIndex(map);
        MDD mddAtTrustLevel0 = mddCreation.createMDD(indexOfGrey, indexOfMike, 0);
        MDD mddAtTrustLevel1 = mddCreation.createMDD(indexOfGrey, indexOfMike, 1);
        MDD mddAtTrustLevel2 = mddCreation.createMDD(indexOfGrey, indexOfMike, 2);
        MDDEvaluation mddEvaluation = new MDDEvaluation();
        assertEquals(0.2, mddEvaluation.getProbability(mddAtTrustLevel0), 0.00001);
        assertEquals(0.608, mddEvaluation.getProbability(mddAtTrustLevel1), 0.00001);
        assertEquals(0.192, mddEvaluation.getProbability(mddAtTrustLevel2), 0.00001);
    }

    @Test
    public void shouldNotGetSensitivityWhenRootNodeDoesNotExist() {
        MDD mdd = new MDD(new RelationshipNode(1));
        MDDEvaluation mddEvaluation = new MDDEvaluation();
        assertEquals(NO_ROOT_NODE, mddEvaluation.getSensitivityOfNode(mdd, 0, 1));
    }

    @Test
    public void shouldNotGetSensitivityWhenRelationshipDoesNotExist() {
        Relationship relationship = new Relationship(3, 0, 1,
                new double[] {0.1, 0.8, 0.1});
        RelationshipNode relationshipNode = new RelationshipNode(relationship);
        relationshipNode.putNextNode(0, new RelationshipNode(0));
        relationshipNode.putNextNode(1, new RelationshipNode(1));
        relationshipNode.putNextNode(2, new RelationshipNode(1));
        MDD mdd = new MDD(relationshipNode);
        MDDEvaluation mddEvaluation = new MDDEvaluation();
        assertEquals(NO_SUCH_RELATIONSHIP, mddEvaluation.getSensitivityOfNode(mdd, 0, 2));
    }

    @Test
    public void shouldGetSensitivityCorrectlyFromOneLayerMDD() {
        Relationship relationship = new Relationship(3, 0, 1,
                new double[] {0.1, 0.8, 0.1});
        RelationshipNode relationshipNode = new RelationshipNode(relationship);
        relationshipNode.putNextNode(0, new RelationshipNode(0));
        relationshipNode.putNextNode(1, new RelationshipNode(1));
        relationshipNode.putNextNode(2, new RelationshipNode(1));
        MDD mdd = new MDD(relationshipNode);
        MDDEvaluation mddEvaluation = new MDDEvaluation();
        assertEquals(((0.9 - 0) + (1 - 0.9) + (1 - 0.9)) / 2, mddEvaluation.getSensitivityOfNode(mdd, 0, 1));
    }

    @Test
    public void shouldGetSensitivityCorrectlyFromTwoLayerMDDCorrectly() {
        Relationship relationship = new Relationship(3, 0, 1,
                new double[] {0.1, 0.8, 0.1});
        RelationshipNode relationshipNode = new RelationshipNode(relationship);
        Relationship relationship1 = new Relationship(3, 2, 1,
                new double[] {0.2, 0.8, 0});
        RelationshipNode layer2Node1 = new RelationshipNode(relationship1);
        layer2Node1.putNextNode(0, new RelationshipNode(0));
        layer2Node1.putNextNode(1, new RelationshipNode(1));
        layer2Node1.putNextNode(2, new RelationshipNode(1));
        RelationshipNode layer2Node2 = new RelationshipNode(relationship1);
        layer2Node2.putNextNode(0, new RelationshipNode(0));
        layer2Node2.putNextNode(1, new RelationshipNode(1));
        layer2Node2.putNextNode(2, new RelationshipNode(0));
        relationshipNode.putNextNode(0, new RelationshipNode(1));
        relationshipNode.putNextNode(1, layer2Node1);
        relationshipNode.putNextNode(2, layer2Node2);
        MDD mdd = new MDD(relationshipNode);
        MDDEvaluation mddEvaluation = new MDDEvaluation();
        double probability = 0.1 + 0.8*0.8 + 0.1*0.8;
        assertEquals((Math.abs(probability - 1) + Math.abs(probability - 0.8) + Math.abs(probability - 0.8)) / 2,
                mddEvaluation.getSensitivityOfNode(mdd, 0, 1));
        assertEquals((Math.abs(probability - 0.1) + Math.abs(probability - 0.1 - 0.8*1 - 0.1*1) +
                        Math.abs(probability - 0.1)) / 2,
                mddEvaluation.getSensitivityOfNode(mdd, 2, 1));
    }

    @Test
    public void shouldGetSensitivityFromGivenModelCorrectly() {
        List<List<Relationship>> socialNetwork = new ArrayList<>(4);
        int indexOfGrey = 0;
        int indexOfRoy = 1;
        int indexOfCraig = 2;
        int indexOfMike = 3;
        socialNetwork.add(new ArrayList<>());
        socialNetwork.add(new ArrayList<>());
        socialNetwork.add(new ArrayList<>());
        socialNetwork.add(new ArrayList<>());
        Relationship relationshipFromGreyToRoy = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfGrey, indexOfRoy);

        Relationship relationshipFromGreyToCraig = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfGrey, indexOfCraig);

        Relationship relationshipFromCraigToRoy = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfCraig, indexOfRoy);

        Relationship relationshipFromRoyToMike = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfRoy, indexOfMike);

        relationshipFromGreyToRoy.setTrustProbability(0.1, 0);
        relationshipFromGreyToRoy.setTrustProbability(0.5, 1);
        relationshipFromGreyToRoy.setTrustProbability(0.4, 2);

        relationshipFromRoyToMike.setTrustProbability(0.2, 0);
        relationshipFromRoyToMike.setTrustProbability(0.5, 1);
        relationshipFromRoyToMike.setTrustProbability(0.3, 2);

        relationshipFromGreyToCraig.setTrustProbability(0, 0);
        relationshipFromGreyToCraig.setTrustProbability(0.6, 1);
        relationshipFromGreyToCraig.setTrustProbability(0.4, 2);

        relationshipFromCraigToRoy.setTrustProbability(0, 0);
        relationshipFromCraigToRoy.setTrustProbability(0, 1);
        relationshipFromCraigToRoy.setTrustProbability(1, 2);

        socialNetwork.get(indexOfGrey).add(relationshipFromGreyToRoy);
        socialNetwork.get(indexOfGrey).add(relationshipFromGreyToCraig);
        socialNetwork.get(indexOfCraig).add(relationshipFromCraigToRoy);
        socialNetwork.get(indexOfRoy).add(relationshipFromRoyToMike);
        MDDCreation mddCreation = new MDDCreation(socialNetwork);
        Map<Long, Integer> map = new HashMap<>();
        map.put((long)0, 0);
        map.put((long)1, 1);
        map.put((long)2, 2);
        map.put((long)3, 3);
        mddCreation.setPersonIdToGraphIndex(map);
        MDD mddAtTrustLevel0 = mddCreation.createMDD(indexOfGrey, indexOfMike, 0);
        MDD mddAtTrustLevel1 = mddCreation.createMDD(indexOfGrey, indexOfMike, 1);
        MDD mddAtTrustLevel2 = mddCreation.createMDD(indexOfGrey, indexOfMike, 2);
        MDDEvaluation mddEvaluation = new MDDEvaluation();
        assertEquals(0.0, mddEvaluation.getSensitivityOfNode(mddAtTrustLevel0, indexOfGrey, indexOfRoy), 0.00001);
        assertEquals(0.6, mddEvaluation.getSensitivityOfNode(mddAtTrustLevel0, indexOfRoy, indexOfMike), 0.00001);
        assertEquals(0.0, mddEvaluation.getSensitivityOfNode(mddAtTrustLevel0, indexOfGrey, indexOfCraig), 0.00001);
        assertEquals(0.0, mddEvaluation.getSensitivityOfNode(mddAtTrustLevel0, indexOfCraig, indexOfRoy), 0.00001);
        assertEquals(0.126, mddEvaluation.getSensitivityOfNode(mddAtTrustLevel1, indexOfGrey, indexOfRoy), 0.00001);
        assertEquals(0.624, mddEvaluation.getSensitivityOfNode(mddAtTrustLevel1, indexOfRoy, indexOfMike), 0.00001);
        assertEquals(0.169, mddEvaluation.getSensitivityOfNode(mddAtTrustLevel1, indexOfGrey, indexOfCraig), 0.00001);
        assertEquals(0.068, mddEvaluation.getSensitivityOfNode(mddAtTrustLevel1, indexOfCraig, indexOfRoy), 0.00001);
        assertEquals(0.126, mddEvaluation.getSensitivityOfNode(mddAtTrustLevel2, indexOfGrey, indexOfRoy), 0.00001);
        assertEquals(0.416, mddEvaluation.getSensitivityOfNode(mddAtTrustLevel2, indexOfRoy, indexOfMike), 0.00001);
        assertEquals(0.126, mddEvaluation.getSensitivityOfNode(mddAtTrustLevel2, indexOfGrey, indexOfCraig), 0.00001);
        assertEquals(0.072, mddEvaluation.getSensitivityOfNode(mddAtTrustLevel2, indexOfCraig, indexOfRoy), 0.00001);
    }

}
