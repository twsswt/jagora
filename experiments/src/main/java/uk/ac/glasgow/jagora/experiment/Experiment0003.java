package uk.ac.glasgow.jagora.experiment;

import static java.lang.String.format;
import static java.util.stream.IntStream.range;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.ac.glasgow.jagora.util.Random;

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

/**
 * Reproduce's Cliff (1997)'s experimental results for Zero Intelligence Traders.
 * @author Tim
 *
 */
public class Experiment0003 {
	
	private Long maxTickCount = 5000l;
	private Integer numberOfBuyers = 50000;
	private Integer numberOfSellers = 50000;
	private Integer seed =  1;
	
	private Double maximumRelativeChange = 0.1;
	private Long maximumAbsoluteChange = 1l;
	
	private Double learningRate = 0.1;
	
	
	private Random random;
	
	private Stock lemons;
	
	private StockExchange stockExchange;
	
	private StubTradeListener stubTradeListener;
	
	private List<ZIPTrader> traders;
	private SerialTickerTapeObserver tickerTapeObserver;
	private World world;
	
	@Before
	public void setUp() throws Exception {
		
		random = new Random(seed);
		
		lemons = new Stock("lemons");
		
		stockExchange = createStockExchange();
		
		traders = new ArrayList<ZIPTrader>();
		
		int [] buyerSeeds = range(0,numberOfBuyers).toArray();
				
		for (Integer seed : buyerSeeds)
			configureBuyerZIPTrader(seed, 0l, 5000l);
		
		int [] sellerSeeds = range(0,numberOfSellers).toArray();
		
		for(Integer seed: sellerSeeds)
			configureSellerZIPTrader(seed, 0l, 5000l);
		
		Collections.shuffle(traders);

	}

	private StockExchange createStockExchange() throws FileNotFoundException {
		world = new SimpleSerialWorld(maxTickCount);
		
		MarketFactory marketFactory = new ContinuousOrderDrivenMarketFactory(new OldestOrderPricer ());
		
		tickerTapeObserver = new SerialTickerTapeObserver();
		
		stubTradeListener = new StubTradeListener();
		tickerTapeObserver.registerTradeListener(stubTradeListener);

		//registerFilteredStdOutOrderListener(false);
		//registerFilteredStdOutOrderListener(true);
				
		tickerTapeObserver.registerTradeListener(new StdOutTradeListener());
		
		PrintStream printStream = new PrintStream(new FileOutputStream("prices.txt"));
		
		tickerTapeObserver.registerTradeListener(
			new PriceTimeLoggerTickerTapeListener(printStream));

		
		return new DefaultStockExchange(world, tickerTapeObserver, marketFactory);
	}
	
	private void registerFilteredStdOutOrderListener(Boolean isOffer) {
		FilterOnDirectionOrderListener filteredOrderListener =
			new FilterOnDirectionOrderListener(new StdOutOrderListener(), isOffer);
		tickerTapeObserver.registerOrderListener(filteredOrderListener);
	}

	private void configureBuyerZIPTrader(int seed, Long floorPrice, Long ceilPrice) {
				
		Long limitPrice = (long)(random.nextDouble() * (ceilPrice - floorPrice)) + floorPrice;	
				
		ZIPTrader trader = configureBasicZIPTraderBuilder("buyer",seed)
			.addBuyOrderJob(lemons, floorPrice, limitPrice)
			.build();
		
		registerZIPTrader(trader);
	}

	private void configureSellerZIPTrader(int seed,	Long floorPrice, Long ceilPrice) {
		
		Long limitPrice = (long)(random.nextDouble() * (ceilPrice - floorPrice)) + floorPrice;	
				
		ZIPTrader trader = configureBasicZIPTraderBuilder("seller",seed)
			.addStock(lemons, 1)
			.addSellOrderJob(lemons, limitPrice, ceilPrice)
			.build();
		
		registerZIPTrader(trader);
	}
	
	private ZIPTraderBuilder configureBasicZIPTraderBuilder(String namePrefix, int seed) {
		
		String name = format("%s_%d",namePrefix,seed);
		
		return new ZIPTraderBuilder(name)
			.setCash(1000000l)
			.setSeed(seed)
			.setMaximumAbsoluteChange(maximumAbsoluteChange)
			.setMaximumRelativeChange(maximumRelativeChange)
			.setLearningRate(learningRate);
	}
	
	private void registerZIPTrader(ZIPTrader trader) {
		traders.add(trader);
		stockExchange.createLevel2View().registerOrderListener(trader);
		stockExchange.createLevel1View().registerTradeListener(trader);
	}
	
	@Test
	public void testAveragePrice() {
				
		while (world.isAlive()){	
			ZIPTrader zipTrader = random.chooseElement(traders);
			zipTrader.speak(stockExchange.createLevel2View());
			stockExchange.doClearing();
		}

		//assertThat(stubTradeListener.getAverageTradePrice(), closeTo(22.6, 0.1));
	}

}
