package com.mdd.algorithm;

public interface Node {

    /**
     * Get the next node according to the value
     * @param value State of the current node
     * @return Node when choosing the specific value
     */
    Node getNextNode(int value);

    /**
     * Check if the current node is the last node
     * @return True if the node is the last one, false if not.
     */
    boolean isLast();

}
