package com.mdd.algorithm;

import java.util.*;

import static com.mdd.common.CommonConstant.NO_ORDERED;

public class Relationship implements Cloneable{
    
    private int numberOfTrustLevel;
    private double[] trustProbability;
    private long startNode;
    private long endNode;
    private int order = NO_ORDERED;

    /**
     * Relationship stands for the trust relation between start node and end node
     * It includes the different probability for different trust level
     * @param numberOfTrustLevel Number of the trust level for the trust relation
     * @param startNode Index of the source person
     * @param endNode Index of the target person
     */
    Relationship(int numberOfTrustLevel, long startNode, long endNode) {
        this.numberOfTrustLevel = numberOfTrustLevel;
        trustProbability = new double[numberOfTrustLevel];
        for (int i = 0; i < numberOfTrustLevel; i++) {
            trustProbability[i] = 0;
        }
        this.startNode = startNode;
        this.endNode = endNode;
        int [] a = new int[1];
        Arrays.asList(a);
    }

    Relationship(int numberOfTrustLevel, long startNode, long endNode, double[] trustProbability) {
        this.numberOfTrustLevel = numberOfTrustLevel;
        this.trustProbability = trustProbability;
        this.startNode = startNode;
        this.endNode = endNode;
        if (trustProbability.length != numberOfTrustLevel) {
            throw new IllegalArgumentException("Number of trust probability is not the same as the number of trust level!");
        }
    }

    Relationship(long startNode, long endNode, double[] trustProbability) {
        this.numberOfTrustLevel = trustProbability.length;
        this.trustProbability = trustProbability;
        this.startNode = startNode;
        this.endNode = endNode;
    }

    Relationship(Relationship relationship) {
        this.numberOfTrustLevel = relationship.numberOfTrustLevel;
        this.trustProbability = Arrays.copyOf(relationship.trustProbability, numberOfTrustLevel);
        this.startNode = relationship.startNode;
        this.endNode = relationship.endNode;
        this.order = relationship.order;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setTrustProbability(double probability, int indexOfTrustLevel) {
        trustProbability[indexOfTrustLevel] = probability;
    }

    public void setTrustProbability(double[] trustProbability) {
        this.trustProbability = trustProbability;
    }

    public double getTrustProbability(int i) {
        if (i >= numberOfTrustLevel || i < 0)
            throw new IndexOutOfBoundsException("Index is not within the number of trust level");
        return trustProbability[i];
    }

    public double[] getTrustProbability() {
        return trustProbability;
    }

    public int getNumberOfTrustLevel() {
        return numberOfTrustLevel;
    }

    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }

    public long getStartNode() {
        return startNode;
    }

    public void setStartNode(long startNode) {
        this.startNode = startNode;
    }

    public long getEndNode() {
        return endNode;
    }

    public void setEndNode(long endNode) {
        this.endNode = endNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relationship that = (Relationship) o;
        return numberOfTrustLevel == that.numberOfTrustLevel &&
                startNode == that.startNode &&
                endNode == that.endNode &&
                order == that.order &&
                Arrays.equals(trustProbability, that.trustProbability);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(numberOfTrustLevel, startNode, endNode, order);
        result = 31 * result + Arrays.hashCode(trustProbability);
            return result;
    }
}
