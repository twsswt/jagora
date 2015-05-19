package uk.ac.glasgow.jagora.experiment;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.MarketFactory;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.engine.TradingEngine;
import uk.ac.glasgow.jagora.engine.impl.SerialRandomEngineBuilder;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.SimpleHistoricTrader;
import uk.ac.glasgow.jagora.trader.impl.SimpleHistoricTraderBuilder;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

public class Experiment0001 {

	private World world;
	private Stock lemons;
	private StockExchange stockExchange;
	
	private SerialTickerTapeObserver tickerTapeObserver;
	
	private Long numberOfTraderActions = 10l;
	private Integer seed = 1;
	private int numberOfTraders = 1000;
	private Double initialTraderCash = 1000000.0;
	private TradingEngine engine;
	
	@Before
	public void setUp() throws Exception {
		world = new SimpleSerialWorld(numberOfTraderActions);
		lemons = new Stock("lemons");
		
		MarketFactory marketFactory = new ContinuousOrderDrivenMarketFactory();
		
		tickerTapeObserver = new SerialTickerTapeObserver();
		
		stockExchange = new DefaultStockExchange(world, tickerTapeObserver, marketFactory);

		Random r = new Random(seed);
		
		Trader dan = new StubTraderBuilder("stub", initialTraderCash)
			.addStock(lemons, 10).build();
		
		StockExchangeLevel1View danView = stockExchange.createLevel1View();
		danView.placeBuyOrder(new LimitBuyOrder(dan, lemons, 5, 10.01));
		danView.placeSellOrder(new LimitSellOrder(dan, lemons, 5, 9.99));
		
		Set<Level1Trader> traders = new HashSet<Level1Trader>();
		
		for (int i = 0 ; i < numberOfTraders ; i++){
			SimpleHistoricTrader historicTrader = 
				new SimpleHistoricTraderBuilder("trader["+i+"]",initialTraderCash, r.nextInt())
					.addStock(lemons, 1000)
					.build();
			
			stockExchange.createLevel1View().registerTradeListener(historicTrader);
			traders.add(historicTrader);
		}
		
		stockExchange.createLevel1View().registerTradeListener(new StdOutTickerTapeListener());
		
		engine = new SerialRandomEngineBuilder(world, seed)
			.addStockExchange(stockExchange)
			.addTraders(traders)
			.build();
	}
	
	@Test
	public void test() {
		engine.run();
	}

}
