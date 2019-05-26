package com.mdd.algorithm;

import java.util.Arrays;

public class DynamicAdjustmentImpl implements DynamicAdjustment{

//    //Pace of trust growth for SinAlpha model
//    private final double w = 0.1;
//    private final double theta = 0.5;
//
//    /**
//     * Get the adjusted probability array for a specific link owing to the change of a value in specific level
//     * Adjustment rule bases on SinAlpha model
//     * @param original Initial probability for each level
//     * @param indexToChange Index in original to be rated/voted
//     * @return New probability for all levels
//     */
//    @Override
//    public double[] adjust(double[] original, int indexToChange) {
//        if (original.length != 3)
//            throw new IllegalArgumentException("Dynamic adjustment for number of trust level not equal to 3 has not been supported!");
//        double weight0 = -0.8;
//        double weight1 = -0.2;
//        double weight2 = 1;
//        if (indexToChange == 1) {
//            weight0 = 0.2;
//            weight1 = 0.8;
//            weight2 = -1;
//        }
//        if (indexToChange == 0) {
//            weight0 = 0.8;
//            weight1 = 0.2;
//            weight2 = -1.5;
//        }
//        double a = Math.asin((original[indexToChange] - theta) /theta);
//        a = a + weight2 * w;
//        double level2 = theta * Math.sin(a) + theta;
//        double level1 = original[1] + Math.abs(level2 - original[2]) * weight1;
//        double level0 = original[0] + Math.abs(level2 - original[2]) * weight0;
//        original[0] = level0;
//        original[1] = level1;
//        original[2] = level2;
//        return original;
//    }

    /**
     * Get the adjusted probability array for a specific link owing to the change of a value in specific level
     * Adjustment rule bases on number of rating/votes received for a specific trust level
     * @param original Initial probability for each level
     * @param indexToChange Index in original to be rated/voted
     * @param n Number of changes on the link for the index
     * @return New probability for all levels
     */
    @Override
    public double[] adjust(double[] original, int indexToChange, int n) {
        if (original.length != 3)
            throw new IllegalArgumentException("Dynamic adjustment for number of trust level not equal to 3 has not been supported!");
        double weight0 = -0.8;
        double weight1 = -0.2;
        double weight2 = 1.0;
        if (indexToChange == 1) {
            weight0 = 0.2;
            weight1 = 0.8;
            weight2 = -1.0;
        }
        if (indexToChange == 0) {
            weight0 = 0.8;
            weight1 = 0.2;
            weight2 = -1.5;
        }
        double level2 = original[2] * (1 + weight2 / n);
        level2 = level2 < 0 ? 0 : level2;
        level2 = level2 > 1 ? 1 : level2;
        double level1 = original[1] + Math.abs(level2 - original[2]) * weight1;
        double level0 = original[0] + Math.abs(level2 - original[2]) * weight0;
        // Adjust the value when out of bound condition appears.
        if (level1 > 1) {
            level1 = 1;
            level0 = 0;
        }
        else if (level0 > 1) {
            level0 = 1;
            level1 = 0;
        }
        else if (level1 < 0) {
            level1 = 0;
            level0 = 1 - level2;
        }
        else if (level0 < 0) {
            level0 = 0;
            level1 = 1 - level2;
        }
        original[0] = level0;
        original[1] = level1;
        original[2] = level2;
        return original;
    }
}
