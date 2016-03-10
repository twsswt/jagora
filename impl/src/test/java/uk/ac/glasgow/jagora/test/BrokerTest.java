package uk.ac.glasgow.jagora.test;

import java.util.HashMap;

import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.DefaultMarketSellOrder;
import uk.ac.glasgow.jagora.impl.StopLossSellOrder;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.DefaultBroker;

public class BrokerTest extends EasyMockSupport {
	
	@Rule
	public EasyMockRule rule = new EasyMockRule(this);
	
	private DefaultBroker broker;

	private Stock lemons = new Stock("lemons");
	
	@Mock
	private StockExchangeLevel1View mockExchange;

	@Mock
	private Trader alice;
	
	@Mock
	private Trader bob;
	
	@Mock
	private Trader charlie;

	@Before
	public void setUp() throws  Exception{
		
		broker = new DefaultBroker("broker", 0l, new HashMap<Stock,Integer>());

	}

	@Test
	public void testSingleSellStopLoss (){

		mockExchange.placeMarketSellOrder(new DefaultMarketSellOrder(alice, lemons, 10));
		//TODO		
		replayAll();
		broker.placeStopLossOrder(new StopLossSellOrder(alice, 25l, lemons, 10));
		broker.tradeExecuted(new TradeExecutionEvent(lemons, bob, charlie, 1l, 24l, 1));
		broker.speak(mockExchange);		
		verifyAll();		
		
	}
}
