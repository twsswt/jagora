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
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.trader.impl.random.RandomSpreadCrossingTrader;
import uk.ac.glasgow.jagora.trader.impl.random.RandomSpreadCrossingTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.random.RandomSpreadCrossingTraderPctBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

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

		mockExchange.getBestBidPrice(lemons);
		expectLastCall().andReturn(4l);
		mockExchange.placeSellOrder(new LimitSellOrder (trader, lemons, 2, 3l));
		mockExchange.getBestOfferPrice(lemons);
		expectLastCall().andReturn(6l);
		mockExchange.placeBuyOrder(new LimitBuyOrder (trader, lemons, 2, 6l));

		replayAll ();
		
		trader.speak(mockExchange);
		trader.speak(mockExchange);
		
		verifyAll();
	}


	@Test
	public void testRandomSpreadCrossingTraderPct () {

		trader = new RandomSpreadCrossingTraderPctBuilder()
				.setName("alicePct")
				.setCash(1000l)
				.setSeed(2)
				.addStock(lemons,50)
				.addTradeRangePct(lemons,1,10,0.1)
				.build();

		expect(mockExchange.getBestBidPrice(lemons)).andReturn(100l);
		mockExchange.placeSellOrder(new LimitSellOrder(trader,lemons,7,91l));

		expect(mockExchange.getBestOfferPrice(lemons)).andReturn(150l);
		mockExchange.placeBuyOrder(new LimitBuyOrder(trader,lemons,1,162l));

		replayAll();

		trader.speak(mockExchange);
		trader.speak(mockExchange);

		verifyAll();
	}
}
