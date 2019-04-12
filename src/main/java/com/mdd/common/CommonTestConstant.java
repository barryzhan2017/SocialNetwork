package com.mdd.common;

import com.mdd.algorithm.RelationshipNode;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CommonTestConstant {
    public static final int NUMBER_OF_TRUST_LEVEL = 3;

    public static void checkParents(RelationshipNode parentNode, int numberOfTrustLevel) {
        for (int i = 0; i < numberOfTrustLevel; i++) {
            RelationshipNode childNode = parentNode.getNextNode(i);
            if (childNode != null) {
                assertSame(parentNode, childNode.getParent());
                checkParents(childNode, numberOfTrustLevel);
            }
        }
    }
}
