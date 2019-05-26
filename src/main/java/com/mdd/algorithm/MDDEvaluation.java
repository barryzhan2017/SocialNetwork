package com.mdd.algorithm;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mdd.common.CommonConstant.*;

public class MDDEvaluation {

    /**
     * Get the system state probability for specific trust level
     * @param mdd Current mdd being calculated
     * @return The score of the trust for a person to the other one.
     */
    public double getProbability(MDD mdd) {
        RelationshipNode rootNode = mdd.getRootNode();
        if (rootNode == null || rootNode.getValue() != NONE_SINK_NODE) return NO_ROOT_NODE;
        return getProbability(mdd.getRootNode());
    }

    /**
     * Get the sensitivity of the specific relationship in a given mdd model. If the relationship is not found,
     * return NO_SUCH_RELATIONSHIP. If root node does not exist, return NO_ROOT_NODE.
     * If the probability for specific trust level is 0, skip this sensitivity calculation.
     * Plus, the final number of trust level to divide should be always number of trust level - 1
     * @param mdd MDD model to analyze the importance of the given relationship
     * @param startNode Start node of the relationship
     * @param endNode End node of the relationship
     * @return Sensitivity of the given relationship
     *
     */
    public double getSensitivityOfNode(MDD mdd, int startNode, int endNode) {
        double probability = getProbability(mdd);
        if (probability == NO_ROOT_NODE) return NO_ROOT_NODE;
        double sensitivity = 0;
        List<Relationship> list = getAllSameRelationshipsFromMDD(mdd.getRootNode(), startNode, endNode);
        if (list.isEmpty()) return NO_SUCH_RELATIONSHIP;
        Relationship exampleRelationship = list.get(0);
        int numberOfTrustLevel = exampleRelationship.getNumberOfTrustLevel();
        double[] tmp = exampleRelationship.getTrustProbability();
        for (int i = 0; i < numberOfTrustLevel; i++) {
            double[] curProbability = new double[numberOfTrustLevel];
            curProbability[i] = tmp[i] == 0 ? 0 : 1;
            for (Relationship relationship : list) {
                relationship.setTrustProbability(curProbability);
            }
            sensitivity += Math.abs(getProbability(mdd) - probability);
        }
        for (Relationship relationship : list)
            relationship.setTrustProbability(tmp);
        return sensitivity / (numberOfTrustLevel - 1);
    }

    /**
     * Get all the relationships given a start node and end node of that kind of relationship.
     * @param root Root node of the mdd
     * @param startNode Target relationship's start node
     * @param endNode Target relationship's end node
     * @return List of relationships corresponds to the target
     */
    private List<Relationship> getAllSameRelationshipsFromMDD(RelationshipNode root, int startNode, int endNode) {
        List<Relationship> relationships = new ArrayList<>();
        dfs(root, startNode, endNode, relationships);
        return relationships;
    }

    /**
     * Depth first search in the mdd to find all the target relationships
     * @param root Current mdd's root node
     * @param startNode Target relationship's start node
     * @param endNode Target relationship's end node
     * @param relationships List of relationships to store the target relationships
     */
    private void dfs(RelationshipNode root, int startNode, int endNode, List<Relationship> relationships) {
        if (root.getValue() == NONE_SINK_NODE) {
            Relationship relationship = root.getRelationship();
            if (relationship.getStartNode() == startNode
                    && relationship.getEndNode() == endNode) {
                relationships.add(relationship);
            }
            else {
                for (RelationshipNode children : root.getTrustLevelToNextNode().values())
                    dfs(children, startNode, endNode, relationships);
            }
        }
    }

    /**
     * Recursively get the probability of each node. For non-sink node, the value should be
     * the sum of all the probability of the trust levels times the probability of the child nodes.
     * For sink node, the score is the value of it.
     * @param rootNode Current node being evaluated for its score.
     * @return Score of the current node
     */
    private double getProbability(RelationshipNode rootNode) {
        if (rootNode.getValue() != NONE_SINK_NODE) return rootNode.getValue();
        double probability = 0;
        for (Map.Entry<Integer, RelationshipNode> childNode : rootNode.getTrustLevelToNextNode().entrySet()) {
            probability += getProbability(childNode.getValue())
                    * rootNode.getRelationship().getTrustProbability(childNode.getKey());
        }
        return probability;
    }
}
