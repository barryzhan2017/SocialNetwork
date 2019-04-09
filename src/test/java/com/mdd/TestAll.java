package com.mdd;


import com.mdd.algorithm.MDDCreationTest;
import com.mdd.algorithm.MDDReduction;
import com.mdd.algorithm.MDDReductionTest;
import com.mdd.algorithm.MDDTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({MDDCreationTest.class, MDDTest.class, MDDReductionTest.class})
public class TestAll {
}
