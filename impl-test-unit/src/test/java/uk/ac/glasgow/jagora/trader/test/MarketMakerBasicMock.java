package uk.ac.glasgow.jagora.trader.test;

import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.impl.DefaultTrade;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.ticker.LimitOrderEvent;
import uk.ac.glasgow.jagora.ticker.OrderEvent;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.trader.impl.marketmaker.MarketMaker;
import uk.ac.glasgow.jagora.trader.impl.marketmaker.MarketMakerBasicBuilder;
import static org.easymock.EasyMock.*;

import static uk.ac.glasgow.jagora.ticker.LimitOrderEvent.Action.PLACED;;


public class MarketMakerBasicMock {

	private Stock lemons;

	private MarketMakerBasicBuilder traderBuilder;

	@Rule
	public EasyMockRule rule = new EasyMockRule(this);

	@Mock
	StockExchangeLevel2View mockExchange;


	@Before
	public void setUp() {
		lemons = new Stock("lemons");

		traderBuilder = new MarketMakerBasicBuilder()
			.setName("Goldman")
			.setTargetStockQuantity(lemons, 1000)
			.setCash(100000000l)
			.setSeed(1)
			.setSpread(0.01)
			.addStock(lemons, 1000);
	}


	/**
	 * In the algorithm an order is placed and subsequently
	 * cancelled on every speak() turn of the marketMaker.
	 * In this test the second place of an order should be
	 * at a lower price because of the inventory excess
	 * - more inventory of a stock than the one aimed for
	 */
	@Test
	public void testInventoryPriceAdjustment() throws Exception {
		mockExchange = createMock(StockExchangeLevel2View.class);

		MarketMaker marketMaker = traderBuilder.build();

		//just necessities for operation
		mockExchange.registerOrderListener(marketMaker);
		mockExchange.registerTradeListener(marketMaker);

		DefaultLimitSellOrder sellOrder1 = new DefaultLimitSellOrder(marketMaker, lemons, 1000, 10100l);
		DefaultLimitSellOrder sellOrder2 = new DefaultLimitSellOrder(marketMaker, lemons, 2000, 10067l);
		mockExchange.placeLimitSellOrder(sellOrder1);
		mockExchange.cancelLimitSellOrder(sellOrder1);
		mockExchange.placeLimitSellOrder(sellOrder2);


		DefaultLimitBuyOrder buyOrder1 = new DefaultLimitBuyOrder(marketMaker, lemons, 1000, 9900l);
		DefaultLimitBuyOrder buyOrder2 = new DefaultLimitBuyOrder(marketMaker, lemons, 1000, 9867l);
		mockExchange.placeLimitBuyOrder(buyOrder1);
		mockExchange.cancelLimitBuyOrder(buyOrder1);
		mockExchange.placeLimitBuyOrder(buyOrder2);


		replay(mockExchange);


		//Put some orders in the marketMaker so that liquidity calculations are not affected
		marketMaker.limitOrderEvent(new LimitOrderEvent(null, null, lemons, 1000, OrderEvent.OrderDirection.BUY, 10000l, PLACED));
		marketMaker.limitOrderEvent(new LimitOrderEvent(null, null, lemons, 1000, OrderEvent.OrderDirection.SELL, 10000l, PLACED));

		//Put at least one trade executed so the pricing algorithm can function properly
		marketMaker.tradeExecuted(new TradeExecutionEvent(lemons, null, null, null, 10000l, 500));


		marketMaker.speak(mockExchange);

		//balance of inventory is broken with the buy of this stock
		marketMaker.buyStock(new DefaultTrade(lemons, 1000, 10l, null, new DefaultLimitBuyOrder(marketMaker, lemons, null, null)));

		//now as there is more stock than the targeted market share, prices should be going down
		marketMaker.speak(mockExchange);

		verify(mockExchange);


	}

	/**
	 * In the algorithm an order is placed and subsequently
	 * cancelled on every speak() turn of the marketMaker.
	 * In this test buy liquidity is subsequently increased,
	 * which should increase the spread on the side that
	 * was last active - Sell in this case. The second
	 * sell order should have a higher price than the first one
	 */
	@Test
	public void testBuyLiquidityImbalancePriceAdjustment() {


		mockExchange = createMock(StockExchangeLevel2View.class);

		MarketMaker marketMaker = traderBuilder.build();

		mockExchange.registerOrderListener(marketMaker);
		mockExchange.registerTradeListener(marketMaker);


		DefaultLimitSellOrder sellOrder1 = new DefaultLimitSellOrder(marketMaker, lemons, 1000, 10100l);
		DefaultLimitSellOrder sellOrder2 = new DefaultLimitSellOrder(marketMaker, lemons, 1000, 10114l);
		mockExchange.placeLimitSellOrder(sellOrder1);
		mockExchange.cancelLimitSellOrder(sellOrder1);
		mockExchange.placeLimitSellOrder(sellOrder2);

		DefaultLimitBuyOrder buyOrder1 = new DefaultLimitBuyOrder(marketMaker, lemons, 1000, 9900l);
		mockExchange.placeLimitBuyOrder(buyOrder1);
		mockExchange.cancelLimitBuyOrder(buyOrder1);
		mockExchange.placeLimitBuyOrder(buyOrder1);

		replay(mockExchange);

		marketMaker.limitOrderEvent(new LimitOrderEvent(null, null, lemons, 1000, OrderEvent.OrderDirection.BUY, 10000l, PLACED));
		marketMaker.limitOrderEvent(new LimitOrderEvent(null, null, lemons, 1000, OrderEvent.OrderDirection.SELL, 10000l, PLACED));

		//Algorithm for spread increase will work only if last trade was
		marketMaker.tradeExecuted(new TradeExecutionEvent(lemons, null, null, null, 10000l, 500));

		marketMaker.speak(mockExchange);

		marketMaker.limitOrderEvent(new LimitOrderEvent(null, null, lemons, 1000, OrderEvent.OrderDirection.BUY, 1000l, PLACED));

		//Now that there is imbalance in liquidity - more buy desire, spread should increase
		// Sell price should be going up
		marketMaker.speak(mockExchange);

		verify(mockExchange);
	}

	/**
	 * In the algorithm an order is placed and subsequently
	 * cancelled on every speak() turn of the marketMaker.
	 * In this test Sell Liquidity is subsequently increased,
	 * which should increase the spread on the side that was
	 * last active - buy in this case. The second buy order should
	 * have a lower price than the first one.
	 */
	@Test
	public void testSellLiquidityImbalancePriceAdjustment() {
		mockExchange = createMock(StockExchangeLevel2View.class);

		MarketMaker marketMaker = traderBuilder.build();

		mockExchange.registerOrderListener(marketMaker);
		mockExchange.registerTradeListener(marketMaker);

		DefaultLimitBuyOrder buyOrder1 = new DefaultLimitBuyOrder(marketMaker, lemons, 1000, 9900l);
		DefaultLimitBuyOrder buyOrder2 = new DefaultLimitBuyOrder(marketMaker, lemons, 1000, 9858l);
		mockExchange.placeLimitBuyOrder(buyOrder1);
		mockExchange.cancelLimitBuyOrder(buyOrder1);
		mockExchange.placeLimitBuyOrder(buyOrder2);


		DefaultLimitSellOrder sellOrder1 = new DefaultLimitSellOrder(marketMaker, lemons, 1000, 10100l);
		mockExchange.placeLimitSellOrder(sellOrder1);
		mockExchange.cancelLimitSellOrder(sellOrder1);
		mockExchange.placeLimitSellOrder(sellOrder1);

		replay(mockExchange);

		marketMaker.limitOrderEvent(new LimitOrderEvent(null, null, lemons, 1000, OrderEvent.OrderDirection.BUY, 10000l, PLACED));
		marketMaker.limitOrderEvent(new LimitOrderEvent(null, null, lemons, 1000, OrderEvent.OrderDirection.SELL, 10000l, PLACED));

		marketMaker.tradeExecuted(new TradeExecutionEvent(lemons, null, null, null, 10000l, 500));

		marketMaker.speak(mockExchange);

		marketMaker.limitOrderEvent(new LimitOrderEvent(null, null, lemons, 1000, OrderEvent.OrderDirection.SELL, 1000l, PLACED));

		//Now that there is imbalance in liquidity - more buy desire, spread should increase
		// Buy  price should be going down
		marketMaker.speak(mockExchange);

		verify(mockExchange);
	}


	/**
	 * In the algorithm an order is placed and subsequently
	 * cancelled on every speak() turn of the marketMaker.
	 * In this test the influence on liquidity imbalance is
	 * increased to a point where the sell order, that is affected
	 * should cross the buyOrder. This should not be possible and in
	 * a mechanism which increases the price of the sell order to 1l
	 * more than the buy order.
	 */
	@Test
	public void testFixPriceAnomalies() {

		mockExchange = createMock(StockExchangeLevel2View.class);

		MarketMaker marketMaker = traderBuilder
				.setLiquidityAdjustmentInfluence(5.0) //This being the changing factor
				.setInventoryAdjustmentInfluence(1.0)
				.build();

		mockExchange.registerOrderListener(marketMaker);
		mockExchange.registerTradeListener(marketMaker);


		DefaultLimitSellOrder sellOrder1 = new DefaultLimitSellOrder(marketMaker, lemons, 1000, 10100l);
		DefaultLimitSellOrder sellOrder2 = new DefaultLimitSellOrder(marketMaker, lemons, 1000, 9901l);
		mockExchange.placeLimitSellOrder(sellOrder1);
		mockExchange.cancelLimitSellOrder(sellOrder1);
		mockExchange.placeLimitSellOrder(sellOrder2);

		DefaultLimitBuyOrder buyOrder1 = new DefaultLimitBuyOrder(marketMaker, lemons, 1000, 9900l);
		mockExchange.placeLimitBuyOrder(buyOrder1);
		mockExchange.cancelLimitBuyOrder(buyOrder1);
		mockExchange.placeLimitBuyOrder(buyOrder1);

		replay(mockExchange);

		marketMaker.limitOrderEvent(new LimitOrderEvent(null, null, lemons, 1000, OrderEvent.OrderDirection.BUY, 10000l, PLACED));
		marketMaker.limitOrderEvent(new LimitOrderEvent(null, null, lemons, 1000, OrderEvent.OrderDirection.SELL, 10000l, PLACED));

		marketMaker.tradeExecuted(new TradeExecutionEvent(lemons, null, null, null, 10000l, 500));

		marketMaker.speak(mockExchange);

		marketMaker.limitOrderEvent(new LimitOrderEvent(null, null, lemons, 1000, OrderEvent.OrderDirection.SELL, 1000l, PLACED));

		marketMaker.speak(mockExchange);

		verify(mockExchange);
	}

	/**
	 * In the algorithm an order is placed and subsequently
	 * cancelled on every speak() turn of the marketMaker.
	 * In this test inventory is imbalanced after the
	 * first turn to a point, where MarketMaker will provide
	 * a stub quote - A buyOrder of only 0.1 of the shares
	 * it is usually trying to buy is put
	 */
	@Test
	public void testStubQuoteAdjustment() throws Exception {
		mockExchange = createMock(StockExchangeLevel2View.class);
		
		MarketMaker marketMaker = traderBuilder
				.setLiquidityAdjustmentInfluence(5.0) //This being the changing factor
				.setInventoryAdjustmentInfluence(1.0)
				.build();

		mockExchange.registerOrderListener(marketMaker);
		mockExchange.registerTradeListener(marketMaker);

		DefaultLimitBuyOrder buyOrder1 = new DefaultLimitBuyOrder(marketMaker, lemons, 1000, 9900l);
		DefaultLimitBuyOrder buyOrder2 = new DefaultLimitBuyOrder(marketMaker, lemons, 10, 9830l);
		mockExchange.placeLimitBuyOrder(buyOrder1);
		mockExchange.cancelLimitBuyOrder(buyOrder1);
		mockExchange.placeLimitBuyOrder(buyOrder2);

		DefaultLimitSellOrder sellOrder1 = new DefaultLimitSellOrder(marketMaker, lemons, 1000, 10100l);
		DefaultLimitSellOrder sellOrder2 = new DefaultLimitSellOrder(marketMaker, lemons, 3100, 10030l);
		mockExchange.placeLimitSellOrder(sellOrder1);
		mockExchange.cancelLimitSellOrder(sellOrder1);
		mockExchange.placeLimitSellOrder(sellOrder2);


		replay(mockExchange);

		marketMaker.limitOrderEvent(new LimitOrderEvent(null, null, lemons, 1000, OrderEvent.OrderDirection.BUY, 10000l, PLACED));
		marketMaker.limitOrderEvent(new LimitOrderEvent(null, null, lemons, 1000, OrderEvent.OrderDirection.SELL, 10000l, PLACED));

		marketMaker.tradeExecuted(new TradeExecutionEvent(lemons, null, null, null, 10000l, 500));

		marketMaker.speak(mockExchange);

		//balance of inventory is broken with the buy of this stock
		marketMaker.buyStock(new DefaultTrade(lemons, 2100, 10l, null, new DefaultLimitBuyOrder(marketMaker, lemons, null, null)));

		marketMaker.speak(mockExchange);

		verify(mockExchange);

	}

}

