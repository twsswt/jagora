package uk.ac.glasgow.jagora.experiment;

import static java.lang.String.format;
import static java.util.stream.IntStream.range;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.MarketFactory;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.pricer.impl.OldestOrderPricer;
import uk.ac.glasgow.jagora.test.stub.StubTradeListener;
import uk.ac.glasgow.jagora.ticker.impl.FilterOnDirectionOrderListener;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.ticker.impl.StdOutOrderListener;
import uk.ac.glasgow.jagora.ticker.impl.StdOutTradeListener;
import uk.ac.glasgow.jagora.trader.impl.ZIPTrader;
import uk.ac.glasgow.jagora.trader.impl.ZIPTraderBuilder;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

public class Experiment0003 {
	
	private Random random = new Random(1);

	private Stock lemons;
	private StockExchange stockExchange;
	
	private StubTradeListener stubTradeListener;
	private int numberOfBuyers = 50;
	private int numberOfSellers = 50;
	
	private List<ZIPTrader> traders;
	private SerialTickerTapeObserver tickerTapeObserver;
	
	@Before
	public void setUp() throws Exception {
		
		lemons = new Stock("lemons");
		
		World world = new SimpleSerialWorld(5000l);
		
		MarketFactory marketFactory = new ContinuousOrderDrivenMarketFactory(new OldestOrderPricer ());
		
		tickerTapeObserver = new SerialTickerTapeObserver();
		
		stubTradeListener = new StubTradeListener();
		tickerTapeObserver.registerTradeListener(stubTradeListener);

		//registerFilteredStdOutOrderListener(false);
		//registerFilteredStdOutOrderListener(true);
				
		tickerTapeObserver.registerTradeListener(new StdOutTradeListener());
		
		stockExchange = new DefaultStockExchange(world, tickerTapeObserver, marketFactory);
		
		traders = new ArrayList<ZIPTrader>();
		
		int [] buyerSeeds = range(0,numberOfBuyers).toArray();
				
		for (int seed : buyerSeeds)
			configureBuyerZIPTrader(seed, 5.0, 50.0);
		
		int [] sellerSeeds = range(0,numberOfSellers).toArray();
		
		for(int seed: sellerSeeds)
			configureSellerZIPTrader(seed, 5.0, 50.0);
		
		
	
	}

	@Test
	public void testAveragePrice() {
				
		while (stubTradeListener.getTradeCount() < numberOfBuyers)
			for (ZIPTrader zipTrader : traders) {
				zipTrader.speak(stockExchange.createLevel2View());
				stockExchange.doClearing();
			}

		assertThat(stubTradeListener.getAverageTradePrice(), closeTo(22.6, 0.1));
	}
	
	private void registerFilteredStdOutOrderListener(Boolean isOffer) {
		FilterOnDirectionOrderListener filteredOrderListener =
			new FilterOnDirectionOrderListener(new StdOutOrderListener(), isOffer);
		tickerTapeObserver.registerOrderListener(filteredOrderListener);
	}

	private void configureBuyerZIPTrader(int seed,	Double floorPrice, Double ceilPrice) {
				
		Double limitPrice = (random.nextDouble() * (ceilPrice - floorPrice)) + floorPrice;	
				
		ZIPTrader trader = configureBasicZIPTraderBuilder("buyer",seed)
			.addBuyOrderJob(lemons, floorPrice, limitPrice)
			.build();
		registerZIPTrader(trader);
	}

	private void configureSellerZIPTrader(int seed,	Double floorPrice, Double ceilPrice) {
		
		Double limitPrice = random.nextDouble() * (ceilPrice - floorPrice) + floorPrice;	
				
		ZIPTrader trader = configureBasicZIPTraderBuilder("seller",seed)
			.addStock(lemons, 1)
			.addSellOrderJob(lemons, limitPrice, ceilPrice)
			.build();
		registerZIPTrader(trader);
	}
	
	private ZIPTraderBuilder configureBasicZIPTraderBuilder(String namePrefix, int seed) {
		
		String name = format("%s_%d",namePrefix,seed);
		
		return new ZIPTraderBuilder(name)
			.setCash(1000.0)
			.setSeed(seed)
			.setMaximumAbsoluteChange(1.0)
			.setMaximumRelativeChange(0.1)
			.setLearningRate(0.1);
	}
	
	private void registerZIPTrader(ZIPTrader trader) {
		traders.add(trader);
		stockExchange.createLevel2View().registerOrderListener(trader);
		stockExchange.createLevel1View().registerTradeListener(trader);
	}

}
