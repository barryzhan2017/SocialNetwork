package com.mdd.algorithm;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class DynamicAdjustmentImplTest {

    @Test
    public void shouldAdjustmentWhenReceivingLevel0RatingWorksCorrectly() {
        DynamicAdjustment dynamicAdjustment = new DynamicAdjustmentImpl();
        double[] link = new double[] {0.2, 0.6, 0.2};
        dynamicAdjustment.adjust(link, 0, 1);
        assertEquals(0.0, link[2], 0.00001);
        assertEquals(0.64, link[1], 0.00001);
        assertEquals(0.36,link[0],0.00001);
    }

    @Test
    public void shouldAdjustmentWhenReceivingLevel1RatingWorksCorrectly() {
        DynamicAdjustment dynamicAdjustment = new DynamicAdjustmentImpl();
        double[] link = new double[] {0.2, 0.6, 0.2};
        dynamicAdjustment.adjust(link, 1, 1);
        assertEquals(0.0, link[2], 0.00001);
        assertEquals(0.76, link[1], 0.00001);
        assertEquals(0.24,link[0],0.00001);
    }

    @Test
    public void shouldAdjustmentWhenReceivingLevel2RatingWorksCorrectly() {
        DynamicAdjustment dynamicAdjustment = new DynamicAdjustmentImpl();
        double[] link = new double[] {0.2, 0.6, 0.2};
        dynamicAdjustment.adjust(link, 2, 1);
        assertEquals(0.4, link[2], 0.00001);
        assertEquals(0.56, link[1], 0.00001);
        assertEquals(0.04,link[0],0.00001);
    }

    @Test
    public void shouldAdjustmentWhenReceivingLevel2RatingMakesLevel2And0OutOfBoundWorksCorrectly() {
        DynamicAdjustment dynamicAdjustment = new DynamicAdjustmentImpl();
        double[] link = new double[] {0.05, 0.05, 0.9};
        dynamicAdjustment.adjust(link, 2, 1);
        assertEquals(1.0, link[2], 0.00001);
        assertEquals(0.0, link[1], 0.00001);
        assertEquals(0.0, link[0],0.00001);
    }

    @Test
    public void shouldAdjustmentWhenReceivingLevel2RatingMakesLevel0OutOfBoundWorksCorrectly() {
        DynamicAdjustment dynamicAdjustment = new DynamicAdjustmentImpl();
        double[] link = new double[] {0.05, 0.15, 0.8};
        dynamicAdjustment.adjust(link, 2, 5);
        assertEquals(0.96, link[2], 0.00001);
        assertEquals(0.04, link[1], 0.00001);
        assertEquals(0.0, link[0],0.00001);
    }

    @Test
    public void shouldAdjustmentWhenReceivingLevel2RatingMakesLevel2OutOfBoundWorksCorrectly() {
        DynamicAdjustment dynamicAdjustment = new DynamicAdjustmentImpl();
        double[] link = new double[] {0.05, 0.15, 0.8};
        dynamicAdjustment.adjust(link, 2, 4);
        assertEquals(1.0, link[2], 0.00001);
        assertEquals(0.0, link[1], 0.00001);
        assertEquals(0.0, link[0],0.00001);
    }
}
