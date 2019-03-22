package com.mdd.algorithm;

import com.mdd.common.SpringContext;
import com.mdd.dao.PersonDao;
import com.mdd.dao.TrustRelationDao;
import com.mdd.entity.Person;
import com.mdd.entity.TrustRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MDDCreation {

    private Relationship[][] socialNetworkGraph;
    private int numberOfTrustLevel = -1;
    private Map<Long, Integer> personIdToGraphIndex = new HashMap<>();

    MDDCreation(int numberOfTrustLevel) {
        this.numberOfTrustLevel = numberOfTrustLevel;
        ApplicationContext context =  SpringContext.getApplicationContext();
        trustRelationDao = context.getBean(TrustRelationDao.class);
        personDao = context.getBean(PersonDao.class);
        initializeSocialNetworkGraph();
    }

    private TrustRelationDao trustRelationDao;
    private PersonDao personDao;

    Relationship[][] getSocialNetworkGraph() {
        return socialNetworkGraph;
    }

    Map<Long, Integer> getPersonIdToGraphIndex() {
        return personIdToGraphIndex;
    }

    /**
     * Initialize the social network graph by getting all the trust relationships from database
     */
    private void initializeSocialNetworkGraph() {
        Collection<Person> people = (Collection<Person>)personDao.findAll();
        int sizeOfSocialNetworkGraph = people.size();
        socialNetworkGraph = new Relationship[sizeOfSocialNetworkGraph][sizeOfSocialNetworkGraph];
        for (int i = 0; i < sizeOfSocialNetworkGraph; i++) {
            for (int j = 0; j < sizeOfSocialNetworkGraph; j++) {
                socialNetworkGraph[i][j] = null;
            }
        }
        Iterable<TrustRelation> trustRelations = trustRelationDao.findAll();
        int indexOfPeople = 0;
        for (TrustRelation trustRelation : trustRelations) {
            Person sourcePerson = trustRelation.getSource();
            Person targetPerson = trustRelation.getTarget();
            //When new person comes, the graph should record the mapping.
            Long sourceId = sourcePerson.getId();
            Long targetId = targetPerson.getId();
            //No such element, increase the number of people
            if (personIdToGraphIndex.putIfAbsent(sourceId, indexOfPeople) == null) {
                indexOfPeople++;
            }
            if (personIdToGraphIndex.putIfAbsent(targetId, indexOfPeople) == null) {
                indexOfPeople++;
            }
            int sourcePersonIndex = personIdToGraphIndex.get(sourceId);
            int targetPersonIndex = personIdToGraphIndex.get(targetId);
            Relationship relationship = socialNetworkGraph[sourcePersonIndex][targetPersonIndex] == null ?
                    new Relationship(numberOfTrustLevel) : socialNetworkGraph[sourcePersonIndex][targetPersonIndex];
            relationship.setTrustProbability(trustRelation.getProbability(), trustRelation.getTrustIndex());
            socialNetworkGraph[sourcePersonIndex][targetPersonIndex] = relationship;
        }
    }

    void orderNode() {

    }


}
