package com.mdd.algorithm;

import java.util.Arrays;
import java.util.Objects;

public class Relationship {

    private int numberOfTrustLevel;
    private double[] trustProbability;

    public Relationship(int numberOfTrustLevel) {
        this.numberOfTrustLevel = numberOfTrustLevel;
        trustProbability = new double[numberOfTrustLevel];
        for (int i = 0; i < numberOfTrustLevel; i++) {
            trustProbability[i] = 0;
        }
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
                Arrays.equals(trustProbability, that.trustProbability);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(numberOfTrustLevel);
        result = 31 * result + Arrays.hashCode(trustProbability);
        return result;
    }
}
