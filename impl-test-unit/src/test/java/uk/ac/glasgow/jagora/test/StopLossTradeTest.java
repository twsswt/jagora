package uk.ac.glasgow.jagora.test;


import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.impl.StopLossSellOrder;
import uk.ac.glasgow.jagora.pricer.impl.SellOrderPricer;
import uk.ac.glasgow.jagora.test.stub.StubTrader;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.impl.StopLossBuyOrder;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;
import static org.junit.Assert.assertEquals;

public class StopLossTradeTest {

	private StubTrader alice;
	private StubTrader george;
	private StubTrader bruce;
	private StubTrader dean;
	private StubTrader jojo;

	private Stock lemons = new Stock("lemons");

	private SimpleSerialWorld world;
	private MarketFactory marketFactory;

	@Before
	public void setUp() throws  Exception{

		alice = new StubTraderBuilder()
			.setName("alice")
			.setCash(50000l)
			.addStock(lemons, 100)
			.build();
		
		george = new StubTraderBuilder()
			.setName("george")
			.setCash(50000l)
			.addStock(lemons, 1000)
			.build();
		
		bruce = new StubTraderBuilder()
			.setName("bruce")
			.setCash(50000l)
			.build();

		dean = new StubTraderBuilder()
			.setName("dean")
			.setCash(50000l)
			.addStock(lemons, 50)
			.build();

		jojo = new StubTraderBuilder()
			.setName("jojo")
			.setCash(50000l)
			.addStock(lemons, 80)
			.build();

		world = new SimpleSerialWorld(1000l);
		marketFactory = new ContinuousOrderDrivenMarketFactory(new SellOrderPricer());

	}

	@Test
	public void testStopLoss (){

		DefaultStockExchange market =
			new DefaultStockExchange(world,	new SerialTickerTapeObserver(),	marketFactory);

		StopLossSellOrder stopLossOrder = new StopLossSellOrder(alice, market.createLevel1View(), 99l, lemons, 10);
		
		StockExchangeLevel1View level1View = market.createLevel1View() ;
		level1View.registerTradeListener(stopLossOrder);

		SellOrder sellOrder1 = new LimitSellOrder(george,lemons, 100, 100l);
		george.supplyOrder(sellOrder1);
		george.speak(market.createLevel1View());

		BuyOrder buyOrder1 = new LimitBuyOrder(bruce,lemons,100, 100l);
		george.supplyOrder(buyOrder1);
		george.speak(market.createLevel1View());

		//create one more buy order to check stoploss is not activated
		BuyOrder buyOrder2 = new LimitBuyOrder(bruce,lemons, 200,98l );
		bruce.supplyOrder(buyOrder2);
		bruce.speak(market.createLevel1View());

		market.doClearing();
		//first market transaction to check that a false order is not triggered
		assertEquals(  alice.getInventory(lemons) ,(Integer) 100);

		SellOrder sellOrder2 = new LimitSellOrder(george,lemons, 100, 97l);
		george.supplyOrder(sellOrder2);
		george.speak(market.createLevel1View());
		//do the first trade
		market.doClearing();
		//now deal with alice order
		market.doClearing();
		assertEquals( 90,  alice.getInventory(lemons).intValue());
		assertEquals( 50980l, alice.getCash().longValue());


	}

	@Test
	public void testStopLossBuy () {

		DefaultStockExchange market =
				new DefaultStockExchange(world,	new SerialTickerTapeObserver(),	marketFactory);

		StopLossBuyOrder listener = new StopLossBuyOrder(alice,market.createLevel1View(), 110l,lemons, 10);
		StockExchangeLevel1View level1View = market.createLevel1View() ;
		level1View.registerTradeListener(listener);

		BuyOrder buyOrder1 = new LimitBuyOrder(bruce,lemons, 100, 100l);
		bruce.supplyOrder(buyOrder1);
		bruce.speak(market.createLevel1View());

		SellOrder sellOrder1 = new LimitSellOrder(george, lemons, 100, 100l);
		george.supplyOrder(sellOrder1);
		george.speak(market.createLevel1View());

		SellOrder sellOrder2 = new LimitSellOrder(george, lemons, 200,120l);
		george.supplyOrder(sellOrder2);
		george.speak(market.createLevel1View());

		market.doClearing();

		assertEquals( alice.getInventory(lemons), (Integer) 100);

		BuyOrder buyOrder2 = new LimitBuyOrder(bruce,lemons, 100, 120l);
		bruce.supplyOrder(buyOrder2);
		bruce.speak(market.createLevel1View());

		market.doClearing();
		//now alice order
		market.doClearing();

		assertEquals(110, alice.getInventory(lemons).intValue());
		assertEquals(48800l, alice.getCash().longValue());

	}


	@Test
	public void testSeveralStopLossSellOrders(){
		SerialTickerTapeObserver tickerTapeObserver =new SerialTickerTapeObserver();

		DefaultStockExchange market =
				new DefaultStockExchange(world,tickerTapeObserver,marketFactory);

		StopLossSellOrder listener1 = new StopLossSellOrder(alice,market.createLevel1View(), 99l, lemons, 100);
		StockExchangeLevel1View level1View = market.createLevel1View() ;
		level1View.registerTradeListener(listener1);

		StopLossSellOrder listener2 = new StopLossSellOrder(dean,market.createLevel1View(), 95l, lemons, 50);
		level1View.registerTradeListener(listener2);

		StopLossSellOrder listener3 = new StopLossSellOrder(jojo, market.createLevel1View(), 96l, lemons, 80);
		level1View.registerTradeListener(listener3);

		SellOrder sellOrder1 = new LimitSellOrder(george,lemons, 100, 100l);
		george.supplyOrder(sellOrder1);
		george.speak(market.createLevel1View());

		BuyOrder buyOrder1 = new LimitBuyOrder(bruce,lemons,100, 100l);
		george.supplyOrder(buyOrder1);
		george.speak(market.createLevel1View());

		//create one more buy order to check stoploss is not activated
		BuyOrder buyOrder2 = new LimitBuyOrder(bruce,lemons, 150,98l );
		bruce.supplyOrder(buyOrder2);
		bruce.speak(market.createLevel1View());

		market.doClearing();
		//first market transaction to check that a false order is not triggered
		assertEquals(  alice.getInventory(lemons) ,(Integer) 100);

		SellOrder sellOrder2 = new LimitSellOrder(george,lemons, 100, 97l);
		george.supplyOrder(sellOrder2);
		george.speak(market.createLevel1View());
		//do the first trade
		market.doClearing();
		//now deal with alice order
		market.doClearing();

		assertEquals(50, alice.getInventory(lemons).intValue());
		assertEquals(54900l, alice.getCash().longValue());//50000 + 50*98
		assertEquals(50, dean.getInventory(lemons).intValue());
		assertEquals(50000l, dean.getCash().longValue());
		assertEquals(80, jojo.getInventory(lemons).intValue());
		assertEquals(50000l, jojo.getCash().longValue());


		BuyOrder buyOrder3 = new LimitBuyOrder(bruce,lemons, 100, 95l);
		bruce.supplyOrder(buyOrder3);
		bruce.speak(market.createLevel1View());

		BuyOrder buyOrder4 = new LimitBuyOrder(george,lemons,300, 94l);
		george.supplyOrder(buyOrder4);
		george.speak(market.createLevel1View());

		//do the rest of alice market order
		market.doClearing();
		//now deal with dean order
		market.doClearing();

		assertEquals(0, alice.getInventory(lemons).intValue());
		assertEquals( alice.getCash(), (Long)( 54900l + 50l*95l)); //alice should sell 50 at 95
		assertEquals( jojo.getInventory(lemons), (Integer) 0); //all should be sold
		assertEquals( jojo.getCash(), (Long) (50000l + 50l*95l + 30l*94l)); //jojo should get the other 50 at 95 and 30 at 97
		assertEquals( dean.getInventory(lemons), (Integer) 0);//only 50 should be bought
		assertEquals( dean.getCash(), (Long)( 50000l + 50l*94l)); //this should be the rest at the lowest price

	}

	@Test
	public void testSeveralStopLossBuyOrders () {
		DefaultStockExchange market =
				new DefaultStockExchange(world,	new SerialTickerTapeObserver(),	marketFactory);

		StopLossBuyOrder listener = new StopLossBuyOrder(alice, market.createLevel1View(), 110l, lemons, 100);
		StockExchangeLevel1View level1View = market.createLevel1View() ;
		level1View.registerTradeListener(listener);

		StopLossBuyOrder listener1 = new StopLossBuyOrder(dean, market.createLevel1View(), 130l, lemons, 100);
		level1View.registerTradeListener(listener1);

		StopLossBuyOrder listener2 = new StopLossBuyOrder(jojo ,market.createLevel1View(), 125l, lemons, 100);
		level1View.registerTradeListener(listener2);

		BuyOrder buyOrder1 = new LimitBuyOrder(bruce,lemons, 100, 100l);
		bruce.supplyOrder(buyOrder1);
		bruce.speak(market.createLevel1View());

		SellOrder sellOrder1 = new LimitSellOrder(george, lemons, 100, 100l);
		george.supplyOrder(sellOrder1);
		george.speak(market.createLevel1View());

		SellOrder sellOrder2 = new LimitSellOrder(george, lemons, 150,120l);
		george.supplyOrder(sellOrder2);
		george.speak(market.createLevel1View());

		market.doClearing();

		assertEquals( alice.getInventory(lemons), (Integer) 100);

		BuyOrder buyOrder2 = new LimitBuyOrder(bruce,lemons, 100, 120l);
		bruce.supplyOrder(buyOrder2);
		bruce.speak(market.createLevel1View());

		market.doClearing();
		
		//now alice order
		market.doClearing();

		assertEquals(150, alice.getInventory(lemons).intValue());
		assertEquals(44000, alice.getCash().longValue());

		SellOrder sellOrder3 = new LimitSellOrder(george,lemons,100,130l);
		george.supplyOrder(sellOrder3);
		george.speak(market.createLevel1View());

		SellOrder sellOrder4 = new LimitSellOrder(george,lemons,100,140l);
		george.supplyOrder(sellOrder4);
		george.speak(market.createLevel1View());

		market.doClearing();
		//now the others' orders
		market.doClearing();


		assertEquals(200, alice.getInventory(lemons).intValue());
		assertEquals(alice.getCash(), (Long)( 50000l - 50l*120l - 50l*130l));
		assertEquals(180, jojo.getInventory(lemons).intValue()); 
		assertEquals(50000l -50l*130l-30l*140l, jojo.getCash().longValue());
		assertEquals(dean.getInventory(lemons), (Integer) 100);//doubled
		assertEquals(dean.getCash(), (Long)( 50000l -50l*140l));

	}

}