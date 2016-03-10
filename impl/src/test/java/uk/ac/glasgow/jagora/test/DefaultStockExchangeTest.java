package uk.ac.glasgow.jagora.test;

import static java.util.Arrays.asList;

import java.util.ArrayList;

import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.impl.DefaultTrade;
import uk.ac.glasgow.jagora.pricer.impl.SellLimitOrderPricer;
import uk.ac.glasgow.jagora.ticker.StockExchangeObservable;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;


public class DefaultStockExchangeTest extends EasyMockSupport {
	
	@Rule
	public EasyMockRule rule = new EasyMockRule(this);

	private Stock lemons = new Stock("lemons");
	private Stock oranges = new Stock("oranges");
	
	@Mock(name="alice")
	private Trader alice;
	
	@Mock(name="bob")
	private Trader bob;
		
	private LimitSellOrder[] sellOrders;
	private LimitBuyOrder[] limitBuyOrders;
	private Trade[] trades;		
	
	@Mock
	private StockExchangeObservable tickerTapeObserver;
	
	private DefaultStockExchange defaultStockExchange;
	
	private StockExchangeLevel1View stockExchangeLevel1View;


	@Before
	public void setUp() throws Exception {
			
		sellOrders = new LimitSellOrder[] {
			new DefaultLimitSellOrder(bob, lemons, 50, 550l),
			new DefaultLimitSellOrder(bob, lemons, 10, 559l),
			new DefaultLimitSellOrder(alice, oranges, 20, 265l),
			new DefaultLimitSellOrder(alice, oranges, 20, 250l)
		};
		
		limitBuyOrders = new LimitBuyOrder[]{
			new DefaultLimitBuyOrder(alice, lemons, 25, 450l),
			new DefaultLimitBuyOrder(alice, lemons, 60, 560l),
			new DefaultLimitBuyOrder(bob, oranges, 30, 270l)
		};
		
		trades = new Trade[] {
			new DefaultTrade(lemons, 50, 550l, sellOrders[0], limitBuyOrders[1]),
			new DefaultTrade(lemons, 10, 559l, sellOrders[1], limitBuyOrders[1]),
			new DefaultTrade(oranges, 20, 250l, sellOrders[3], limitBuyOrders[2]),
			new DefaultTrade(oranges, 10, 265l, sellOrders[2], limitBuyOrders[2])

		};
		
		ContinuousOrderDrivenMarketFactory marketFactory = 
			new ContinuousOrderDrivenMarketFactory(new SellLimitOrderPricer());
		
		SimpleSerialWorld world = new SimpleSerialWorld(1000l);

		defaultStockExchange = 
			new DefaultStockExchange(world,	tickerTapeObserver,	marketFactory);
		
		stockExchangeLevel1View = 
			defaultStockExchange.createLevel1View();
	}
	
	@Test
	public void testPlaceLimitOrder (){
		
		tickerTapeObserver.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitBuyOrder>(limitBuyOrders[0], 0l));
		
		replayAll();
		
		stockExchangeLevel1View.placeLimitBuyOrder(limitBuyOrders[0]);
		
		verifyAll();
	}
	
	@Test
	public void testCancelLimitOrder (){
	
		tickerTapeObserver.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitBuyOrder>(limitBuyOrders[0], 0l));
		tickerTapeObserver.notifyOrderListenersOfLimitOrderCancellation(
			new TickEvent<LimitBuyOrder>(limitBuyOrders[0], 1l));
		
		
		replayAll();
		
		stockExchangeLevel1View.placeLimitBuyOrder(limitBuyOrders[0]);
		stockExchangeLevel1View.cancelLimitBuyOrder(limitBuyOrders[0]);
		
		verifyAll();
		
	}
	
	/**
	 * A cancelled non-existent order should not result in a notification to the observable.
	 */
	@Test
	public void testCancelNonExistentOrder (){		
		
		replayAll();
		
		stockExchangeLevel1View.cancelLimitBuyOrder(limitBuyOrders[0]);
		
		verifyAll();		
	}
	

	/**
	 * Executes a short order sequence for two agents on two stocks.
	 */
	@Test
	public void testClearingWithNoCompatibleOrders() {

		tickerTapeObserver.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitSellOrder>(sellOrders[0], 0l));
		
		tickerTapeObserver.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitBuyOrder>(limitBuyOrders[0], 1l));
		
		tickerTapeObserver.notifyTradeListeners(new ArrayList<>());
		
		replayAll();
		
		stockExchangeLevel1View
			.placeLimitSellOrder(sellOrders[0]);
		
		stockExchangeLevel1View
			.placeLimitBuyOrder(limitBuyOrders[0]);
				
		defaultStockExchange.doClearing();
		
		verifyAll ();
	}
	
	@Test
	public void testCompletelyClearSellSide () throws Exception{
		
		tickerTapeObserver.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitSellOrder>(sellOrders[0], 0l));
		
		tickerTapeObserver.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitBuyOrder>(limitBuyOrders[0], 1l));
		
		tickerTapeObserver.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitSellOrder>(sellOrders[1], 2l));
		
		tickerTapeObserver.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitBuyOrder>(limitBuyOrders[1], 3l));

		bob.sellStock(trades[0]);
		alice.buyStock(trades[0]);
		bob.sellStock(trades[1]);
		alice.buyStock(trades[1]);
		
		tickerTapeObserver.notifyTradeListeners(
			asList(
				new TickEvent<Trade>(trades[0], 4l),
				new TickEvent<Trade>(trades[1], 5l)));
				
		replayAll();
		
		stockExchangeLevel1View
			.placeLimitSellOrder(sellOrders[0]);
	
		stockExchangeLevel1View
			.placeLimitBuyOrder(limitBuyOrders[0]);

				
		stockExchangeLevel1View
			.placeLimitSellOrder(sellOrders[1]);
	
		stockExchangeLevel1View
			.placeLimitBuyOrder(limitBuyOrders[1]);
		
		defaultStockExchange.doClearing();
		
		verifyAll();
	}
	
	@Test
	public void testCompletelyClearBuySide () throws Exception {

		tickerTapeObserver.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitSellOrder>(sellOrders[2], 0l));
		
		tickerTapeObserver.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitSellOrder>(sellOrders[3], 1l));
		
		tickerTapeObserver.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitBuyOrder>(limitBuyOrders[2], 2l));
		
		alice.sellStock(trades[2]);
		bob.buyStock(trades[2]);
		alice.sellStock(trades[3]);
		bob.buyStock(trades[3]);
		
		tickerTapeObserver.notifyTradeListeners(
			asList(new TickEvent<Trade>(trades[2], 3l), new TickEvent<Trade>(trades[3], 4l)));
		
		replayAll ();
		
		stockExchangeLevel1View
			.placeLimitSellOrder(sellOrders[2]);
		
		stockExchangeLevel1View
			.placeLimitSellOrder(sellOrders[3]);

		stockExchangeLevel1View
			.placeLimitBuyOrder(limitBuyOrders[2]);
		
		defaultStockExchange.doClearing();
		
		verifyAll ();
	}
	
}
