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
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.pricer.LimitOrderTradePricer;
import uk.ac.glasgow.jagora.pricer.impl.OldestLimitOrderPricer;
import uk.ac.glasgow.jagora.ticker.StockExchangeObservable;
import uk.ac.glasgow.jagora.ticker.impl.OutputStreamOrderListener;
import uk.ac.glasgow.jagora.ticker.impl.OutputStreamTradeListener;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.AbstractTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.SafeAbstractTrader;
import uk.ac.glasgow.jagora.trader.impl.SimpleHistoricTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.random.RandomTraderBuilder;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

/**
 * A simple experiment to demonstrate price discovery and steady state behaviour of random agents.
 * @author tws
 *
 */
public class Experiment0001 {
		
	private TradingEngine engine;
	
	@Before
	public void setUp() throws Exception {
		
		Random r = new Random(1);
		
		World world =
			new SimpleSerialWorld(10000l);
		
		Stock lemons =
			new Stock("lemons");
		
		LimitOrderTradePricer limitOrderTradePricer =
			new OldestLimitOrderPricer();
		
		MarketFactory marketFactory = 
			new ContinuousOrderDrivenMarketFactory(limitOrderTradePricer);
		
		StockExchangeObservable stockExchangeObservable = 
			new SerialTickerTapeObserver();
		
		StockExchange stockExchange = 
			new DefaultStockExchange(world, stockExchangeObservable, marketFactory);
		
		Set<Trader> traders = new HashSet<Trader>();
		
		SimpleHistoricTraderBuilder simpleHistoricTraderBuilder = 
			new SimpleHistoricTraderBuilder()
				.setCash(1000l)
				.addStock(lemons, 5)
				.monitorStockExchange(stockExchange);
		
		/*range(0, 50).forEach(i -> traders.add(
				simpleHistoricTraderBuilder
					.setSeed(r.nextInt())
					.setName("trader["+i+"]")
					.build()));*/
		
		traders.add(
			new RandomTraderBuilder()
				.setCash(1000l)
				.addStock(lemons, 5000)
				.setSellOrderRange(lemons, 1, 2, -1l, 10l)
				.setBuyOrderRange (lemons, 1, 2, -9l, 2l)
				.setName("RandomTrader")
				.setRandom(new Random(r.nextInt()))
				.build());
		
		OutputStreamTradeListener tradeListener = 
			new OutputStreamTradeListener(System.out);
		
		OutputStreamOrderListener orderListener = 
			new OutputStreamOrderListener(System.out);
		
		stockExchangeObservable.registerTradeListener(tradeListener);
		stockExchangeObservable.registerOrderListener(orderListener);
		
		engine = new SerialRandomEngineBuilder()
			.setWorld(world)
			.setRandom(new Random(1))
			.addStockExchange(stockExchange)
			.addTradersStockExchangeView(traders, stockExchange)
			.build();
		
		Trader dan = new AbstractTraderBuilder(){

			public Trader build() {
				setName("stub");
				setCash(200l);
				addStock(lemons, 1);
				return new SafeAbstractTrader(getName(), getCash(), getInventory()){};
			}
			
		}.build();
		
	
		StockExchangeLevel1View danView = stockExchange.createLevel1View();
		
		danView.placeLimitBuyOrder(new DefaultLimitBuyOrder(dan, lemons, 1, 75l));
		danView.placeLimitSellOrder(new DefaultLimitSellOrder(dan, lemons, 1, 75l));
		
		stockExchange.doClearing();
		
		danView.placeLimitBuyOrder(new DefaultLimitBuyOrder(dan, lemons, 1, 100l));
		danView.placeLimitSellOrder(new DefaultLimitSellOrder(dan, lemons, 1, 50l));	}
	
	@Test
	public void test() {
		engine.run();
	}

}
