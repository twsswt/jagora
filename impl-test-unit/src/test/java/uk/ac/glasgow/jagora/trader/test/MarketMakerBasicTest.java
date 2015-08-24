package uk.ac.glasgow.jagora.trader.test;


import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.engine.TradingEngine;
import uk.ac.glasgow.jagora.engine.impl.DelayableSerialRandomEngineBuilder;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.pricer.impl.OldestLimitOrderPricer;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.ticker.OrderEvent;
import uk.ac.glasgow.jagora.ticker.impl.OutputStreamTradeListener;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.marketmaker.MarketDatum;
import uk.ac.glasgow.jagora.trader.impl.marketmaker.MarketMaker;
import uk.ac.glasgow.jagora.trader.impl.marketmaker.MarketMakerBasicBuilder;
import uk.ac.glasgow.jagora.trader.impl.random.RandomTraderPercentage;
import uk.ac.glasgow.jagora.trader.impl.random.RandomTraderPercentageBuilder;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class MarketMakerBasicTest {

	private MarketMaker marketMaker;


	private World world;
	private Stock lemons;
	private StockExchange stockExchange;

	private SerialTickerTapeObserver tickerTapeObserver;

	private Long numberOfTraderActions = 10000l;
	private TradingEngine engine;


	@Before
	public void setUp () throws  Exception{

		world = new SimpleSerialWorld(numberOfTraderActions);
		lemons = new Stock("lemons");

		MarketFactory marketFactory = new ContinuousOrderDrivenMarketFactory(new OldestLimitOrderPricer());

		tickerTapeObserver = new SerialTickerTapeObserver();

		stockExchange = new DefaultStockExchange(world, tickerTapeObserver, marketFactory);

		Random r = new Random(1);

		Trader dan = new StubTraderBuilder()
				.setName("stub")
				.setCash(100000000l)
				.addStock(lemons, 10).build();

		StockExchangeLevel1View danView = stockExchange.createLevel1View();
		danView.placeLimitBuyOrder(new DefaultLimitBuyOrder(dan, lemons, 5, 1001l));
		danView.placeLimitSellOrder(new DefaultLimitSellOrder(dan, lemons, 5, 999l));

		Set<Level1Trader> traders = new HashSet<Level1Trader>();

		for (int i = 0 ; i < 90 ; i++){
			
			RandomTraderPercentage randomTrader =
				new RandomTraderPercentageBuilder()
					.setName("trader["+i+"]")
					.setCash(100000000l)
					.setSeed(r.nextInt())
					.setBuyOrderRange(lemons, 1, 100, -0.005,0.005)
					.setSellOrderRange(lemons, 1, 100, -0.005,0.005)
					.addStock(lemons, 100)
					.build();

			traders.add(randomTrader);
		}


		marketMaker = new MarketMakerBasicBuilder()
			.setName("Goldman")
			.addStock(lemons, 1000)
			.setTargetStockQuantity(lemons, 1000)
			.setCash(100000000l)
			.setSeed(r.nextInt())
			.setSpread(0.003)
			.build();

		stockExchange.createLevel1View().registerTradeListener(new OutputStreamTradeListener(System.out));

		engine = new DelayableSerialRandomEngineBuilder()
			.setWorld(world)
			.setSeed(1)
			.addStockExchange(stockExchange)
			.addTraders(traders)
			.addPrivilegedTrader(marketMaker)
			.setStandardDelay(6l)
			.build();
	}

	@Test
	public void testLiquidityCalculation() throws Exception{

		engine.run();


		System.out.println("cash is " +marketMaker.getCash() );
		System.out.println("inventory is " +marketMaker.getInventory(lemons));

		Field marketDatum = marketMaker.getClass().getDeclaredField("marketDatum");
		marketDatum.setAccessible(true);
		MarketDatum marketData = (MarketDatum) marketDatum.get(marketMaker);

		Field buyField = marketData.getClass().getDeclaredField("buySideLiquidity");
		buyField.setAccessible(true);
		Integer buySideLiquidity = (Integer) buyField.get(marketData);

		Field sellField = marketData.getClass().getDeclaredField("sellSideLiquidity");
		sellField.setAccessible(true);
		Integer sellSideLiquidity = (Integer) sellField.get(marketData);


		System.out.println("Buy liquidity calculated by marketMaker is " + buySideLiquidity);
		System.out.println("Sell liquidity calculated by marketMaker is " + sellSideLiquidity);


		//Need to execute the following block to have the observer view of liquidity
		Integer realBuySideLiquidity = 0;
		Integer realSellSideLiquidity = 0;
		for (OrderEvent event :tickerTapeObserver.getLimitBuyOrderHistory(lemons))
			realBuySideLiquidity += event.quantity;

		for (OrderEvent event: tickerTapeObserver.getLimitSellOrderHistory(lemons))
			realSellSideLiquidity += event.quantity;


		for (OrderEvent event : tickerTapeObserver.getCancelledBuyOrderHistory(lemons))
			realBuySideLiquidity -= event.quantity;

		for (OrderEvent event: tickerTapeObserver.getCancelledSellOrderHistory(lemons))
			realSellSideLiquidity -= event.quantity;

		for (TickEvent<Trade> event :tickerTapeObserver.getTradeHistory(lemons)){
			realBuySideLiquidity -= event.event.getQuantity();
			realSellSideLiquidity -= event.event.getQuantity();
		}

		//this has to be adjusted around the point at which the market maker is let on the market
		Double permittedError = 400.0;

		assertThat (realBuySideLiquidity.doubleValue(),
				closeTo(buySideLiquidity.doubleValue(),permittedError));
		assertThat(realSellSideLiquidity.doubleValue(),
				closeTo(sellSideLiquidity.doubleValue(),permittedError));

		System.out.println("Observer calculated buy liquidity " + realBuySideLiquidity);
		System.out.println("Observer calculated sell liquidity " +realSellSideLiquidity);

}
}
