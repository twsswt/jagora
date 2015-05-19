package uk.ac.glasgow.jagora.trader.test;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.MarketFactory;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.pricer.impl.SellOrderPricer;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.impl.RandomTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.SimpleHistoricTrader;
import uk.ac.glasgow.jagora.trader.impl.SimpleHistoricTraderBuilder;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

public class SimpleHistoricTraderTest {
	
	private final Integer numberOfTraderActions = 1000;
	private final Double initialTraderCash = 1000000.00;
	private final Integer initialNumberOfLemons = 10000;
	private final Integer seed = 1;

	private Stock lemons;
	private StockExchange stockExchange;
	
	private SimpleHistoricTrader alice;
	private Level1Trader bob;
	private Level1Trader charlie;
	
	private Level1Trader dan;
	
	private World world;
	
	private SerialTickerTapeObserver tickerTapeObserver;

	@Before
	public void setUp() throws Exception {
		world = new SimpleSerialWorld(numberOfTraderActions*5l);
		lemons = new Stock("lemons");
		
		MarketFactory marketFactory = new ContinuousOrderDrivenMarketFactory(new SellOrderPricer());
		
		tickerTapeObserver = new SerialTickerTapeObserver();
		
		stockExchange = new DefaultStockExchange(world, tickerTapeObserver, marketFactory);

		alice = new SimpleHistoricTraderBuilder("alice",initialTraderCash, seed)
			.addStock(lemons, initialNumberOfLemons)
			.build();
		
		bob = new RandomTraderBuilder("bob", initialTraderCash, seed)
			.addStock(lemons, initialNumberOfLemons)
			.setTradeRange(lemons, 1, 100, -.1, +.1, -.1, +.1)
			.build();
		
		charlie = new RandomTraderBuilder("charlie", initialTraderCash, seed)
			.addStock(lemons, initialNumberOfLemons)
			.setTradeRange(lemons, 1, 100, -.1, +.1, -.1, +.1)
			.build();
		
		dan = new StubTraderBuilder("dan", initialTraderCash)
			.addStock(lemons, 10).build();
		
		stockExchange.createLevel1View().registerTradeListener(alice);
	}

	@Test
	public void test() {
		
		//Create initial market conditions
		BuyOrder seedBuyOrder = new LimitBuyOrder(dan, lemons, 10, 5.0);
		stockExchange.createLevel1View().placeBuyOrder(seedBuyOrder);
		SellOrder seedSellOrder = new LimitSellOrder(dan, lemons, 10, 5.0);
		stockExchange.createLevel1View().placeSellOrder(seedSellOrder);
		
		//Allow two random traders to create a liquid market.
		for (Integer i = 0; i < numberOfTraderActions/2; i++){
			bob.speak(stockExchange.createLevel1View());
			charlie.speak(stockExchange.createLevel1View());
			stockExchange.doClearing();
			
			StockExchangeLevel1View xyz = stockExchange.createLevel1View();
		}
		
		//Alice now participates.
		for (Integer i = 0; i < numberOfTraderActions/2; i++){
			bob.speak(stockExchange.createLevel1View());
			stockExchange.doClearing();
			charlie.speak(stockExchange.createLevel1View());
			stockExchange.doClearing();
			alice.speak(stockExchange.createLevel1View());
			stockExchange.doClearing();
		}		
			
		List<TickEvent<Trade>> executedTrades = tickerTapeObserver.getTradeHistory(lemons);
		
		assertThat(executedTrades.size(), greaterThan(0));
		
		List<TickEvent<Trade>> aliceSellTrades = 
			executedTrades.stream()
			.filter(executedTrade -> executedTrade.event.getSeller().equals(alice))
			.collect(Collectors.toList());
		
		List<TickEvent<Trade>> aliceBuyTrades = 
				executedTrades.stream()
				.filter(executedTrade -> executedTrade.event.getBuyer().equals(alice))
				.collect(Collectors.toList());
				
		Double averageLemonPrice = 
			executedTrades.stream()
				.mapToDouble(executedTrade->executedTrade.event.getPrice())
				.average()
				.getAsDouble();
		
		Double aliceSellAveragePrice = 
			aliceSellTrades.stream()
				.mapToDouble(executedTrade->executedTrade.event.getPrice())
				.average()
				.getAsDouble();
		
		Double aliceBuyAveragePrice = 
				aliceBuyTrades.stream()
					.mapToDouble(executedTrade->executedTrade.event.getPrice())
					.average()
					.getAsDouble();
		
		assertThat("", aliceSellAveragePrice, greaterThan(averageLemonPrice));

		assertThat("", aliceBuyAveragePrice, lessThan(averageLemonPrice));
	}
}
