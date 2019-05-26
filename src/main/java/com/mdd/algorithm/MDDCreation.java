package com.mdd.algorithm;

import com.mdd.common.SpringContext;
import com.mdd.dao.PersonDao;
import com.mdd.dao.TrustRelationDao;
import com.mdd.entity.Person;
import com.mdd.entity.TrustRelation;
import org.springframework.context.ApplicationContext;
import java.util.*;


import static com.mdd.common.CommonConstant.LEVEL_OF_TRUST_CIRCLE;
import static com.mdd.common.CommonConstant.NO_ORDERED;

public class MDDCreation {

    private List<List<Relationship>> socialNetwork;
    private int numberOfTrustLevel;
    private Map<Long, Integer> personIdToGraphIndex = new HashMap<>();
    private int sizeOfPeople;
    int times = 0;
    public MDDCreation(int numberOfTrustLevel) {
        this.numberOfTrustLevel = numberOfTrustLevel;
        ApplicationContext context =  SpringContext.getApplicationContext();
        personDao = context.getBean(PersonDao.class);
        initializeSocialNetworkGraph();
        sizeOfPeople = socialNetwork.size();
    }

    private PersonDao personDao;


    List<List<Relationship>> getSocialNetwork() {
        return socialNetwork;
    }

    Map<Long, Integer> getPersonIdToGraphIndex() {
        return personIdToGraphIndex;
    }

    public void setPersonIdToGraphIndex(Map<Long, Integer> personIdToGraphIndex) {
        this.personIdToGraphIndex = personIdToGraphIndex;
    }

    //Used for testing main method
    MDDCreation(List<List<Relationship>> socialNetwork) {
        this.socialNetwork = socialNetwork;
        sizeOfPeople = socialNetwork.size();
        this.numberOfTrustLevel = socialNetwork.get(0).get(0).getNumberOfTrustLevel();
    }

    /**
     * Initialize the social network graph by getting all the persons' trust relations from database
     * Here we use the adjacent list for efficiency for ordering process
     */
    private void initializeSocialNetworkGraph() {
        List<Person> people = (List<Person>)personDao.findAll(1);
        if (people.isEmpty())
            throw new NullPointerException("Cannot find the person in database!");
        List<List<Relationship>> adjacentList = new ArrayList<>(people.size());
        int indexInGraph = 0;
        for (Person person : people) {
            personIdToGraphIndex.put(person.getId(), indexInGraph);
            adjacentList.add(new ArrayList<>());
            if (person.getTrustedPeople() == null) continue;
            Map<Person, Relationship> targetToRelationship = new HashMap<>();
            //Loop to create map of the target people to the relationship for them
            for (TrustRelation trustRelation : person.getTrustedPeople()) {
                Person targetPerson = trustRelation.getTarget();
                Relationship relationship = targetToRelationship.getOrDefault(targetPerson,
                        new Relationship(numberOfTrustLevel, person.getId(), targetPerson.getId()));
                relationship.setTrustProbability(trustRelation.getProbability(), trustRelation.getTrustIndex());
                targetToRelationship.put(targetPerson, relationship);
            }
            // Add all the non-duplicate relationships into adjacent list
            for (Relationship relationship : targetToRelationship.values())
                adjacentList.get(indexInGraph).add(relationship);
            indexInGraph++;
        }
        socialNetwork = adjacentList;
    }

    /**
     * Check if the source, target person and trust level are within index.
     * Order and get the path firstly and then
     * create MDD from source person to sink person at given trust level
     * Clear the order information for the relationship
     * @param sourceNode Source person id
     * @param targetNode Sink person id
     * @param trustLevel The trust degree from source person to the sink person
     * @return MDD constructing the evaluation of the trust level from source person to the sink person
     */
    public MDD createMDD(long sourceNode, long targetNode, int trustLevel) {
        clearOrder();
        if (trustLevel >= numberOfTrustLevel || trustLevel < 0)
            throw new IllegalArgumentException("Input trust level is out of the limit for number of trust level!");
        if (!personIdToGraphIndex.containsKey(sourceNode) || !personIdToGraphIndex.containsKey(targetNode))
            throw new IndexOutOfBoundsException("Source or target index is not defined in the network!");

        List<List<Relationship>> paths = orderRelationship(sourceNode, targetNode);
        return createMDD(paths, trustLevel);
    }

    /**
     * Create MDD from source person to sink person at given trust level
     * Iterate all the paths combination that corresponds the demand of constructing the mdd
     * and apply or operation to the combination to get the result.
     * Use reduction algorithm to reduce the surplus nodes of the result.
     * @param paths All of the possible paths from source person to the sink one
     * @param trustLevel The trust level we need to make sure the mdd at
     * @return  MDD at the trust level. If there is no available path, just return null.
     */
    private MDD createMDD(List<List<Relationship>> paths, int trustLevel) {
        System.out.println(paths.size());
        for (List<Relationship> relationships : paths) System.out.println("nodes"+relationships.size()+","+relationships);
        if (paths.isEmpty()) return null;
        MDD mddAtTrustLevel = null;
        int size = paths.size();
        //To avoid computing for or the combination
        if (size == 1) return createMDDForPathAtSomeTrustLevel(paths.get(0), trustLevel);
        // i + 1 is used to store the mdd when the path i is at given trust level
        MDD [] mddArray = new MDD[size + 1];
        // Other i,j means (mdds i and mdds i+1 and... mdds j)
        // mdds means for applying or to all the possible mdds
        MDD [][] mdds = new MDD[size + 1][size + 1];
        for (int i = 0; i < size; i++)
            createCombinationMDDsForPath(trustLevel, paths.get(i), mdds, mddArray, i + 1);
        for (int i = 0; i < size; i++) {
            System.out.println("path"+ i);
            //Avoid MDD20, MDD30 to be applied or operation with mdd at trust level
            if (i >= 1 && trustLevel == 0) break;
            MDD mddWhenThisPathAtTrustLevel;
            //  [1][i] and i and [i + 2][size]
            if (i > 0 && i < size - 1) {
                mdds[1][i] = mdds[i][i].and(mdds[1][i - 1]);
                mddWhenThisPathAtTrustLevel = mdds[1][i].and(mddArray[i + 1]).and(mdds[i + 2][size]);
            }
            else if (i == size - 1)
                mddWhenThisPathAtTrustLevel = mdds[size - 1][size - 1].and(mdds[1][size - 2]).and(mddArray[size]);
            // i == 0
            else {
                mddWhenThisPathAtTrustLevel = mdds[size][size];
                for (int j = size - 2; j >= 1; j--) {
                    //avoid trustLevel - 1 becomes out of index bound
                    mddWhenThisPathAtTrustLevel = mddWhenThisPathAtTrustLevel.and(mdds[j + 1][j + 1]);
                    mdds[j + 1][size] = mddWhenThisPathAtTrustLevel;
                }
                mddWhenThisPathAtTrustLevel = mddWhenThisPathAtTrustLevel.and(mddArray[1]);
            }
                mddAtTrustLevel = mddAtTrustLevel == null ? mddWhenThisPathAtTrustLevel :
                        mddAtTrustLevel.or(mddWhenThisPathAtTrustLevel);
        }
//        MDDReduction mddReduction = new MDDReduction(numberOfTrustLevel);
//        mddReduction.reduce(mddAtTrustLevel);
        return mddAtTrustLevel;
    }

    /**
     * Create all the combination by or operation of mdds lower than or equal to given trust level.
     * It should store mdds in mdds or mddList
     * @param trustLevel Trust level the mdds should be lower than or equal to
     * @param path Path used to construct mdd
     * @param mdds The extra array to store some repeated mdds
     * @param mddList The extra array to store some repeated mdds at given trust level
     * @param j The index + 1 for the path in mdds
     */
    private void createCombinationMDDsForPath(int trustLevel, List<Relationship> path, MDD[][] mdds, MDD[] mddList, int j) {
        MDD mddForThisPath;
        mddForThisPath =
                createMDDForPathAtSomeTrustLevel(path, trustLevel);
        mddList[j] = mddForThisPath;
        for (int k = trustLevel - 1; k >= 0; k--)
            mddForThisPath = createMDDForPathAtSomeTrustLevel(path, k).or(mddForThisPath);
        mdds[j][j] = mddForThisPath;
    }

    /**
     * Create the MDD for a relationship node. Use trustLevelToGo to specify the sink node 1.
     * The other left should be sink node 0.
     * @param trustLevelToGo The index of state linking to the sink node 1
     * @param relationship The relationship for the current MDD's root node
     * @return return a basic MDD for a relationship node linking to sink node
     */
    private MDD createMDDFromRelationship(int[] trustLevelToGo, Relationship relationship) {
        RelationshipNode relationshipNode = new RelationshipNode(relationship);
        MDD basicMdd = new MDD(relationshipNode);
        int j = 0;
        int n = trustLevelToGo.length;
        for (int i = 0; i < numberOfTrustLevel; i++) {
            if (j < n && trustLevelToGo[j] == i) {
                relationshipNode.putNextNode(i, new RelationshipNode(1, relationshipNode));
                j++;
            }
            else
                relationshipNode.putNextNode(i, new RelationshipNode(0, relationshipNode));
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
         if (path == null || path.isEmpty())
            return null;
         int size = path.size();
         if (size == 1) return createMDDFromRelationship(new int[] {trustLevel}, path.get(0));
         //Two types of array for creating mdd for a relationship.
         int[] trustLevelToGoForLaterNode = new int[numberOfTrustLevel - trustLevel];
         int[] trustLevelToGoForPreviousNode = new int[numberOfTrustLevel - trustLevel - 1];
         for (int i = 0; i < trustLevelToGoForLaterNode.length; i++) {
             trustLevelToGoForLaterNode[i] = trustLevel + i;
         }
         for (int i = 0; i < trustLevelToGoForPreviousNode.length; i++) {
             trustLevelToGoForPreviousNode[i] = trustLevel + i + 1;
         }
         // For index i, it stores the combination of mdds applied and operation from i to the size.
         MDD[] mdds = new MDD[size + 1];
         MDD mddFromApplyingOrToAllCombination = null;
         MDD cur = null;
         for (int i = size - 1; i >= 1; i--) {
             cur = createMDDFromRelationship(trustLevelToGoForLaterNode, path.get(i)).and(cur);
             mdds[i + 1] = cur;
         }
         //Use to track the previous combination mdd to avoid repeated computation.
         MDD previousComb = null;
         // Apply 1...i - 1 and i and i + 1...n to compose the mdd
         for (int i = 0; i < size; i++) {
             Relationship node = path.get(i);
             MDD mddForOneCombinationOfNodes = createMDDFromRelationship(new int[] {trustLevel}, node);
             if (i > 0 && i < size - 1) {
                 previousComb = createMDDFromRelationship(trustLevelToGoForPreviousNode, path.get(i - 1)).and(previousComb);
                 mddForOneCombinationOfNodes = mddForOneCombinationOfNodes.and(previousComb).and(mdds[i + 2]);
             }
             //To apply and operation for the other nodes in the path to achieve the trust level.
             else if (i == 0)
                 mddForOneCombinationOfNodes = mddForOneCombinationOfNodes.and(mdds[2]);
             else
                 mddForOneCombinationOfNodes = mddForOneCombinationOfNodes.
                         and(previousComb).and(createMDDFromRelationship(trustLevelToGoForPreviousNode, path.get(size - 2)));
             mddFromApplyingOrToAllCombination = mddFromApplyingOrToAllCombination == null ? mddForOneCombinationOfNodes :
                         mddFromApplyingOrToAllCombination.or(mddForOneCombinationOfNodes);
         }
         return mddFromApplyingOrToAllCombination;
    }

    /**
     * Clear all the order made by ordering process
     */
    private void clearOrder() {
        for (List<Relationship> relationships : socialNetwork) {
            for (Relationship relationship : relationships)
                relationship.setOrder(NO_ORDERED);
        }
    }

    /**
     * Ordering the relationship by queuing the edges when traversing from source node to sink node
     * The order should be output from queue and the order will be set to corresponding relationship from 1
     * For adjacency list will need v + e
     * @param sourceNode Node id for the source person
     * @param targetNode Node id for the end person
     * @return Ordered paths from source person to the sink one
     */
    List<List<Relationship>> orderRelationship(long sourceNode, long targetNode) {
        boolean[] isVisited = new boolean[sizeOfPeople];
        LinkedList<Relationship> orderQueue = new LinkedList<>();
        List<List<Relationship>> paths = new ArrayList<>();
        //Use index in the adjacent list
        int sourceNodeIndex = personIdToGraphIndex.get(sourceNode);
        int targetNodeIndex = personIdToGraphIndex.get(targetNode);
        findAllPathsAndStoreInQueue(sourceNodeIndex, targetNodeIndex, orderQueue, isVisited, paths, 0);
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
     * @param sourceNode Node id for the source person
     * @param targetNode Node id for the end person
     * @param orderQueue Queue to store all the relationship from source node to sink node
     * @param isVisited Check if the node is visited
     * @param paths To contain all the different paths from source to sink
     */
    private void findAllPathsAndStoreInQueue(int sourceNode, int targetNode, List<Relationship> orderQueue, boolean[] isVisited,
                                             List<List<Relationship>> paths, int level) {
        isVisited[sourceNode] = true;
        //Find the path
        if (sourceNode == targetNode) {
            isVisited[sourceNode] = false;
            List<Relationship> orderedQueue = new ArrayList<>(orderQueue);
            paths.add(orderedQueue);
            return;
        }
        //Limit the number of level
        if (level == LEVEL_OF_TRUST_CIRCLE) {
            isVisited[sourceNode] = false;
            return;
        }
        for (Relationship relationship : socialNetwork.get(sourceNode)) {
            int targetIndex = personIdToGraphIndex.get(relationship.getEndNode());
            if (!isVisited[targetIndex]) {
                orderQueue.add(relationship);
                findAllPathsAndStoreInQueue(targetIndex, targetNode, orderQueue, isVisited, paths, level + 1);
                orderQueue.remove(orderQueue.size() - 1);
            }
        }
        isVisited[sourceNode] = false;
    }


}
