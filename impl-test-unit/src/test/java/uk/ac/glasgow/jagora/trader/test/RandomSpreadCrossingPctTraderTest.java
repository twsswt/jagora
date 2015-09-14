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
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.trader.impl.random.RandomSpreadCrossingTrader;
import uk.ac.glasgow.jagora.trader.impl.random.RandomSpreadCrossingTraderBuilder;
import uk.ac.glasgow.jagora.trader.ivo.impl.RandomSpreadCrossingTraderPctBuilder;
import static org.easymock.EasyMock.expect;

public class RandomSpreadCrossingPctTraderTest extends EasyMockSupport {
	
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
	public void testRandomSpreadCrossingTraderPct () {

		trader = new RandomSpreadCrossingTraderPctBuilder()
				.setName("alicePct")
				.setCash(1000l)
				.setSeed(2)
				.addStock(lemons,50)
				.addTradeRangePct(lemons,1,10,0.1)
				.build();

		expect(mockExchange.getBestOfferPrice(lemons)).andReturn(100l);
		mockExchange.placeLimitBuyOrder(new DefaultLimitBuyOrder(trader,lemons,7,109l));

		expect(mockExchange.getBestBidPrice(lemons)).andReturn(150l);
		mockExchange.placeLimitSellOrder(new DefaultLimitSellOrder(trader,lemons,1,138l));

		replayAll();

		trader.speak(mockExchange);
		trader.speak(mockExchange);

		verifyAll();
	}
}
