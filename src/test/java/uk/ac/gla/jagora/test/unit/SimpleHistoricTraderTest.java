package uk.ac.gla.jagora.test.unit;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.gla.jagora.ExecutedTrade;
import uk.ac.gla.jagora.Order;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.Trade;
import uk.ac.gla.jagora.Trader;
import uk.ac.gla.jagora.World;
import uk.ac.gla.jagora.orderdriven.OrderDrivenStockExchange;
import uk.ac.gla.jagora.orderdriven.impl.OrderDrivenStockExchangeImpl;
import uk.ac.gla.jagora.trader.RandomTraderBuilder;
import uk.ac.gla.jagora.trader.SimpleHistoricTrader;
import uk.ac.gla.jagora.trader.SimpleHistoricTraderBuilder;
import uk.ac.gla.jagora.world.SimpleSerialWorld;

public class SimpleHistoricTraderTest {
	
	private final Integer numberOfTraderActions = 10000;
	private final Double initialTraderCash = 1000000.00;
	private final Integer initialNumberOfLemons = 10000;
	
	private Stock lemons;
	private OrderDrivenStockExchange marketForLemons;
	
	private SimpleHistoricTrader alice;
	private Trader bob;
	private Trader charlie;
	private World world;
	

	@Before
	public void setUp() throws Exception {
		world = new SimpleSerialWorld();
		lemons = new Stock("lemons");
		marketForLemons = new OrderDrivenStockExchangeImpl(world);

		alice = new SimpleHistoricTraderBuilder("alice",initialTraderCash,1)
			.addStock(lemons, initialNumberOfLemons)
			.build();
		
		bob = new RandomTraderBuilder("bob", initialTraderCash,1)
			.addStock(lemons, initialNumberOfLemons)
			.addTradeRange(lemons, 0.0, 10.0, 0, 100)
			.build();
		
		charlie = new RandomTraderBuilder("charlie", initialTraderCash,1)
			.addStock(lemons, initialNumberOfLemons)
			.addTradeRange(lemons, 0.0, 10.0, 0, 100)
			.build();
		
		marketForLemons.createTraderStockExchangeView().addTicketTapeListener(alice, lemons);
	}

	@Test
	public void test() {
				
		//Allow two random traders to create a liquid market.
		for (Integer i = 0; i < numberOfTraderActions/2; i++){
			bob.speak(marketForLemons.createTraderStockExchangeView());
			marketForLemons.doClearing();
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
			Trade trade = executedTrade.trade;
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
