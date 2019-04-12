package com.mdd.algorithm;

import com.mdd.common.SpringContext;
import com.mdd.dao.PersonDao;
import com.mdd.dao.TrustRelationDao;
import com.mdd.entity.Person;
import com.mdd.entity.TrustRelation;
import org.springframework.context.ApplicationContext;
import java.util.*;



import static com.mdd.common.CommonConstant.NO_ORDERED;

public class MDDCreation {

    private Relationship[][] socialNetworkGraph;
    private int numberOfTrustLevel = -1;
    private Map<Long, Integer> personIdToGraphIndex = new HashMap<>();
    private int sizeOfPeople;

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
                    this.numberOfTrustLevel = socialNetworkGraph[i][j].getTrustProbability().length;
                    break;
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
            }
            else {
                relationship = socialNetworkGraph[sourcePersonIndex][targetPersonIndex];
            }
            relationship.setTrustProbability(trustRelation.getProbability(), trustRelation.getTrustIndex());
            socialNetworkGraph[sourcePersonIndex][targetPersonIndex] = relationship;
        }
    }

    /**
     * Check if the source, target person and trust level are within index.
     * Order and get the path firstly and then
     * create MDD from source person to sink person at given trust level
     * Clear the order information for the relationship
     * @param sourceNode Source person
     * @param targetNode Sink person
     * @param trustLevel The trust degree from source person to the sink person
     * @return MDD constructing the evaluation of the trust level from source person to the sink person
     */
    MDD createMDD(int sourceNode, int targetNode, int trustLevel) {
        if (trustLevel >= numberOfTrustLevel || trustLevel < 0) {
            throw new IllegalArgumentException("Input trust level is out of the limit for number of trust level!");
        }
        if (targetNode >= sizeOfPeople || sourceNode >= sizeOfPeople) {
            throw new IndexOutOfBoundsException("Source or target index is too large to get the person!");
        }
        List<List<Relationship>> paths = orderRelationship(sourceNode, targetNode);
        MDD mdd = createMDD(paths, trustLevel);
        clearOrder();
        return mdd;
    }

    /**
     * Create MDD from source person to sink person at given trust level
     * Iterate all the paths combination that corresponds the demand of constructing the mdd
     * and apply or operation to the combination to get the result.
     * Use reduction algorithm to reduce the surplus nodes of the result.
     * @param paths All of the possible paths from source person to the sink one
     * @param trustLevel The trust level we need to make sure the mdd at
     * @return  MDD at the trust level
     */
    private MDD createMDD(List<List<Relationship>> paths, int trustLevel) {
        MDD mddAtTrustLevel = null;
        int size = paths.size();
        for (int i = 0; i < size; i++) {
            //Avoid MDD20, MDD30 to be applied or operation with mdd at trust level
            if (i >= 1 && trustLevel == 0) break;
            MDD mddWhenThisPathAtTrustLevel = createMDDForPathAtSomeTrustLevel(paths.get(i), trustLevel);
            for (int j = 0; j < size; j++) {
                //avoid trustLevel - 1 becomes out of index bound
                if (j == i) {
                    continue;
                }
                MDD mddForThisPath;
                if (j > i) {
                    mddForThisPath = createMDDForPathAtSomeTrustLevel(paths.get(j), trustLevel);
                    for (int k = trustLevel - 1; k >= 0; k--) {
                        mddForThisPath = mddForThisPath.or(createMDDForPathAtSomeTrustLevel(paths.get(j), k));
                    }
                }
                else {
                    mddForThisPath = createMDDForPathAtSomeTrustLevel(paths.get(j), trustLevel - 1);
                    for (int k = trustLevel - 2; k >= 0; k--) {
                        mddForThisPath = mddForThisPath.or(createMDDForPathAtSomeTrustLevel(paths.get(j), k));
                    }
                }
                mddWhenThisPathAtTrustLevel = mddWhenThisPathAtTrustLevel.and(mddForThisPath);
            }
            if (mddAtTrustLevel == null) {
                mddAtTrustLevel = mddWhenThisPathAtTrustLevel;
            }
            else {
                mddAtTrustLevel.or(mddWhenThisPathAtTrustLevel);
            }
        }
        MDDReduction mddReduction = new MDDReduction(numberOfTrustLevel);
        mddReduction.reduce(mddAtTrustLevel);
        return mddAtTrustLevel;
    }

    /**
     * Create the MDD for a relationship node. Use trustLevelToGo to specify the sink node 1.
     * The other left should be sink node 0.
     * @param trustLevelToGo The index of state linking to the sink node 1
     * @param relationship The relationship for the current MDD's root node
     * @return return a basic MDD for a relationship node linking to sink node
     */
    private MDD createMDDForRelationship(int[] trustLevelToGo, Relationship relationship) {
        RelationshipNode relationshipNode = new RelationshipNode(relationship);
        MDD basicMdd = new MDD(relationshipNode);
        for (int i = 0; i < numberOfTrustLevel; i++) {
            relationshipNode.putNextNode(i, new RelationshipNode(0, relationshipNode));
        }
        for (int indexToGO : trustLevelToGo) {
            relationshipNode.putNextNode(indexToGO, new RelationshipNode(1, relationshipNode));
        }
        return basicMdd;
    }

    /**
     * Create MDD for a specific trust level. Loop through the relationship in this path
     * and apply and operation to get one possibility. Finally apply or to all of the combinations.
     * @param path One of the path from source person to the sink one.
     * @param trustLevel The expected trust level for this path to obtain
     * @return The MDD combines all the different possibilities to achieve the path at this trust level, null if the path is null or empty
     */
     MDD createMDDForPathAtSomeTrustLevel(List<Relationship> path, int trustLevel) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        int indexOfCurrentNode = 0;
        //Two types of array for creating mdd for a relationship.
        int[] trustLevelToGoForLaterNode = new int[numberOfTrustLevel - trustLevel];
        int[] trustLevelToGoForPreviousNode = new int[numberOfTrustLevel - trustLevel - 1];
        for (int i = 0; i < trustLevelToGoForLaterNode.length; i++) {
            trustLevelToGoForLaterNode[i] = trustLevel + i;
        }
        for (int i = 0; i < trustLevelToGoForPreviousNode.length; i++) {
            trustLevelToGoForPreviousNode[i] = trustLevel + i + 1;
        }
        MDD mddFromApplyingOrToAllCombination = null;
        for (Relationship node : path) {
            int[] trustLevelToGo = new int[1];
            trustLevelToGo[0] = trustLevel;
            MDD mddForOneCombinationOfNodes = createMDDForRelationship(trustLevelToGo ,node);
            int indexOfOtherNode = 0;
            //To apply and operation for the other nodes in the path to achieve the trust level.
            for (Relationship otherNode : path) {
                if (indexOfOtherNode == indexOfCurrentNode) {
                    indexOfOtherNode++;
                    continue;
                }
                if (indexOfOtherNode > indexOfCurrentNode) {
                    MDD otherMDD = createMDDForRelationship(trustLevelToGoForLaterNode, otherNode);
                    mddForOneCombinationOfNodes = mddForOneCombinationOfNodes.and(otherMDD);
                }
                else {
                    MDD otherMDD = createMDDForRelationship(trustLevelToGoForPreviousNode, otherNode);
                    mddForOneCombinationOfNodes = mddForOneCombinationOfNodes.and(otherMDD);
                }
                indexOfOtherNode++;
            }
            indexOfCurrentNode++;
            if (mddFromApplyingOrToAllCombination == null) {
                mddFromApplyingOrToAllCombination = mddForOneCombinationOfNodes;
            }
            else {
                mddFromApplyingOrToAllCombination =
                        mddFromApplyingOrToAllCombination.or(mddForOneCombinationOfNodes);
            }
        }
        return mddFromApplyingOrToAllCombination;
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
    List<List<Relationship>> orderRelationship(int sourceNode, int targetNode) {
        boolean[] isVisited = new boolean[sizeOfPeople];
        LinkedList<Relationship> orderQueue = new LinkedList<>();
        List<List<Relationship>> paths = new ArrayList<>();
        findAllPathsAndStoreInQueue(sourceNode, targetNode, orderQueue, isVisited, paths);
        int order = 1;
        for (List<Relationship> relationships : paths) {
            for (Relationship relationship : relationships) {
                //Without setting the order, go to set it
                if (relationship.getOrder() == NO_ORDERED) {
                    relationship.setOrder(order);
                    order++;
                }
            }
        }
        return paths;
    }

    /**
     * Find all paths from the source person to the sink person by using DFS
     * Array list is fine because the add and remove operation aims to the last element.
     * @param sourceNode Node for the source person
     * @param targetNode Node for the end person
     * @param orderQueue Queue to store all the relationship from source node to sink node
     * @param isVisited Check if the node is visited
     * @param paths To contain all the different paths from source to sink
     */
    private void findAllPathsAndStoreInQueue(int sourceNode, int targetNode, List<Relationship> orderQueue, boolean[] isVisited,
                                             List<List<Relationship>> paths) {
        isVisited[sourceNode] = true;
        //Find the path
        if (sourceNode == targetNode) {
            isVisited[sourceNode] = false;
            List<Relationship> orderedQueue = new ArrayList<>(orderQueue);
            paths.add(orderedQueue);
            return;
        }
        for (int i = 0; i < sizeOfPeople; i++) {
            Relationship adjacentRelationship = socialNetworkGraph[sourceNode][i];
            if (adjacentRelationship != null && !isVisited[i]) {
                orderQueue.add(adjacentRelationship);
                findAllPathsAndStoreInQueue(i, targetNode, orderQueue, isVisited, paths);
                orderQueue.remove(orderQueue.size() - 1);
            }
        }
        isVisited[sourceNode] = false;
    }


}
