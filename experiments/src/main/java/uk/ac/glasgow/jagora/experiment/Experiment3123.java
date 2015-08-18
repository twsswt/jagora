package uk.ac.glasgow.jagora.experiment;

import org.junit.Before;
import org.junit.Test;


public class Experiment3123 extends ExperimentUtility {

    @Before
    public void setUp() throws Exception{
        createExperiment();
    }

    @Test
    public void test () {
        engine.run();
    }
}
