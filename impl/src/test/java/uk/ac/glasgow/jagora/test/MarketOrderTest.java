package uk.ac.glasgow.jagora.test;

import static java.util.Arrays.asList;

import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.MarketBuyOrder;
import uk.ac.glasgow.jagora.MarketFactory;
import uk.ac.glasgow.jagora.MarketSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.TradeExecutionException;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultMarketBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.impl.DefaultMarketSellOrder;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.impl.DefaultTrade;
import uk.ac.glasgow.jagora.pricer.impl.SellLimitOrderPricer;
import uk.ac.glasgow.jagora.ticker.StockExchangeObservable;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

public class MarketOrderTest extends EasyMockSupport {

	private static final Stock lemons = new Stock("lemons");
	
	@Rule
	public EasyMockRule rule = new EasyMockRule(this);

	@Mock(name="alice")
	private Trader alice;
	
	@Mock(name="bruce")
	private Trader bruce;
	
	@Mock(name="george")
	private Trader george;
	
	@Mock
	private StockExchangeObservable stockExchangeObservable;

	private DefaultStockExchange stockExchange;

	private StockExchangeLevel1View stockExchangeLevel1View;

	@Before
	public void setUp() throws Exception {

		World world = new SimpleSerialWorld(1000l);
		MarketFactory marketFactory =
			new ContinuousOrderDrivenMarketFactory(new SellLimitOrderPricer());
		
		stockExchange =
			new DefaultStockExchange(world,	stockExchangeObservable, marketFactory);
		
		stockExchangeLevel1View = stockExchange.createLevel1View();
	}

	/**
	 * Clear a market buy order against two sell orders.
	 * @throws TradeExecutionException
	 */

	@Test
	public void testMarketBuyOrder() throws TradeExecutionException {

		LimitSellOrder limitSellOrder1 = new DefaultLimitSellOrder(bruce, lemons, 3000, 100l);
		LimitSellOrder limitSellOrder2 = new DefaultLimitSellOrder(george, lemons, 1000, 150l);
		MarketBuyOrder marketBuyOrder = new DefaultMarketBuyOrder(alice, lemons, 100);

		Trade trade1 = new DefaultTrade(lemons, 100, 100l, limitSellOrder1, marketBuyOrder);
		
		stockExchangeObservable.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitSellOrder>(limitSellOrder1, 0l));
		
		stockExchangeObservable.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitSellOrder>(limitSellOrder2, 1l));
		
		stockExchangeObservable.notifyOrderListenersOfMarketOrder(
			new TickEvent<MarketBuyOrder>(marketBuyOrder, 2l));
		
		bruce.sellStock(trade1);
		alice.buyStock(trade1);
		
		stockExchangeObservable.notifyTradeListeners(asList(new TickEvent<Trade>(trade1, 3l)));
		
		replayAll ();
		
		stockExchangeLevel1View.placeLimitSellOrder(limitSellOrder1);
		stockExchangeLevel1View.placeLimitSellOrder(limitSellOrder2);
		stockExchangeLevel1View.placeMarketBuyOrder(marketBuyOrder);
		stockExchange.doClearing();
		
		verifyAll();
		
	}

	@Test
	public void testMarketSellOrder () throws TradeExecutionException {
		
		LimitBuyOrder limitBuyOrder1 = new DefaultLimitBuyOrder(alice, lemons, 100, 100l);
		LimitBuyOrder limitBuyOrder2 = new DefaultLimitBuyOrder(george, lemons, 100, 50l);
		MarketSellOrder marketSellOrder = new DefaultMarketSellOrder(bruce, lemons, 150);

		Trade trade1 = new DefaultTrade(lemons, 100, 100l, marketSellOrder, limitBuyOrder1);
		Trade trade2 = new DefaultTrade(lemons, 50, 50l, marketSellOrder, limitBuyOrder2);
		
		stockExchangeObservable.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitBuyOrder>(limitBuyOrder1, 0l));
		
		stockExchangeObservable.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitBuyOrder>(limitBuyOrder1, 1l));
		
		stockExchangeObservable.notifyOrderListenersOfMarketOrder(
			new TickEvent<MarketSellOrder>(marketSellOrder, 2l));
		
		bruce.sellStock(trade1);
		alice.buyStock(trade1);		
		bruce.sellStock(trade2);
		george.buyStock(trade2);
		
		stockExchangeObservable.notifyTradeListeners(
			asList(new TickEvent<Trade>(trade1, 3l), new TickEvent<Trade>(trade2, 4l)));

		replayAll ();
		
		stockExchangeLevel1View.placeLimitBuyOrder(limitBuyOrder1);
		stockExchangeLevel1View.placeLimitBuyOrder(limitBuyOrder2);
		stockExchangeLevel1View.placeMarketSellOrder(marketSellOrder);
		stockExchange.doClearing();
		
		verifyAll();
	}

	/**
	 * Check that no trade occurs on a market with only market orders.
	 */
	@Test
	public void testTwoMarketOrders() {
		
		MarketBuyOrder marketBuyOrder = new DefaultMarketBuyOrder(alice, lemons, 100);
		MarketSellOrder marketSellOrder = new DefaultMarketSellOrder(bruce, lemons, 150);

		stockExchangeObservable.notifyOrderListenersOfMarketOrder(
			new TickEvent<MarketBuyOrder>(marketBuyOrder, 0l));
		
		stockExchangeObservable.notifyOrderListenersOfMarketOrder(
			new TickEvent<MarketSellOrder>(marketSellOrder, 1l));
		
		stockExchangeObservable.notifyTradeListeners(asList());
		
		replayAll();
	
		stockExchangeLevel1View.placeMarketBuyOrder(marketBuyOrder);
		stockExchangeLevel1View.placeMarketSellOrder(marketSellOrder);
		stockExchange.doClearing();

		verifyAll();

	}

	/**
	 * Tests that the availability of a spread allows a
	 * market order to be executed in preference to the
	 * spread creating limit orders.
	 */
	@Test
	public void testMarketOrderWhenBetterPrice() throws TradeExecutionException {
		
		MarketSellOrder marketSellOrder = new DefaultMarketSellOrder(bruce, lemons, 150);
		LimitSellOrder limitSellOrder1 = new DefaultLimitSellOrder(george, lemons, 50, 150l);
		LimitBuyOrder limitBuyOrder1 = new DefaultLimitBuyOrder(alice, lemons, 100, 100l);
		
		Trade trade1 = new DefaultTrade(lemons, 100, 100l, marketSellOrder, limitBuyOrder1);

		stockExchangeObservable.notifyOrderListenersOfMarketOrder(
			new TickEvent<MarketSellOrder>(marketSellOrder, 0l));
		
		stockExchangeObservable.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitSellOrder>(limitSellOrder1, 1l));
		
		stockExchangeObservable.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitBuyOrder>(limitBuyOrder1, 2l));

		bruce.sellStock(trade1);
		alice.buyStock(trade1);
		
		stockExchangeObservable.notifyTradeListeners(
			asList(new TickEvent<Trade>(trade1, 3l)));

		replayAll ();

		stockExchangeLevel1View.placeMarketSellOrder(marketSellOrder);
		stockExchangeLevel1View.placeLimitSellOrder(limitSellOrder1);
		stockExchangeLevel1View.placeLimitBuyOrder(limitBuyOrder1);

		stockExchange.doClearing();

		verifyAll();
		
	}


	/**
	 * Tests that two market sell orders are executed in the
	 * order passed to the market, even though counter party
	 * limit buy orders are prioritised by limit price.
	 * 
	 * @throws TradeExecutionException
	 */
	@Test
	public void testTwoMarketSellOrders() throws TradeExecutionException{

		MarketSellOrder marketSellOrder1 = new DefaultMarketSellOrder(bruce,lemons,100);
		MarketSellOrder marketSellOrder2 = new DefaultMarketSellOrder(george,lemons,50);

		LimitBuyOrder limitBuyOrder1 = new DefaultLimitBuyOrder(alice,lemons,50,50l);
		Trade trade1 = new DefaultTrade(lemons, 50, 50l, marketSellOrder1, limitBuyOrder1);
		
		LimitBuyOrder limitBuyOrder2 = new DefaultLimitBuyOrder(alice,lemons,70,60l);
		Trade trade2 = new DefaultTrade(lemons, 50, 60l, marketSellOrder1, limitBuyOrder2);
		Trade trade3 = new DefaultTrade(lemons, 20, 60l, marketSellOrder2, limitBuyOrder2);
		
		// resetAll ();
		
		stockExchangeObservable.notifyOrderListenersOfMarketOrder(
			new TickEvent<MarketSellOrder>(marketSellOrder1, 0l));
		
		stockExchangeObservable.notifyOrderListenersOfMarketOrder(
			new TickEvent<MarketSellOrder>(marketSellOrder2, 1l));
		
		stockExchangeObservable.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitBuyOrder>(limitBuyOrder1, 2l));
		
		bruce.sellStock(trade1);
		alice.buyStock(trade1);
		
		stockExchangeObservable.notifyTradeListeners(
			asList(new TickEvent<Trade>(trade1, 3l)));

		stockExchangeObservable.notifyOrderListenersOfLimitOrder(
			new TickEvent<LimitBuyOrder>(limitBuyOrder2, 4l));

		bruce.sellStock(trade2);
		alice.buyStock(trade2);
		george.sellStock(trade3);
		alice.buyStock(trade3);
		
		stockExchangeObservable.notifyTradeListeners(
			asList(
				new TickEvent<Trade>(trade2, 5l),
				new TickEvent<Trade>(trade3, 6l)));

		replayAll ();
		
		stockExchangeLevel1View.placeMarketSellOrder(marketSellOrder1);
		stockExchangeLevel1View.placeMarketSellOrder(marketSellOrder2);
		stockExchangeLevel1View.placeLimitBuyOrder(limitBuyOrder1);

		stockExchange.doClearing();
		
		stockExchangeLevel1View.placeLimitBuyOrder(limitBuyOrder2);
		
		stockExchange.doClearing();
		
		verifyAll ();

	}
}
