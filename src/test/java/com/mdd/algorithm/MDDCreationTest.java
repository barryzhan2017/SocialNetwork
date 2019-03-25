package com.mdd.algorithm;

import com.mdd.common.CommonTestConstant;
import com.mdd.dao.PersonDao;
import com.mdd.dao.TrustRelationDao;
import com.mdd.entity.Person;
import com.mdd.entity.TrustRelation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static com.mdd.common.CommonConstant.NO_ORDERED;
import static org.junit.Assert.assertEquals;



public class MDDCreationTest {

    private int indexOfGrey = 0;
    private int indexOfRoy = 1;
    private int indexOfCraig = 2;
    private int indexOfMike = 3;
    private int numberOfPeople = 4;
    private int numberOfRelationship = 6;
    private MDDCreation mddCreation;

    //Initialize the 3 layer trust level social network graph.
    @Before
    public void initializeMDDCreation() {
        Relationship[][] socialNetworkGraph = new Relationship[4][4];
        Relationship relationshipFromGreyToRoy = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfGrey, indexOfRoy);
        relationshipFromGreyToRoy.setTrustProbability(0.3, 0);
        relationshipFromGreyToRoy.setTrustProbability(0.5, 1);
        relationshipFromGreyToRoy.setTrustProbability(0.2, 2);

        Relationship relationshipFromRoyToGrey = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfRoy, indexOfGrey);
        relationshipFromRoyToGrey.setTrustProbability(0, 0);
        relationshipFromRoyToGrey.setTrustProbability(0, 1);
        relationshipFromRoyToGrey.setTrustProbability(1, 2);

        Relationship relationshipFromGreyToCraig = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfGrey, indexOfCraig);
        relationshipFromGreyToCraig.setTrustProbability(0.2, 0);
        relationshipFromGreyToCraig.setTrustProbability(0.8, 1);
        relationshipFromGreyToCraig.setTrustProbability(0, 2);

        Relationship relationshipFromCraigToRoy = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfCraig, indexOfRoy);
        relationshipFromCraigToRoy.setTrustProbability(0.2, 0);
        relationshipFromCraigToRoy.setTrustProbability(0.6, 1);
        relationshipFromCraigToRoy.setTrustProbability(0.2, 2);

        Relationship relationshipFromCraigToMike = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfCraig, indexOfMike);
        relationshipFromCraigToMike.setTrustProbability(0.6, 0);
        relationshipFromCraigToMike.setTrustProbability(0.2, 1);
        relationshipFromCraigToMike.setTrustProbability(0.2, 2);

        Relationship relationshipFromRoyToMike = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfRoy, indexOfMike);
        relationshipFromRoyToMike.setTrustProbability(0.1, 0);
        relationshipFromRoyToMike.setTrustProbability(0.8, 1);
        relationshipFromRoyToMike.setTrustProbability(0.1, 2);

        Relationship relationshipFromMikeToRoy = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, indexOfMike, indexOfRoy);
        relationshipFromMikeToRoy.setTrustProbability(0.1, 0);
        relationshipFromMikeToRoy.setTrustProbability(0.8, 1);
        relationshipFromMikeToRoy.setTrustProbability(0.1, 2);

        socialNetworkGraph[indexOfRoy][indexOfRoy]= null;
        socialNetworkGraph[indexOfCraig][indexOfCraig]= null;
        socialNetworkGraph[indexOfGrey][indexOfGrey]= null;
        socialNetworkGraph[indexOfMike][indexOfMike]= null;
        socialNetworkGraph[indexOfMike][indexOfCraig]= null;
        socialNetworkGraph[indexOfMike][indexOfGrey]= null;
        socialNetworkGraph[indexOfGrey][indexOfMike]= null;
        socialNetworkGraph[indexOfCraig][indexOfGrey]= null;
        socialNetworkGraph[indexOfRoy][indexOfCraig]= null;

        socialNetworkGraph[indexOfMike][indexOfRoy]= relationshipFromMikeToRoy;
        socialNetworkGraph[indexOfRoy][indexOfGrey] = relationshipFromRoyToGrey;
        socialNetworkGraph[indexOfGrey][indexOfRoy] = relationshipFromGreyToRoy;
        socialNetworkGraph[indexOfGrey][indexOfCraig] = relationshipFromGreyToCraig;
        socialNetworkGraph[indexOfCraig][indexOfRoy] = relationshipFromCraigToRoy;
        socialNetworkGraph[indexOfCraig][indexOfMike] = relationshipFromCraigToMike;
        socialNetworkGraph[indexOfRoy][indexOfMike] = relationshipFromRoyToMike;

        mddCreation = new MDDCreation(socialNetworkGraph);
    }


    //Construct the order from the source node Grey to Mike. The path should be build from smaller index
    //There should be 3 paths (Grey--Roy--Mike, Grey--Craig--Roy--Mike, Grey--Craig--Mike)
    @Test
    public void shouldOrderOfMDDBeSetCorrectlyWhenFindingPathFromRoyToCraig() {
        mddCreation.orderRelationship(indexOfGrey, indexOfMike);
        Relationship[][] relationships = mddCreation.getSocialNetworkGraph();

        assertEquals(NO_ORDERED, relationships[indexOfMike][indexOfRoy].getOrder());
        assertEquals(NO_ORDERED, relationships[indexOfRoy][indexOfGrey].getOrder());
        assertEquals(1, relationships[indexOfGrey][indexOfRoy].getOrder());
        assertEquals(3, relationships[indexOfGrey][indexOfCraig].getOrder());
        assertEquals(4, relationships[indexOfCraig][indexOfRoy].getOrder());
        assertEquals(5, relationships[indexOfCraig][indexOfMike].getOrder());
        assertEquals(2, relationships[indexOfRoy][indexOfMike].getOrder());
    }

    //Construct the order from the source node Grey to Roy. The path should be build from smaller index
    //It should avoid path with loop. There should be 3 paths (Grey--Roy, Grey--Craig--Roy, Grey--Craig--Mike--roy)
    @Test
    public void shouldOrderOfMDDBeSetCorrectlyWhenFindingPathFromGreyToRoy() {
        mddCreation.orderRelationship(indexOfGrey, indexOfRoy);
        Relationship[][] relationships = mddCreation.getSocialNetworkGraph();

        assertEquals(5, relationships[indexOfMike][indexOfRoy].getOrder());
        assertEquals(NO_ORDERED, relationships[indexOfRoy][indexOfGrey].getOrder());
        assertEquals(1, relationships[indexOfGrey][indexOfRoy].getOrder());
        assertEquals(2, relationships[indexOfGrey][indexOfCraig].getOrder());
        assertEquals(3, relationships[indexOfCraig][indexOfRoy].getOrder());
        assertEquals(4, relationships[indexOfCraig][indexOfMike].getOrder());
        assertEquals(NO_ORDERED, relationships[indexOfRoy][indexOfMike].getOrder());
    }

}


























