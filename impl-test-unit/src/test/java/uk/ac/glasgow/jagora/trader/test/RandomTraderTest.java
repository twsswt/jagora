package uk.ac.glasgow.jagora.trader.test;

import static org.easymock.EasyMock.expect;

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
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.impl.RandomTraderBuilder;

public class RandomTraderTest extends EasyMockSupport {
		
	@Rule
	public EasyMockRule rule = new EasyMockRule(this);
	
	private Level1Trader trader;
	private Stock lemons;
	
	@Mock
	private StockExchangeLevel1View mockExchange;

	@Before
	public void setUp() throws Exception {
		
		
		lemons  = new Stock("lemons");
		
		trader = new RandomTraderBuilder()
			.setName("alice")
			.setCash(100l)
			.setSeed(1)
			.addStock(lemons, 500000)
			.setBuyOrderRange(lemons, 1, 100, -5l, +5l)
			.setSellOrderRange(lemons, 1, 100, -5l, +5l)
			.build();
	}

	@Test
	public void test() {
		
		expect(mockExchange.getLastKnownBestOfferPrice(lemons)).andReturn(50l);
		mockExchange.placeSellOrder(new LimitSellOrder(trader, lemons, 29, 49l));
		expect(mockExchange.getLastKnownBestBidPrice(lemons)).andReturn(50l);
		mockExchange.placeBuyOrder(new LimitBuyOrder(trader, lemons, 2, 45l));

		replayAll ();
		
		trader.speak(mockExchange);
		trader.speak(mockExchange);
		
		verifyAll ();
	
	}

}
