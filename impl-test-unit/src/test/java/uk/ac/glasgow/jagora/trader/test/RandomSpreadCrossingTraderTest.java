package uk.ac.glasgow.jagora.trader.test;

import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.trader.impl.random.RandomSpreadCrossingTrader;
import uk.ac.glasgow.jagora.trader.impl.random.RandomSpreadCrossingTraderBuilder;
import static org.easymock.EasyMock.expect;

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
	}

	@Test
	public void testRandomSpreadCrossingTrader() {

		trader = new RandomSpreadCrossingTraderBuilder()
			.setName("alice")
			.setCash(100l)
			.setSeed(1)
			.addStock(lemons, 10)
			.addTradeRange(lemons, 1, 4, 4l)
			.build();

		
		expect(mockExchange.getBestOfferPrice(lemons)).andReturn(6l);
		mockExchange.placeLimitBuyOrder(new DefaultLimitBuyOrder (trader, lemons, 3, 7l));
		expect(mockExchange.getBestOfferPrice(lemons)).andReturn(4l);
		mockExchange.placeLimitBuyOrder(new DefaultLimitBuyOrder (trader, lemons, 2, 6l));

		replayAll ();
		
		trader.speak(mockExchange);
		trader.speak(mockExchange);
		
		verifyAll();
	}

}
