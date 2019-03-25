package com.mdd.algorithm;

import com.mdd.common.SpringContext;
import com.mdd.dao.PersonDao;
import com.mdd.dao.TrustRelationDao;
import com.mdd.entity.Person;
import com.mdd.entity.TrustRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mdd.common.CommonConstant.NO_ORDERED;

public class MDDCreation {

    private Relationship[][] socialNetworkGraph;
    private int numberOfTrustLevel = -1;
    private Map<Long, Integer> personIdToGraphIndex = new HashMap<>();
    private int sizeOfPeople;
    private int sizeOfRelationship = 0;

    MDDCreation(int numberOfTrustLevel) {
        this.numberOfTrustLevel = numberOfTrustLevel;
        ApplicationContext context =  SpringContext.getApplicationContext();
        trustRelationDao = context.getBean(TrustRelationDao.class);
        personDao = context.getBean(PersonDao.class);
        initializeSocialNetworkGraph();
        sizeOfPeople = socialNetworkGraph[0].length;
    }

    private TrustRelationDao trustRelationDao;
    private PersonDao personDao;

    Relationship[][] getSocialNetworkGraph() {
        return socialNetworkGraph;
    }

    Map<Long, Integer> getPersonIdToGraphIndex() {
        return personIdToGraphIndex;
    }

    //Used for testing main method
    MDDCreation(Relationship[][] socialNetworkGraph) {
        this.socialNetworkGraph = socialNetworkGraph;
        sizeOfPeople = socialNetworkGraph[0].length;
        for (int i = 0; i < sizeOfPeople; i++) {
            for (int j = 0; j < sizeOfPeople; j++) {
                if (socialNetworkGraph[i][j] != null) {
                    sizeOfRelationship++;
                }
            }
        }
    }

    /**
     * Initialize the social network graph by getting all the trust relationships from database
     * For constructing the graph, adjacency list needs edge^2 but the graph needs node^2.
     * Here we use the matrix for coding simplicity to avoid generic array creation
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
            Relationship relationship;
            if (socialNetworkGraph[sourcePersonIndex][targetPersonIndex] == null) {
                relationship = new Relationship(numberOfTrustLevel, sourcePersonIndex, targetPersonIndex);
                sizeOfRelationship++;
            }
            else {
                relationship = socialNetworkGraph[sourcePersonIndex][targetPersonIndex];
            }
            relationship.setTrustProbability(trustRelation.getProbability(), trustRelation.getTrustIndex());

            socialNetworkGraph[sourcePersonIndex][targetPersonIndex] = relationship;
        }
    }

    void createMDD(int sourceNode, int targetNode, int trustLevel) {
        List<LinkedList<Relationship>> paths = orderRelationship(sourceNode, targetNode);

        clearOrder();
    }

    /**
     * Clear all the order made by ordering process
     */
    private void clearOrder() {
        for (int i = 0; i < sizeOfPeople; i++) {
            for (int j = 0; j < sizeOfPeople; j++) {
                if (socialNetworkGraph[i][j] != null) {
                    socialNetworkGraph[i][j].setOrder(-1);
                }
            }
        }
    }

    /**
     * Ordering the relationship by queuing the edges when traversing from source node to sink node
     * The order should be output from queue and the order will be set to corresponding relationship from 1
     * For adjacency matrix, it will need v^2. List will need v + e
     * @param sourceNode Node for the source person
     * @param targetNode Node for the end person
     * @return Ordered paths from source person to the sink one
     */
    List<LinkedList<Relationship>> orderRelationship(int sourceNode, int targetNode) {
        boolean[] isVisited = new boolean[sizeOfPeople];
        LinkedList<Relationship> orderQueue = new LinkedList<>();
        List<LinkedList<Relationship>> paths = new LinkedList<>();
        findAllPathsAndStoreInQueue(sourceNode, targetNode, orderQueue, isVisited, paths);
        List<Relationship> relationships = paths.stream().flatMap(Queue::stream).collect(Collectors.toList());
        int order = 1;
        for (Relationship relationship : relationships) {
            //Without setting the order, go to set it
            if (relationship.getOrder() == NO_ORDERED) {
                relationship.setOrder(order);
                order++;
            }
        }
        return paths;
    }

    /**
     * Find all paths from the source person to the sink person by using DFS
     * @param sourceNode Node for the source person
     * @param targetNode Node for the end person
     * @param orderQueue Queue to store all the relationship from source node to sink node
     * @param isVisited Check if the node is visited
     * @param paths To contain all the different paths from source to sink
     */
    private void findAllPathsAndStoreInQueue(int sourceNode, int targetNode, LinkedList<Relationship> orderQueue, boolean[] isVisited,
                                             List<LinkedList<Relationship>> paths) {
        isVisited[sourceNode] = true;
        //Find the path
        if (sourceNode == targetNode) {
            isVisited[sourceNode] = false;
            LinkedList orderedQueue = new LinkedList(orderQueue);
            paths.add(orderedQueue);
            return;
        }

        for (int i = 0; i < sizeOfPeople; i++) {
            Relationship adjacentRelationship = socialNetworkGraph[sourceNode][i];
            if (adjacentRelationship != null && !isVisited[i]) {
                orderQueue.add(adjacentRelationship);
                findAllPathsAndStoreInQueue(i, targetNode, orderQueue, isVisited, paths);
                orderQueue.removeLast();
            }
        }
        isVisited[sourceNode] = false;
    }


}
