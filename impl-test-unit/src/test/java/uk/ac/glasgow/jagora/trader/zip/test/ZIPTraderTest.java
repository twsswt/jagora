package uk.ac.glasgow.jagora.trader.zip.test;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.MarketFactory;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.experiment.StdOutTradeListener;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.pricer.impl.OldestOrderPricer;
import uk.ac.glasgow.jagora.test.stub.StubTradeListener;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.trader.zip.impl.ZIPTrader;
import uk.ac.glasgow.jagora.trader.zip.impl.ZIPTraderBuilder;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

public class ZIPTraderTest {
		
	private Stock lemons;
	private StockExchange stockExchange;
	
	private StubTradeListener stubTradeListener;
	private int numberOfCounterParties = 500;
	
	private List<ZIPTrader> traders = new ArrayList<ZIPTrader>();
	
	@Before
	public void setUp() throws Exception {
		
		lemons = new Stock("lemons");
		
		World world = new SimpleSerialWorld(5000l);
		
		MarketFactory marketFactory = new ContinuousOrderDrivenMarketFactory(new OldestOrderPricer ());
		
		SerialTickerTapeObserver tickerTapeObserver = new SerialTickerTapeObserver();
		tickerTapeObserver.registerTradeListener(stubTradeListener = new StubTradeListener());
		//tickerTapeObserver.registerOrderListener(new StdOutOrderListener());
		tickerTapeObserver.registerTradeListener(new StdOutTradeListener());
		stockExchange = new DefaultStockExchange(world, tickerTapeObserver, marketFactory);
		
		for (int i = 0 ; i < numberOfCounterParties ; i ++){
		
			ZIPTrader buyer = new ZIPTraderBuilder("buyer"+i)
				.setCash(1000.0)
				.setSeed(i)
				.setMaximumAbsoluteChange(0.0)
				.setMaximumRelativeChange(0.1)
				.setLearningRate(0.1)
				.addBuyOrderJob(lemons, 0.0, 25.0)
				.build();
				
			ZIPTrader seller = new ZIPTraderBuilder("seller"+i)
				.setCash(1000.0)
				.addStock(lemons, 1)
				.setSeed(i)
				.setMaximumAbsoluteChange(0.0)
				.setMaximumRelativeChange(0.1)
				.setLearningRate(0.1)
				.addSellOrderJob(lemons, 20.0, 30.0)
				.build();
		
		
			//Register the buyers and sellers with the market.
			stockExchange.createLevel2View().registerOrderListener(buyer);
			stockExchange.createLevel2View().registerOrderListener(seller);
			stockExchange.createLevel1View().registerTradeListener(buyer);
			stockExchange.createLevel1View().registerTradeListener(seller);
			traders.add(buyer);
			traders.add(seller);
			
		}

	
	}

	@Test
	public void testAveragePrice() {
				
		while (stubTradeListener.getTradeCount() < numberOfCounterParties)
			for (ZIPTrader zipTrader : traders) {
				zipTrader.speak(stockExchange.createLevel2View());
				stockExchange.doClearing();
			}

		assertThat(stubTradeListener.getAverageTradePrice(), closeTo(22.6, 0.1));
	}

}
