package uk.ac.glasgow.jagora.experiment;

import static java.lang.String.format;
import static java.util.stream.IntStream.range;
import static uk.ac.glasgow.jagora.experiment.ExperimentalReportsPathsUtil.experimentalPricesDatFilePath;

import java.io.FileOutputStream;
import java.io.PrintStream;
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
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.pricer.LimitOrderTradePricer;
import uk.ac.glasgow.jagora.pricer.impl.OldestLimitOrderPricer;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.AbstractTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.InstitutionalInvestorTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.SafeAbstractTrader;
import uk.ac.glasgow.jagora.trader.impl.random.RandomTraderBuilder;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

/**
 * Demonstrates random traders establishing a price
 * equilibrium and then responding to a large buy order by
 * increasing the equilibrium price.
 * 
 * @author Tim
 *
 */
public class Experiment0002 {
		

	private final String pricesDatFilePath = experimentalPricesDatFilePath(this.getClass());
		
	private World world;
	private Stock lemons;
	private StockExchange stockExchange;
	
	private SerialTickerTapeObserver tickerTapeObserver;
	
	private TradingEngine engine;
	
	@Before
	public void setUp() throws Exception {

		Random r = new Random(1);

		lemons = new Stock("lemons");
		
		world = new SimpleSerialWorld(2000000l);
		
		LimitOrderTradePricer limitOrderTradePricer = new OldestLimitOrderPricer ();
		
		MarketFactory marketFactory = new ContinuousOrderDrivenMarketFactory(limitOrderTradePricer);
		
		tickerTapeObserver = new SerialTickerTapeObserver();
		
		stockExchange = new DefaultStockExchange(world, tickerTapeObserver, marketFactory);

		Set<Trader> traders = new HashSet<Trader>();
						
		Trader dan = new AbstractTraderBuilder(){

			public Trader build() {
				setName("dan");
				setCash(200l);
				addStock(lemons, 1);
				return new SafeAbstractTrader(getName(), getCash(), getInventory()){};
			}
			
		}.build();
		
		StockExchangeLevel1View stubsView = stockExchange.createLevel1View();
		stubsView.placeLimitBuyOrder(new DefaultLimitBuyOrder(dan, lemons, 1, 99l));
		stubsView.placeLimitSellOrder(new DefaultLimitSellOrder(dan, lemons, 1, 101l));		

		RandomTraderBuilder randomTraderBuilder = 
			new RandomTraderBuilder()
				.setCash(200l)
				.addStock(lemons, 1)
				.setSellOrderRange(lemons, 1, 2, -1l, 10l)
				.setBuyOrderRange (lemons, 1, 2, -9l, 2l);
		
		range(0, 49).forEach(i -> 
			traders.add(
				randomTraderBuilder
				.setName(format("RandomTrader[%d]", i))
				.setSeed(r.nextInt())
				.build()));

		Level1Trader institutionalInvestorTrader = 
			new InstitutionalInvestorTraderBuilder()
			.setName("InstitutionalInvestorTrader")
			.setCash(100000l)
			.addScheduledLimitBuyOrder(1000000l, world, lemons, 25, 400l)
			.build();
		traders.add(institutionalInvestorTrader);


		PrintStream printStream = new PrintStream(new FileOutputStream(pricesDatFilePath));
		
		GnuPlotPriceDATLogger gnuPlotPriceDATLogger = new GnuPlotPriceDATLogger(printStream);
		
		tickerTapeObserver.registerTradeListener(gnuPlotPriceDATLogger);
		tickerTapeObserver.registerOrderListener(gnuPlotPriceDATLogger);
		
		engine = new SerialRandomEngineBuilder()
			.setWorld(world)
			.setSeed(1)
			.addStockExchange(stockExchange)
			.addTradersStockExchangeView(traders,stockExchange)
			.build();
	}
	
	@Test
	public void test() {
		engine.run();

	}

}
