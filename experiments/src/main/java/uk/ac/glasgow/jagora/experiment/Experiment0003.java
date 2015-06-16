package uk.ac.glasgow.jagora.experiment;

import static java.lang.String.format;
import static java.util.Collections.shuffle;
import static java.util.stream.IntStream.range;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;
import static uk.ac.glasgow.jagora.experiment.MarketCalculationsUtil.calculateEquilibriumPrice;

import java.io.File;
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
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.pricer.impl.OldestOrderPricer;
import uk.ac.glasgow.jagora.test.stub.StubTradeListener;
import uk.ac.glasgow.jagora.ticker.impl.FilterOnDirectionOrderListener;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.ticker.impl.OutputStreamOrderListener;
import uk.ac.glasgow.jagora.ticker.impl.StdOutTradeListener;
import uk.ac.glasgow.jagora.trader.impl.zip.ZIPTrader;
import uk.ac.glasgow.jagora.trader.impl.zip.ZIPTraderBuilder;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

/**
 * Reproduce's Cliff (1997)'s experimental results for Zero Intelligence Traders.
 * @author Tim
 *
 */
public class Experiment0003 {
	
	// Experimental constants

	private final Integer seed = 1;
	private final Integer permittedError = 100;

	private final Long maxTickCount = 500000l;
	private final Double priceAveragingPortion = 0.5;
	
	private final Double maximumRelativePriceChange = 0.05;
	private final Long maximumAbsolutePriceChange = 5l;
	
	private final Double maxLearningRate = 0.5;
	private final Double minLearningRate = 0.1;
	
	private final Double maxMomentum = 0.8;
	private final Double minMomentum = 0.2;
	
	private final Long buyerTraderCash = 100000000l;
	private final Integer jobQuantity = 1000;
	
	private final Long maxPrice = 5000l;
	private final Long minPrice = 0l;
	
	private final Long minOfferLimit = 0l;
	private final Long maxBidLimit = 5000l;
	
	private final String datFilePath = "reports/jagora/prices.dat";
	
	// Experimental parameters.
	
	private final Integer numberOfBuyers = 100;
	private final Integer numberOfSellers = 50;
	
	// Experimental fixture
	
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
		
		createStockExchange();
		
		createTraders();

	}

	private void createTraders() {
		
		traders = new ArrayList<ZIPTrader>();
		
		range(0,numberOfBuyers)
			.forEach(seed -> configureBuyerZIPTrader(seed, minOfferLimit, maxPrice));
				
		range(0,numberOfSellers)
			.forEach(seed -> configureSellerZIPTrader(seed, minPrice, maxBidLimit));
		
		shuffle(traders);
	}

	private void createStockExchange() throws FileNotFoundException {
		world = new SimpleSerialWorld(maxTickCount);
		
		MarketFactory marketFactory = new ContinuousOrderDrivenMarketFactory(new OldestOrderPricer ());
		
		configureTickerTapeObserver();
		
		stockExchange = new DefaultStockExchange(world, tickerTapeObserver, marketFactory);
	}

	private void configureTickerTapeObserver() throws FileNotFoundException {

		tickerTapeObserver = new SerialTickerTapeObserver();
		
		stubTradeListener = new StubTradeListener();
		tickerTapeObserver.registerTradeListener(stubTradeListener);

		registerFilteredStdOutOrderListener(false);
		registerFilteredStdOutOrderListener(true);
				
		tickerTapeObserver.registerTradeListener(new StdOutTradeListener());
		
		File datFile = new File(datFilePath);
		datFile.getParentFile().mkdirs();
		
		PrintStream printStream = new PrintStream(new FileOutputStream(datFile));
		
		PriceTimeLoggerTickerTapeListener priceTimeLogger = 
			new PriceTimeLoggerTickerTapeListener(printStream);
		
		tickerTapeObserver.registerTradeListener(priceTimeLogger);
		tickerTapeObserver.registerOrderListener(priceTimeLogger);
	}
	
	private void registerFilteredStdOutOrderListener(Boolean isOffer) {
		FilterOnDirectionOrderListener filteredOrderListener =
			new FilterOnDirectionOrderListener(new OutputStreamOrderListener(System.out), isOffer);
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
		
		Double learningRate = createRandomDouble(minLearningRate, maxLearningRate);
		Double momentum = createRandomDouble(minMomentum, maxMomentum);
		
		return new ZIPTraderBuilder(name)
			.setSeed(seed)
			.setMaximumAbsoluteChange(maximumAbsolutePriceChange)
			.setMaximumRelativeChange(maximumRelativePriceChange)
			.setLearningRate(learningRate)
			.setMomentum(momentum)
			.setMinInitialProfit(0.05)
			.setMaxInitialProfit(0.35);
	}

	private Double createRandomDouble(Double min, Double max) {
		return random.nextDouble() * (max - min) +min;
	}
	
	private void registerZIPTrader(ZIPTrader trader) {
		traders.add(trader);
		StockExchangeLevel2View level2View = stockExchange.createLevel2View();
		level2View.registerOrderListener(trader);
		level2View.registerTradeListener(trader);
	}
	
	@Test
	public void runExperiment() {
				
		Double expectedEquilibriumPrice = 
			calculateEquilibriumPrice(maxPrice, minPrice, maxBidLimit, minOfferLimit, numberOfBuyers, numberOfSellers); 
						
		while (world.isAlive()){	
			ZIPTrader zipTrader = random.chooseElement(traders);
			zipTrader.speak(stockExchange.createLevel2View());
			stockExchange.doClearing();
		}
		
		List<TickEvent<Trade>> tradeEvents = tickerTapeObserver.getTradeHistory(lemons);
		
		Double averagePrice = 
			tradeEvents
				.stream()
				.filter(tradeEvent -> tradeEvent.tick > maxTickCount * priceAveragingPortion)
				.mapToLong(tradeEvent -> tradeEvent.event.getPrice())
				.average()
				.orElse(0.0);
		
		assertThat (averagePrice, closeTo(expectedEquilibriumPrice, permittedError));		
	}

}
 