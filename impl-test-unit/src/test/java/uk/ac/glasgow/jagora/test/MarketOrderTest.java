package uk.ac.glasgow.jagora.test;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.MarketFactory;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.*;
import uk.ac.glasgow.jagora.pricer.impl.SellLimitOrderPricer;
import uk.ac.glasgow.jagora.test.stub.StubTrader;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;
import static org.junit.Assert.assertEquals;

public class MarketOrderTest {

	private static final Stock lemons = new Stock("lemons");

	private StubTrader alice;
	private StubTrader bruce;
	private StubTrader george;

	private DefaultStockExchange stockExchange;


	@Before
	public void setUp() throws Exception {

		alice =
			new StubTraderBuilder()
				.setName("alice")
				.setCash(50000l)
				.build();

		bruce =
			new StubTraderBuilder()
				.setName("bruce")
				.setCash(50000l)
				.addStock(lemons, 5000)
				.build();

		george =
			new StubTraderBuilder()
				.setName("george")
				.setCash(50000l)
				.addStock(lemons, 5000)
				.build();

		World world = new SimpleSerialWorld(1000l);
		MarketFactory marketFactory =
			new ContinuousOrderDrivenMarketFactory(new SellLimitOrderPricer());
		
		stockExchange = new DefaultStockExchange(world,	new SerialTickerTapeObserver(),	marketFactory);
	}

	@Test
	public void testMarketBuyOrder(){

		//produce two sell orders and make sure that market order is buying from the lower one
		LimitSellOrder sellOrder1 = new DefaultLimitSellOrder(bruce, lemons, 3000, 100l);
		bruce.supplyOrder(sellOrder1);
		bruce.speak(stockExchange.createLevel1View());

		LimitSellOrder sellOrder2 = new DefaultLimitSellOrder(george, lemons, 1000, 150l);
		george.supplyOrder(sellOrder2);
		george.speak(stockExchange.createLevel1View());

		MarketBuyOrder buyOrder = new MarketBuyOrder(alice, lemons, 100);
		alice.supplyOrder(buyOrder);
		alice.speak(stockExchange.createLevel1View());

		stockExchange.doClearing();

		assertEquals( "", 40000l, alice.getCash().longValue());

		assertEquals("", 100, alice.getInventory(lemons).intValue());

		assertEquals("", 4900, bruce.getInventory(lemons).intValue());

		assertEquals("", 5000, george.getInventory(lemons).intValue());

	}

	@Test
	public void testMarketSellOrder (){
		
		LimitBuyOrder limitBuyOrder1 = new DefaultLimitBuyOrder(alice, lemons, 100, 100l);
		alice.supplyOrder(limitBuyOrder1);
		alice.speak(stockExchange.createLevel1View());

		LimitBuyOrder limitBuyOrder2 = new DefaultLimitBuyOrder(george, lemons, 100, 50l);
		george.supplyOrder(limitBuyOrder2);
		george.speak(stockExchange.createLevel1View());

		MarketSellOrder marketSellOrder = new MarketSellOrder(bruce, lemons, 150);
		bruce.supplyOrder(marketSellOrder);
		bruce.speak(stockExchange.createLevel1View());

		stockExchange.doClearing();

		assertEquals("", 62500l, bruce.getCash().longValue() );
		assertEquals("", 4850, bruce.getInventory(lemons).intValue());

		assertEquals("", alice.getInventory(lemons), (Integer) 100);
	}


	@Test
	public void testTwoMarketOrders() {
		
		// Establish a spread on the market first.

		MarketBuyOrder buyOrder = new MarketBuyOrder(alice, lemons, 100);
		alice.supplyOrder(buyOrder);
		alice.speak(stockExchange.createLevel1View());

		stockExchange.doClearing();

		assertEquals("", alice.getCash(),(Long) 50000l);

		MarketSellOrder sellOrder = new MarketSellOrder(bruce, lemons, 150);
		bruce.supplyOrder(sellOrder);
		bruce.speak(stockExchange.createLevel1View());

		stockExchange.doClearing();

		assertEquals("", 50000l, alice.getCash().longValue());
		assertEquals("", 0, alice.getInventory(lemons).intValue());
		assertEquals("", 5000, bruce.getInventory(lemons).intValue()); //no trade should occur

	}


	@Test
	public void testMarketOrderWhenBetterPrice() {
		
		MarketSellOrder limitSellOrder = new MarketSellOrder(bruce, lemons, 150);
		bruce.supplyOrder(limitSellOrder);
		bruce.speak(stockExchange.createLevel1View());

		stockExchange.doClearing();

		LimitSellOrder sellOrder1 = new DefaultLimitSellOrder(george, lemons, 50, 150l);
		george.supplyOrder(sellOrder1);
		george.speak(stockExchange.createLevel1View());

		LimitBuyOrder buyOrder1 = new DefaultLimitBuyOrder(alice, lemons, 100, 100l);
		alice.supplyOrder(buyOrder1);
		alice.speak(stockExchange.createLevel1View());


		stockExchange.doClearing();
		//Test to see if marketSellOrder price is updated and the order is able to be executed
		assertEquals("", 4900, bruce.getInventory(lemons).intValue());
		assertEquals("", 40000l, alice.getCash().longValue());
	}


	//Two market sell orders are supplied and the first one should be executed and only after the second one as well
	@Test
	public void testTwoMarketSellOrders(){

		MarketSellOrder sellOrder1 = new MarketSellOrder(bruce,lemons,100);
		bruce.supplyOrder(sellOrder1);
		bruce.speak(stockExchange.createLevel1View());

		MarketSellOrder sellOrder2 = new MarketSellOrder(george,lemons,50);
		george.supplyOrder(sellOrder2);
		george.speak(stockExchange.createLevel1View());

		LimitBuyOrder buyOrder1 = new DefaultLimitBuyOrder(alice,lemons,50,50l);
		alice.supplyOrder(buyOrder1);
		alice.speak(stockExchange.createLevel1View());

		stockExchange.doClearing();

		assertEquals("",bruce.getInventory(lemons),(Integer) (5000-50));
		assertEquals("",bruce.getCash(), (Long) (50000l + 50l*50l));

		LimitBuyOrder buyOrder2 = new DefaultLimitBuyOrder(alice,lemons,70,60l);
		alice.supplyOrder(buyOrder2);
		alice.speak(stockExchange.createLevel1View());

		stockExchange.doClearing();

		assertEquals("",bruce.getInventory(lemons),(Integer) (5000-50 - 50));
		assertEquals("",bruce.getCash(), (Long) (50000l + 50l*50l + 60l*50l));

		assertEquals("",george.getInventory(lemons),(Integer) (5000-20));
		assertEquals("",george.getCash(), (Long) (50000l + 20l*60l));




	}
}
