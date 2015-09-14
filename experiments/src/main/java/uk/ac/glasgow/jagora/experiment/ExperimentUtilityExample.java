package uk.ac.glasgow.jagora.experiment;


import org.junit.Before;

import org.junit.Test;

import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.marketmaker.MarketMaker;
import uk.ac.glasgow.jagora.trader.impl.marketmaker.MarketMakerBuilder;

import java.util.Set;

public class ExperimentUtilityExample  extends  ExperimentUtility{

	/**
	 * Overriding a method that needs to be changed
	 * @param traders
	 */
	@Override
	protected void addMarketMakers(Set<Trader> traders) {
		String name = createTraderName(MarketMaker.class,50);

		Trader trader =
			new MarketMakerBuilder()
				.setName(name)
				.setCash(initialLevel2TraderCash)
				.addMarketPositionSpecification(lemons, 1000, 400)
				.addStock(lemons,1000)
				.build();

		traders.add(trader);
	}


	@Before
	public void setUp() throws Exception{
		//show that overridden method is working
		numberOfMarketMakers = 0;

		seed = 30;
		//change of parameters
		numberOfSimpleHistoricTraders = 25;
		initialTraderCash = 5000l;
		standardDelay = 20l;

		//show a big delayed order
		institutionalInvestorStockPercentage = 0.1;
		delayedSellOrders.put(200l,Math.round(10 ));

		createExperiment();
	}

	@Test
	public void test () {
		engine.run();
	}
}
