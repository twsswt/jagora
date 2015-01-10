package uk.ac.glasgow.jagora.test.unit;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.ExecutedTrade;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.Trader;
import uk.ac.glasgow.jagora.World;
import uk.ac.glasgow.jagora.orderdriven.ContinuousOrderDrivenStockExchange;
import uk.ac.glasgow.jagora.orderdriven.impl.OrderDrivenStockExchangeImpl;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.trader.RandomTraderBuilder;
import uk.ac.glasgow.jagora.trader.SimpleHistoricTrader;
import uk.ac.glasgow.jagora.trader.SimpleHistoricTraderBuilder;
import uk.ac.glasgow.jagora.world.SimpleSerialWorld;

public class SimpleHistoricTraderTest {
	
	private final Integer numberOfTraderActions =  1000;
	private final Double initialTraderCash = 1000000.00;
	private final Integer initialNumberOfLemons = 10000;
	private final Integer seed = 1;

	private Stock lemons;
	private ContinuousOrderDrivenStockExchange marketForLemons;
	
	private SimpleHistoricTrader alice;
	private Trader bob;
	private Trader charlie;
	
	private Trader dan;
	
	private World world;
	

	@Before
	public void setUp() throws Exception {
		world = new SimpleSerialWorld();
		lemons = new Stock("lemons");
		marketForLemons = new OrderDrivenStockExchangeImpl(world);

		alice = new SimpleHistoricTraderBuilder("alice",initialTraderCash, seed)
			.addStock(lemons, initialNumberOfLemons)
			.build();
		
		bob = new RandomTraderBuilder("bob", initialTraderCash, seed)
			.addStock(lemons, initialNumberOfLemons)
			.addTradeRange(lemons, 0.1, -.1, 0, 100)
			.build();
		
		charlie = new RandomTraderBuilder("charlie", initialTraderCash, seed)
			.addStock(lemons, initialNumberOfLemons)
			.addTradeRange(lemons, 0.1, -.1, 0, 100)
			.build();
		
		dan = new StubTraderBuilder("dan", initialTraderCash)
			.addStock(lemons, 10).build();
		
		marketForLemons.createTraderStockExchangeView().addTicketTapeListener(alice, lemons);
	}

	@Test
	public void test() {
		//Create initial market conditions
		BuyOrder seedBuyOrder = new BuyOrder(dan, lemons, 10, 5.0);
		marketForLemons.createTraderStockExchangeView().placeBuyOrder(seedBuyOrder);
		SellOrder seedSellOrder = new SellOrder(dan, lemons, 10, 5.0);
		marketForLemons.createTraderStockExchangeView().placeSellOrder(seedSellOrder);
		
		//Allow two random traders to create a liquid market.
		for (Integer i = 0; i < numberOfTraderActions/2; i++){
			bob.speak(marketForLemons.createTraderStockExchangeView());
			charlie.speak(marketForLemons.createTraderStockExchangeView());
			marketForLemons.doClearing();
		}
		
		//Alice now participates.
		for (Integer i = 0; i < numberOfTraderActions/2; i++){
			bob.speak(marketForLemons.createTraderStockExchangeView());
			marketForLemons.doClearing();
			charlie.speak(marketForLemons.createTraderStockExchangeView());
			marketForLemons.doClearing();
			alice.speak(marketForLemons.createTraderStockExchangeView());
			marketForLemons.doClearing();
		}		
	
		Double averageLemonPrice = 0.0;
		Integer totalTradeQuantity = 0;

		for (ExecutedTrade executedTrade : marketForLemons.getTradeHistory(lemons)){
			Trade trade = executedTrade.event;
			averageLemonPrice =
				(averageLemonPrice * totalTradeQuantity + trade.price * trade.quantity) / 
				(totalTradeQuantity + trade.quantity);
			totalTradeQuantity += trade.quantity;
		}
		Double traderInitialEquity = initialTraderCash + averageLemonPrice * initialNumberOfLemons;
		
		Double alicesFinalEquity = alice.getCash() + alice.getInventory(lemons) * averageLemonPrice;

		assertThat("", alicesFinalEquity, greaterThan(traderInitialEquity));
	}
}
