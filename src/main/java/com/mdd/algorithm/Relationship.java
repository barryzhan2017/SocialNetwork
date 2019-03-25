package com.mdd.algorithm;

import java.util.Arrays;
import java.util.Objects;

import static com.mdd.common.CommonConstant.NO_ORDERED;

public class Relationship {

    private int numberOfTrustLevel;
    private double[] trustProbability;
    private int startNode;
    private int endNode;
    private int order = NO_ORDERED;

    /**
     * Relationship stands for the trust relation between start node and end node
     * It includes the different probability for different trust level
     * @param numberOfTrustLevel Number of the trust level for the trust relation
     * @param startNode Index of the source person
     * @param endNode Index of the target person
     */
    Relationship(int numberOfTrustLevel, int startNode, int endNode) {
        this.numberOfTrustLevel = numberOfTrustLevel;
        trustProbability = new double[numberOfTrustLevel];
        for (int i = 0; i < numberOfTrustLevel; i++) {
            trustProbability[i] = 0;
        }
        this.startNode = startNode;
        this.endNode = endNode;
    }

    Relationship(int numberOfTrustLevel, int startNode, int endNode, double[] trustProbability) {
        this.numberOfTrustLevel = numberOfTrustLevel;
        this.trustProbability = trustProbability;
        this.startNode = startNode;
        this.endNode = endNode;
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
