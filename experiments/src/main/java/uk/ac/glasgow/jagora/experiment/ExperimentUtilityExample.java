package uk.ac.glasgow.jagora.experiment;


import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.trader.impl.marketmaker.MarketMaker;
import uk.ac.glasgow.jagora.trader.impl.marketmaker.MarketMakerBasicBuilder;

import java.util.Set;

public class ExperimentUtilityExample  extends  ExperimentUtility{

	/**
	 * Overriding a method that needs to be changed
	 * @param level2Traders
	 */
	@Override
	protected void addMarketMakers(Set<Level2Trader> level2Traders) {
		String name = createTraderName(MarketMaker.class,50);

		Level2Trader trader =
			new MarketMakerBasicBuilder()
				.setName(name)
				.setCash(initialLevel2TraderCash)
				.setSeed(seed)
				.setInventoryAdjustmentInfluence(marketMakerInventoryAdjustmentInfluence)
				.setLiquidityAdjustmentInfluence(marketMakerLiquidityAdjustmentInfluence)
				.setSpread(marketMakerSpread)
				.setTargetStockQuantity(lemons, 1000)
				.addStock(lemons,1000)
				.build();

		level2Traders.add(trader);
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