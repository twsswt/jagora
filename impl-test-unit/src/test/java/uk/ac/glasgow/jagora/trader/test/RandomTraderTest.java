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
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.impl.RandomTraders.RandomTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.RandomTraders.RandomTraderPercentageBuilder;

import static org.easymock.EasyMock.expect;

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
		

	}

	@Test
	public void testRandomTrader() {

		trader = new RandomTraderBuilder()
				.setName("alice")
				.setCash(100l)
				.setSeed(1)
				.addStock(lemons, 500000)
				.setTradeRange(lemons, 1, 100, -5l, +5l, -5l, +5l)
				.build();
		
		expect(mockExchange.getLastKnownBestOfferPrice(lemons)).andReturn(50l);
		mockExchange.placeSellOrder(new LimitSellOrder(trader, lemons, 29, 49l));
		expect(mockExchange.getLastKnownBestBidPrice(lemons)).andReturn(50l);
		mockExchange.placeBuyOrder(new LimitBuyOrder(trader, lemons, 2, 45l));

		replayAll ();
		
		trader.speak(mockExchange);
		trader.speak(mockExchange);
		
		verifyAll ();
	
	}

	@Test
	public void testRandomTraderPercentage () {

		trader = new RandomTraderPercentageBuilder()
				.setSeed(8)
				.setName("baba")
				.addStock(lemons,50000)
				.setCash(1000000l)
				.setTradeRange(lemons,1,100,-0.005,+0.005,-0.005,0.005)
				.build();

		expect(mockExchange.getLastKnownBestOfferPrice(lemons)).andReturn(100l);
		mockExchange.placeSellOrder(new LimitSellOrder(trader, lemons, 8,101l));
		expect(mockExchange.getLastKnownBestBidPrice(lemons)).andReturn(100l);
		mockExchange.placeBuyOrder(new LimitBuyOrder(trader, lemons, 54, 100l));

		replayAll ();

		trader.speak(mockExchange);
		trader.speak(mockExchange);

		verifyAll ();

	}


}
