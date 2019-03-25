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

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest
public class InitializeMDDCreationTest {


    @Autowired
    private TrustRelationDao trustRelationDao;


    @Autowired
    private PersonDao personDao;


    @Before
    public void addPeopleAndRelationshipToDB() {
        Person grey = new Person("Grey");
        Person roy = new Person("Roy");
        Person craig = new Person("Craig");
        Person mike = new Person("Mike");

        personDao.save(grey);
        personDao.save(roy);
        personDao.save(craig);
        personDao.save(mike);

        TrustRelation trustRelation1 = new TrustRelation(grey, roy, 1, 0.5);
        TrustRelation trustRelation2 = new TrustRelation(grey, roy, 2, 0.2);
        TrustRelation trustRelation3 = new TrustRelation(grey, roy, 0, 0.3);
        TrustRelation trustRelation4 = new TrustRelation(roy, grey, 2, 1);
        TrustRelation trustRelation5 = new TrustRelation(roy, grey, 1, 0);
        TrustRelation trustRelation6 = new TrustRelation(roy, grey, 0, 0);
        TrustRelation trustRelation7 = new TrustRelation(grey, craig, 1, 0.8);
        TrustRelation trustRelation8 = new TrustRelation(grey, craig, 0, 0.2);
        TrustRelation trustRelation9 = new TrustRelation(grey, craig, 2, 0);
        TrustRelation trustRelation10 = new TrustRelation(craig, roy, 2, 0.2);
        TrustRelation trustRelation11 = new TrustRelation(craig, roy, 1, 0.6);
        TrustRelation trustRelation12 = new TrustRelation(craig, roy, 0, 0.2);
        TrustRelation trustRelation13 = new TrustRelation(roy, mike, 2, 0.1);
        TrustRelation trustRelation14 = new TrustRelation(roy, mike, 1, 0.8);
        TrustRelation trustRelation15 = new TrustRelation(roy, mike, 0, 0.1);
        TrustRelation trustRelation16 = new TrustRelation(craig, mike, 2, 0.2);
        TrustRelation trustRelation17 = new TrustRelation(craig, mike, 1, 0.2);
        TrustRelation trustRelation18 = new TrustRelation(craig, mike, 0, 0.6);
        TrustRelation trustRelation19 = new TrustRelation(mike, roy, 2, 0.1);
        TrustRelation trustRelation20 = new TrustRelation(mike, roy, 1, 0.8);
        TrustRelation trustRelation21 = new TrustRelation(mike, roy, 0, 0.1);

        trustRelationDao.save(trustRelation1);
        trustRelationDao.save(trustRelation2);
        trustRelationDao.save(trustRelation3);
        trustRelationDao.save(trustRelation4);
        trustRelationDao.save(trustRelation5);
        trustRelationDao.save(trustRelation6);
        trustRelationDao.save(trustRelation7);
        trustRelationDao.save(trustRelation8);
        trustRelationDao.save(trustRelation9);
        trustRelationDao.save(trustRelation10);
        trustRelationDao.save(trustRelation11);
        trustRelationDao.save(trustRelation12);
        trustRelationDao.save(trustRelation13);
        trustRelationDao.save(trustRelation14);
        trustRelationDao.save(trustRelation15);
        trustRelationDao.save(trustRelation16);
        trustRelationDao.save(trustRelation17);
        trustRelationDao.save(trustRelation18);
        trustRelationDao.save(trustRelation19);
        trustRelationDao.save(trustRelation20);
        trustRelationDao.save(trustRelation21);
    }

    @After
    public void removeAllFromDB() {
        personDao.deleteAll();
        trustRelationDao.deleteAll();
    }

    // Check if grey trust level is correct in the relationship graph from reading the database
    // All the other should be null
    @Test
    public void shouldSocialNetworkGraphBeCreatedSuccessfullyFromDB() {
        MDDCreation mddCreation = new MDDCreation(CommonTestConstant.NUMBER_OF_TRUST_LEVEL);
        Relationship[][] socialNetworkGraph = mddCreation.getSocialNetworkGraph();
        Map<Long, Integer> personIdToGraphIndex = mddCreation.getPersonIdToGraphIndex();
        assertEquals(4, socialNetworkGraph[0].length);
        assertEquals(4, personIdToGraphIndex.size());
        Person grey = ((List<Person>)personDao.findPeopleByName("Grey")).get(0);
        Person roy = ((List<Person>)personDao.findPeopleByName("Roy")).get(0);
        Person craig = ((List<Person>)personDao.findPeopleByName("Craig")).get(0);
        Person mike = ((List<Person>)personDao.findPeopleByName("Mike")).get(0);

        int indexOfRoy = personIdToGraphIndex.get(roy.getId());
        int indexOfGrey = personIdToGraphIndex.get(grey.getId());
        int indexOfCraig = personIdToGraphIndex.get(craig.getId());
        int indexOfMike = personIdToGraphIndex.get(mike.getId());

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

        assertEquals(null, socialNetworkGraph[indexOfRoy][indexOfRoy]);
        assertEquals(null, socialNetworkGraph[indexOfCraig][indexOfCraig]);
        assertEquals(null, socialNetworkGraph[indexOfGrey][indexOfGrey]);
        assertEquals(null, socialNetworkGraph[indexOfMike][indexOfMike]);
        assertEquals(null, socialNetworkGraph[indexOfMike][indexOfCraig]);
        assertEquals(null, socialNetworkGraph[indexOfMike][indexOfGrey]);
        assertEquals(null, socialNetworkGraph[indexOfGrey][indexOfMike]);
        assertEquals(null, socialNetworkGraph[indexOfCraig][indexOfGrey]);
        assertEquals(null, socialNetworkGraph[indexOfRoy][indexOfCraig]);

        assertEquals(relationshipFromMikeToRoy, socialNetworkGraph[indexOfMike][indexOfRoy]);
        assertEquals(relationshipFromRoyToGrey, socialNetworkGraph[indexOfRoy][indexOfGrey]);
        assertEquals(relationshipFromGreyToRoy, socialNetworkGraph[indexOfGrey][indexOfRoy]);
        assertEquals(relationshipFromGreyToCraig, socialNetworkGraph[indexOfGrey][indexOfCraig]);
        assertEquals(relationshipFromCraigToRoy, socialNetworkGraph[indexOfCraig][indexOfRoy]);
        assertEquals(relationshipFromCraigToMike, socialNetworkGraph[indexOfCraig][indexOfMike]);
        assertEquals(relationshipFromRoyToMike, socialNetworkGraph[indexOfRoy][indexOfMike]);

    }
}
