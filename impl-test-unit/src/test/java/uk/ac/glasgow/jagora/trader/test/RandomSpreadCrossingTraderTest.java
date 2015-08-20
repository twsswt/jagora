package uk.ac.glasgow.jagora.trader.test;

import static org.easymock.EasyMock.expectLastCall;

import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.trader.impl.random.RandomSpreadCrossingTrader;
import uk.ac.glasgow.jagora.trader.impl.random.RandomSpreadCrossingTraderBuilder;

public class RandomSpreadCrossingTraderTest extends EasyMockSupport {
	
	@Rule
	public EasyMockRule rule = new EasyMockRule(this);
	
	private RandomSpreadCrossingTrader trader;

	private Stock lemons;
	
	@Mock
	private StockExchangeLevel1View mockExchange;

	@Before
	public void setUp() throws Exception {
				
		lemons  = new Stock("lemons");
				
		trader = new RandomSpreadCrossingTraderBuilder()
			.setName("alice")
			.setCash(100l)
			.setSeed(1)
			.addStock(lemons, 10)
			.addTradeRange(lemons, 1, 4, 4l)
			.build();
		
	}

	@Test
	public void test() {
		
		//mockExchange
		mockExchange.getBestBidPrice(lemons);
		expectLastCall().andReturn(4l);
		mockExchange.placeSellOrder(new LimitSellOrder (trader, lemons, 1, 3l));
		mockExchange.getBestOfferPrice(lemons);
		expectLastCall().andReturn(6l);
		mockExchange.placeBuyOrder(new LimitBuyOrder (trader, lemons, 1, 6l));

		replayAll ();
		
		trader.speak(mockExchange);
		trader.speak(mockExchange);
		
		verifyAll();
	}

}
