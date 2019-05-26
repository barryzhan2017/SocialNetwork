package com.mdd.algorithm;


public interface DynamicAdjustment {

//    /**
//     * Get the adjusted probability array for a specific link owing to the change of a value in specific level
//     * Adjustment rule bases on SinAlpha model
//     * @param original Initial probability for each level
//     * @param indexToChange Index in original to be rated/voted
//     * @return New probability for all levels
//     */
//    public double[] adjust(double[] original, int indexToChange);

    /**
     * Get the adjusted probability array for a specific link owing to the change of a value in specific level
     * Adjustment rule bases on number of rating/votes received for a specific trust level
     * @param original Initial probability for each level
     * @param indexToChange Index in original to be rated/voted
     * @param n Number of changes on the link for the index
     * @return New probability for all levels
     */
    public double[] adjust(double[] original, int indexToChange, int n);
}
