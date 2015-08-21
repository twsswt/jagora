package uk.ac.glasgow.jagora.experiment;


import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Ivelin
 *
 */
public class Experiment0004 extends ExperimentUtility {

    @Before
    public void setUp() throws Exception{
        numberOfTraderActions = 1000000l;

        numberOfSimpleHistoricTraders = 10;
        numberOfRandomSpreadCrossingTraders = 10;
        randomSpreadCrossingTraderSpread = 0.01;

        numberOfMarketMakers = 1;
        marketMakerSpread = 0.002;
        marketMakerShare = 0.1f;

        numberOfHighFrequencyTraders = 3;
        hFTSpread = 0.003;

        standardDelay = 6l;

        createExperiment();
    }

    @Test
    public void test() {
        engine.run();
    }
}
