package com.mdd.algorithm;



import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.*;

import static com.mdd.common.CommonConstant.NONE_SINK_NODE;

public class MDDReduction {

    private int numberOfTrustLevel;

    MDDReduction(int numberOfTrustLevel) {
        this.numberOfTrustLevel = numberOfTrustLevel;
    }

    void reduce(MDD mdd) {
        if (mdd == null || mdd.getRootNode() == null || mdd.getRootNode().getValue() != NONE_SINK_NODE)
            throw new IllegalArgumentException("MDD is null or its root node is null or a sink node!");
        List<List<RelationshipNode>> relationshipNodeInEachOrder = new ArrayList<>();
        Queue<RelationshipNode> queue = new LinkedList<>();
        RelationshipNode rootNode = mdd.getRootNode();
        queue.offer(rootNode);
        levelOrderTraversal(queue, relationshipNodeInEachOrder,
                new RelationshipNode(0), new RelationshipNode(1));
        //Reduce from bottom to avoid backtracking for shannon reduction
        for (int i = relationshipNodeInEachOrder.size() - 1; i >= 0; i--) {
            BiMap<RelationshipNode, Object> existingNodes =
                    HashBiMap.create(relationshipNodeInEachOrder.get(i).size());
            mergeIsomorphicChildNodes(relationshipNodeInEachOrder.get(i), existingNodes);
            applyShannonReductionToChildrenNodes(relationshipNodeInEachOrder.get(i));
        }
        //Change the root node to its child node when root node can be shannon reduced.
        if (canBeShannonReduction(rootNode)) {
            mdd.setRootNode(rootNode.getNextNode(0));
        }
    }

    /**
     * Check for the nodes in specific order. If they are the same, make the parent point to the previous one.
     * @param childNodes Current nodes in some order to be checked
     * @param existingNodes A map to differentiate the same nodes.
     */
    void mergeIsomorphicChildNodes(List<RelationshipNode> childNodes, BiMap<RelationshipNode, Object> existingNodes) {
        for (RelationshipNode childNode : childNodes) {
            Object value = existingNodes.get(childNode);
            if (value != null) {
                childNode.getParent().replaceNextNode(childNode.getTrustLevelFromParentToThis(),
                        existingNodes.inverse().get(value));
            }
            else
                existingNodes.put(childNode, new Object());
        }
    }

    /**
     * Check if the child node pointing to the same grandchild nodes. If so, replace the child node with
     * the grandchild node.
     * @param parentNodes Nodes to check their children nodes.
     */
    void applyShannonReductionToChildrenNodes(List<RelationshipNode> parentNodes) {
        for (RelationshipNode parentNode : parentNodes) {
            for (int i = 0; i < numberOfTrustLevel; i++) {
                if (canBeShannonReduction(parentNode.getNextNode(i))) parentNode.replaceNextNode(i, parentNode.getNextNode(i).getNextNode(0));
            }
        }
    }

    private boolean canBeShannonReduction(RelationshipNode parent) {
        int numberOfEqualNodes = 1;
        int trustLevel = 0;
        RelationshipNode childNode = parent.getNextNode(trustLevel);
        //If grandchild nodes are not null, loop to check if they are the same.
        while (childNode != null && trustLevel + 1 < numberOfTrustLevel) {
            RelationshipNode otherChildNode = parent.getNextNode(trustLevel + 1);
            if (otherChildNode == childNode) numberOfEqualNodes++;
            trustLevel++;
        }
        return  numberOfEqualNodes == numberOfTrustLevel;
    }

    /**
     /**
     * Construct a list of all relationship nodes in each order. Do node consider sink nodes
     * Change the pointer to the common sink nodes when meeting the sink node
     * @param queue A queue used for tracking the relationship node in a level order traversal
     * @param relationshipNodeInEachOrder The result list of all relationship nodes in each order
     * @param sinkNode0 Common sink node with value 0 to be pointed
     * @param sinkNode1 Common sink node with value 1 to be pointed
     */
    void levelOrderTraversal(Queue<RelationshipNode> queue, List<List<RelationshipNode>> relationshipNodeInEachOrder,
    RelationshipNode sinkNode0, RelationshipNode sinkNode1) {
        if (queue.isEmpty()) return;
        RelationshipNode rootNode = queue.poll();
        //Don't add any sink nodes into the list.
        if (rootNode.getValue() == 0) {
            rootNode.getParent().replaceNextNode(rootNode.getTrustLevelFromParentToThis(), sinkNode0);
        }
        else if (rootNode.getValue() == 1) {
            rootNode.getParent().replaceNextNode(rootNode.getTrustLevelFromParentToThis(), sinkNode1);
        }
        else {
            int order = rootNode.getRelationship().getOrder();
            //If the lists do not contain the list of current order, add one.
            while (relationshipNodeInEachOrder.size() < order) {
                relationshipNodeInEachOrder.add(new ArrayList<>());
            }
            relationshipNodeInEachOrder.get(order - 1).add(rootNode);
            //Compare order to check if it has moved to the next order
            for (int i = 0; i < this.numberOfTrustLevel; i++) {
                queue.offer(rootNode.getNextNode(i));
            }
        }
        levelOrderTraversal(queue, relationshipNodeInEachOrder, sinkNode0, sinkNode1);
    }
}
