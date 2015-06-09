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
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.pricer.impl.OldestOrderPricer;
import uk.ac.glasgow.jagora.test.stub.StubTradeListener;
import uk.ac.glasgow.jagora.ticker.impl.FilterOnDirectionOrderListener;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.ticker.impl.StdOutOrderListener;
import uk.ac.glasgow.jagora.ticker.impl.StdOutTradeListener;
import uk.ac.glasgow.jagora.trader.impl.zip.ZIPTrader;
import uk.ac.glasgow.jagora.trader.impl.zip.ZIPTraderBuilder;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

/**
 * Reproduce's Cliff (1997)'s experimental results for Zero Intelligence Traders.
 * @author Tim
 *
 */
public class Experiment0003 {
	
	private final Integer seed = 1;
	
	private final Long maxTickCount = 5000000l;
	private final Integer numberOfBuyers = 100;
	private final Integer numberOfSellers = 50;
	
	private final Double maximumRelativeChange = 0.05;
	private final Long maximumAbsoluteChange = 5l;
	
	private final Double learningRate = 0.3;
	
	private final Double momentum = 0.9;
	
	private final Long buyerTraderCash = 10000000l;
	private final Integer jobQuantity = 500;
	
	private final Long maxPrice = 5000l;
	private final Long minPrice = 0l;
	
	private final Long sellerMinLimitPrice = 0l;
	private final Long buyerMaxBidPrice = 5000l;

	//
	
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
			configureBuyerZIPTrader(seed, sellerMinLimitPrice, maxPrice);
		
		int [] sellerSeeds = range(0,numberOfSellers).toArray();
		
		for(Integer seed: sellerSeeds)
			configureSellerZIPTrader(seed, minPrice, buyerMaxBidPrice);
		
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
			.setCash(buyerTraderCash)
			.addBuyOrderJobSpecification(lemons, floorPrice, limitPrice, jobQuantity)
						.build();
		
		registerZIPTrader(trader);
	}

	private void configureSellerZIPTrader(int seed,	Long floorPrice, Long ceilPrice) {
		
		Long limitPrice = (long)(random.nextDouble() * (ceilPrice - floorPrice)) + floorPrice;	
				
		ZIPTrader trader = configureBasicZIPTraderBuilder("seller",seed)
			.addStock(lemons, jobQuantity)
			.addSellOrderJobSpecification(lemons, limitPrice, ceilPrice, jobQuantity)
			.build();
		
		registerZIPTrader(trader);
	}
	
	private ZIPTraderBuilder configureBasicZIPTraderBuilder(String namePrefix, int seed) {
		
		String name = format("%s_%d",namePrefix,seed);
		
		return new ZIPTraderBuilder(name)
			.setSeed(seed)
			.setMaximumAbsoluteChange(maximumAbsoluteChange)
			.setMaximumRelativeChange(maximumRelativeChange)
			.setLearningRate(learningRate)
			.setMomentum(momentum);
	}
	
	private void registerZIPTrader(ZIPTrader trader) {
		traders.add(trader);
		StockExchangeLevel2View level2View = stockExchange.createLevel2View();
		level2View.registerOrderListener(trader);
		level2View.registerTradeListener(trader);
	}
	
	@Test
	public void runExperiment() {
				
		while (world.isAlive()){	
			ZIPTrader zipTrader = random.chooseElement(traders);
			zipTrader.speak(stockExchange.createLevel2View());
			stockExchange.doClearing();
			//System.out.println(stockExchange.createLevel1View());
		}
	}

}
