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
public class MDDCreationTest {

    @Autowired
    private TrustRelationDao trustRelationDao;


    @Autowired
    private PersonDao personDao;


    @Before
    public void addPeopleAndRelationshipToDB() {

        Person greg = new Person("Greg");
        Person roy = new Person("Roy");
        Person craig = new Person("Craig");

        personDao.save(greg);
        personDao.save(roy);
        personDao.save(craig);

        TrustRelation trustRelation1 = new TrustRelation(greg, roy, 1, 0.5);
        TrustRelation trustRelation2 = new TrustRelation(greg, roy, 2, 0.2);
        TrustRelation trustRelation3 = new TrustRelation(greg, roy, 0, 0.3);
        TrustRelation trustRelation4 = new TrustRelation(roy, greg, 2, 0.3);
        TrustRelation trustRelation5 = new TrustRelation(greg, craig, 1, 0.5);
        TrustRelation trustRelation6 = new TrustRelation(greg, craig, 0, 0.2);

        trustRelationDao.save(trustRelation1);
        trustRelationDao.save(trustRelation2);
        trustRelationDao.save(trustRelation3);
        trustRelationDao.save(trustRelation4);
        trustRelationDao.save(trustRelation5);
        trustRelationDao.save(trustRelation6);
    }

    @After
    public void removeAllFromDB() {
        personDao.deleteAll();
        trustRelationDao.deleteAll();
    }

    @Test
    public void shouldSocialNetworkGraphBeCreatedSuccessfullyFromDB() {
        MDDCreation mddCreation = new MDDCreation(CommonTestConstant.NUMBER_OF_TRUST_LEVEL);
        Relationship[][] socialNetworkGraph = mddCreation.getSocialNetworkGraph();
        Map<Long, Integer> personIdToGraphIndex = mddCreation.getPersonIdToGraphIndex();
        assertEquals(3, socialNetworkGraph[0].length);
        assertEquals(3, personIdToGraphIndex.size());
        Person greg = ((List<Person>)personDao.findPeopleByName("Greg")).get(0);
        Person roy = ((List<Person>)personDao.findPeopleByName("Roy")).get(0);
        Person craig = ((List<Person>)personDao.findPeopleByName("Craig")).get(0);

        Relationship relationshipFromGreyToRoy = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL);
        relationshipFromGreyToRoy.setTrustProbability(0.3, 0);
        relationshipFromGreyToRoy.setTrustProbability(0.5, 1);
        relationshipFromGreyToRoy.setTrustProbability(0.2, 2);

        Relationship relationshipFromRoyToGrey = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL);
        relationshipFromRoyToGrey.setTrustProbability(0, 0);
        relationshipFromRoyToGrey.setTrustProbability(0, 1);
        relationshipFromRoyToGrey.setTrustProbability(0.3, 2);

        Relationship relationshipFromGreyToCraig = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL);
        relationshipFromGreyToCraig.setTrustProbability(0.2, 0);
        relationshipFromGreyToCraig.setTrustProbability(0.5, 1);
        relationshipFromGreyToCraig.setTrustProbability(0, 2);

        Relationship relationshipFromCraigToRoy = null;

        Relationship relationshipFromCraigToGrey = null;

        Relationship relationshipFromRoyToCraig = null;

        int indexOfRoy = personIdToGraphIndex.get(roy.getId());
        int indexOfGrey = personIdToGraphIndex.get(greg.getId());
        int indexOfCraig = personIdToGraphIndex.get(craig.getId());

        assertEquals(null, socialNetworkGraph[indexOfRoy][indexOfRoy]);
        assertEquals(null, socialNetworkGraph[indexOfCraig][indexOfCraig]);
        assertEquals(null, socialNetworkGraph[indexOfGrey][indexOfGrey]);
        assertEquals(relationshipFromCraigToGrey, socialNetworkGraph[indexOfCraig][indexOfGrey]);
        assertEquals(relationshipFromRoyToCraig, socialNetworkGraph[indexOfRoy][indexOfCraig]);
        assertEquals(relationshipFromRoyToGrey, socialNetworkGraph[indexOfRoy][indexOfGrey]);
        assertEquals(relationshipFromGreyToRoy, socialNetworkGraph[indexOfGrey][indexOfRoy]);
        assertEquals(relationshipFromGreyToCraig, socialNetworkGraph[indexOfGrey][indexOfCraig]);
        assertEquals(relationshipFromCraigToRoy, socialNetworkGraph[indexOfCraig][indexOfRoy]);
    }

}


























