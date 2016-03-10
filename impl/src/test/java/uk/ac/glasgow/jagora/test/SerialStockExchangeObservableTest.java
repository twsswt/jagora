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
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.impl.DefaultTrade;
import uk.ac.glasgow.jagora.ticker.StockExchangeObservable;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;

public class SerialStockExchangeObservableTest extends EasyMockSupport {

	private Stock lemons = new Stock("lemons");	
	
	@Rule
	public EasyMockRule rule = new EasyMockRule(this);

	@Mock
	TradeListener tradeListener;
	
	@Mock(name="alice")
	private Trader alice;

	@Mock(name="bob")
	private Trader bob;
	
	private StockExchangeObservable stockExchangeObservable;
	
	@Before
	public void setUp() {
		stockExchangeObservable = new SerialTickerTapeObserver();
		stockExchangeObservable.registerTradeListener(tradeListener);
	}

	@Test
	public void testCancellationBook () {

		LimitSellOrder limitSellOrder = new DefaultLimitSellOrder(bob, lemons, 10, 1000l);
		LimitBuyOrder limitBuyOrder = new DefaultLimitBuyOrder(alice, lemons, 10, 1100l);
		
		Trade trade = new DefaultTrade(lemons, 10, 1000l, limitSellOrder, limitBuyOrder);
		TickEvent<Trade> tradeTick = new TickEvent<Trade>(trade, 1l);
		
		TradeExecutionEvent expected = 
			new TradeExecutionEvent(lemons, alice, bob, 1l, 1000l, 10);
		
		tradeListener.tradeExecuted(expected);
					
		replayAll();
		
		stockExchangeObservable.notifyTradeListeners(asList(tradeTick));
		
		verifyAll();
	}

}
