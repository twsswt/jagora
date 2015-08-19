package uk.ac.glasgow.jagora.experiment;


import org.junit.Before;
import org.junit.Test;

public class Experiment004 extends ExperimentUtility {

    @Before
    public void setUp() throws Exception{
        numberOfSimpleHistoricTraders = 10;
        numberOfRandomTraders = 10;
        randomTradersSpread = 0.005;

        numberOfMarketMakers = 1;
        marketMakerSpread = 0.002;
        marketMakerShare = 0.1f;

        standardDelay = 6l;

        createExperiment();
    }

    @Test
    public void test() {
        engine.run();
    }
}
