package uk.ac.glasgow.jagora.trader.test;

import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.SimpleHistoricTrader;
import uk.ac.glasgow.jagora.trader.impl.SimpleHistoricTraderBuilder;

import static org.easymock.EasyMock.expect;

public class SimpleHistoricTraderTest extends EasyMockSupport {

	@Rule
    public EasyMockRule rule = new EasyMockRule(this);
	
	private Stock lemons;
	
	@Mock
	private StockExchangeLevel1View mockExchange;
	
	@Mock
	private Trader mockBuyer;
	
	@Mock
	private Trader mockSeller;

	private SimpleHistoricTrader alice;

	@Before
	public void setUp() throws Exception {

		lemons = new Stock("lemons");
				
		alice = new SimpleHistoricTraderBuilder()
				.setSeed(22)
				.setName("alice")
				.setCash(100l)
			    .addStock(lemons, 10)
			    .build();
	}

	@Test
	public void test() {
				
		expect(mockExchange.getBestBidPrice(lemons)).andReturn(8l);
		expect(mockExchange.getBestOfferPrice(lemons)).andReturn(9l);
		
		mockExchange.placeBuyOrder(new LimitBuyOrder(alice, lemons, 1, 9l));
		
		replayAll ();
		
		alice.tradeExecuted(new TradeExecutionEvent(lemons, mockBuyer, mockSeller, 0l, 10l, 1));
		alice.speak(mockExchange);
		
		verifyAll();
	}
}
