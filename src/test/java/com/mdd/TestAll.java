package com.mdd;


import com.mdd.algorithm.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({MDDCreationTest.class, MDDTest.class, MDDReductionTest.class, MDDEvaluationTest.class})
public class TestAll {
}
