package com.mdd.algorithm;

import com.google.common.collect.Lists;
import com.mdd.common.CommonTestConstant;
import com.mdd.dao.PersonDao;
import com.mdd.dao.RelationNodeDao;
import com.mdd.dao.ScoreDao;
import com.mdd.dao.TrustRelationDao;
import com.mdd.entity.Person;
import com.mdd.entity.RelationNode;
import com.mdd.entity.Score;
import com.mdd.entity.TrustRelation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Array;
import java.util.*;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class InitializeMDDCreationTest {


    @Autowired
    private TrustRelationDao trustRelationDao;

    @Autowired
    private PersonDao personDao;


    @Before
    public void addPeopleAndRelationshipToDB() {
        personDao.deleteAll();
        trustRelationDao.deleteAll();


//        Person grey = new Person("Grey");
//        Person roy = new Person("Roy");
//        Person craig = new Person("Craig");
//        Person mike = new Person("Mike");
//
//
//        TrustRelation trustRelation1 = new TrustRelation(grey, roy, 1, 0.5);
//        TrustRelation trustRelation2 = new TrustRelation(grey, roy, 2, 0.4);
//        TrustRelation trustRelation3 = new TrustRelation(grey, roy, 0, 0.1);
//        TrustRelation trustRelation4 = new TrustRelation(roy, grey, 2, 1);
//        TrustRelation trustRelation5 = new TrustRelation(roy, grey, 1, 0);
//        TrustRelation trustRelation6 = new TrustRelation(roy, grey, 0, 0);
//        TrustRelation trustRelation7 = new TrustRelation(grey, craig, 1, 0.6);
//        TrustRelation trustRelation8 = new TrustRelation(grey, craig, 0, 0);
//        TrustRelation trustRelation9 = new TrustRelation(grey, craig, 2, 0.4);
//        TrustRelation trustRelation10 = new TrustRelation(craig, roy, 2, 1);
//        TrustRelation trustRelation11 = new TrustRelation(craig, roy, 1, 0);
//        TrustRelation trustRelation12 = new TrustRelation(craig, roy, 0, 0);
//        TrustRelation trustRelation13 = new TrustRelation(roy, mike, 2, 0.3);
//        TrustRelation trustRelation14 = new TrustRelation(roy, mike, 1, 0.5);
//        TrustRelation trustRelation15 = new TrustRelation(roy, mike, 0, 0.2);
////        TrustRelation trustRelation16 = new TrustRelation(craig, mike, 2, 0.2);
////        TrustRelation trustRelation17 = new TrustRelation(craig, mike, 1, 0.2);
////        TrustRelation trustRelation18 = new TrustRelation(craig, mike, 0, 0.6);
//        TrustRelation trustRelation19 = new TrustRelation(mike, roy, 2, 0.1);
//        TrustRelation trustRelation20 = new TrustRelation(mike, roy, 1, 0.8);
//        TrustRelation trustRelation21 = new TrustRelation(mike, roy, 0, 0.1);
//
//        grey.trust(trustRelation1);
//        grey.trust(trustRelation2);
//        grey.trust(trustRelation3);
//        grey.trust(trustRelation7);
//        grey.trust(trustRelation8);
//        grey.trust(trustRelation9);
//        roy.trust(trustRelation4);
//        roy.trust(trustRelation5);
//        roy.trust(trustRelation6);
//        roy.trust(trustRelation13);
//        roy.trust(trustRelation14);
//        roy.trust(trustRelation15);
//        craig.trust(trustRelation10);
//        craig.trust(trustRelation11);
//        craig.trust(trustRelation12);
////        craig.trust(trustRelation16);
////        craig.trust(trustRelation17);
////        craig.trust(trustRelation18);
//        mike.trust(trustRelation19);
//        mike.trust(trustRelation20);
//        mike.trust(trustRelation21);
//
//
//
//        personDao.save(grey);
//        personDao.save(roy);
//        personDao.save(craig);
//        personDao.save(mike);

        int numOfPeople = 2000;
        int numOfRelationship = 10000;
        List<Person> people = new ArrayList<>();
        for(int i = 0; i < numOfPeople; i++)
            people.add(new Person("Mike" + i));
        Random random = new Random();
        Set<String> set = new HashSet<>();
        for (int i = 0; i < numOfRelationship; i++) {
            int s = random.nextInt(numOfPeople);
            int t = s;
            while (t == s)
                t = random.nextInt(numOfPeople);
            if (!set.contains(s + "," + t)) {
                set.add(s + "," + t);
                Person person1 = people.get(s);
                Person person2 = people.get(t);
                person1.trust(new TrustRelation(person1, person2, 0, 0.2));
                person1.trust(new TrustRelation(person1, person2, 1, 0.6));
                person1.trust(new TrustRelation(person1, person2, 2, 0.2));
            }
        }
        personDao.save(people, 1);
    }

    @After
    public void removeAllFromDB() {
//        personDao.deleteAll();
//        trustRelationDao.deleteAll();
    }

    @Test
    public void testPerformance() {

    }


    // Check if grey trust level is correct in the relationship graph from reading the database
    // All the other should be null
    @Test
    public void shouldSocialNetworkGraphBeCreatedSuccessfullyFromDB() {
        MDDCreation mddCreation = new MDDCreation(CommonTestConstant.NUMBER_OF_TRUST_LEVEL);
        List<List<Relationship>> socialNetwork = mddCreation.getSocialNetwork();
        Map<Long, Integer> personIdToGraphIndex = mddCreation.getPersonIdToGraphIndex();
        assertEquals(4, socialNetwork.size());
        assertEquals(4, personIdToGraphIndex.size());
        Person grey = ((List<Person>) personDao.findPeopleByName("Grey")).get(0);
        Person roy = ((List<Person>) personDao.findPeopleByName("Roy")).get(0);
        Person craig = ((List<Person>) personDao.findPeopleByName("Craig")).get(0);
        Person mike = ((List<Person>) personDao.findPeopleByName("Mike")).get(0);
        int indexOfRoy = personIdToGraphIndex.get(roy.getId());
        int indexOfGrey = personIdToGraphIndex.get(grey.getId());
        int indexOfCraig = personIdToGraphIndex.get(craig.getId());
        int indexOfMike = personIdToGraphIndex.get(mike.getId());

        Relationship relationshipFromGreyToRoy = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, grey.getId(), roy.getId());
        relationshipFromGreyToRoy.setTrustProbability(0.1, 0);
        relationshipFromGreyToRoy.setTrustProbability(0.5, 1);
        relationshipFromGreyToRoy.setTrustProbability(0.4, 2);

        Relationship relationshipFromRoyToGrey = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, roy.getId(), grey.getId());
        relationshipFromRoyToGrey.setTrustProbability(0, 0);
        relationshipFromRoyToGrey.setTrustProbability(0, 1);
        relationshipFromRoyToGrey.setTrustProbability(1, 2);

        Relationship relationshipFromGreyToCraig = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, grey.getId(), craig.getId());
        relationshipFromGreyToCraig.setTrustProbability(0, 0);
        relationshipFromGreyToCraig.setTrustProbability(0.6, 1);
        relationshipFromGreyToCraig.setTrustProbability(0.4, 2);

        Relationship relationshipFromCraigToRoy = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, craig.getId(), roy.getId());
        relationshipFromCraigToRoy.setTrustProbability(0, 0);
        relationshipFromCraigToRoy.setTrustProbability(0, 1);
        relationshipFromCraigToRoy.setTrustProbability(1, 2);

//        Relationship relationshipFromCraigToMike = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, craig.getId(), mike.getId());
//        relationshipFromCraigToMike.setTrustProbability(0.6, 0);
//        relationshipFromCraigToMike.setTrustProbability(0.2, 1);
//        relationshipFromCraigToMike.setTrustProbability(0.2, 2);

        Relationship relationshipFromRoyToMike = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, roy.getId(), mike.getId());
        relationshipFromRoyToMike.setTrustProbability(0.2, 0);
        relationshipFromRoyToMike.setTrustProbability(0.5, 1);
        relationshipFromRoyToMike.setTrustProbability(0.3, 2);

        Relationship relationshipFromMikeToRoy = new Relationship(CommonTestConstant.NUMBER_OF_TRUST_LEVEL, mike.getId(), roy.getId());
        relationshipFromMikeToRoy.setTrustProbability(0.1, 0);
        relationshipFromMikeToRoy.setTrustProbability(0.8, 1);
        relationshipFromMikeToRoy.setTrustProbability(0.1, 2);

        assertEquals(1, socialNetwork.get(indexOfMike).size());
        assertEquals(1, socialNetwork.get(indexOfCraig).size());
        assertEquals(2, socialNetwork.get(indexOfGrey).size());
        assertEquals(2, socialNetwork.get(indexOfRoy).size());

        assertTrue(socialNetwork.get(indexOfMike).contains(relationshipFromMikeToRoy));
        assertTrue(socialNetwork.get(indexOfRoy).contains(relationshipFromRoyToGrey));
        assertTrue(socialNetwork.get(indexOfGrey).contains(relationshipFromGreyToRoy));
        assertTrue(socialNetwork.get(indexOfGrey).contains(relationshipFromGreyToCraig));
        assertTrue(socialNetwork.get(indexOfCraig).contains(relationshipFromCraigToRoy));
//        assertTrue(socialNetwork.get(indexOfCraig).contains(relationshipFromCraigToMike));
        assertTrue(socialNetwork.get(indexOfRoy).contains(relationshipFromRoyToMike));

    }


}
