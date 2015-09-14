package uk.ac.glasgow.jagora.trader.test;

import java.util.Random;

import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.impl.random.RandomTraderBuilder;
import static org.easymock.EasyMock.expect;

public class RandomTraderTest extends EasyMockSupport {
		
	@Rule
	public EasyMockRule rule = new EasyMockRule(this);
	
	private Level1Trader trader;
	private Stock lemons;

	@Mock
	private Random random;
	
	@Mock
	private StockExchangeLevel1View mockExchange;

	@Before
	public void setUp() throws Exception {
		
		
		lemons  = new Stock("lemons");
		
		trader = new RandomTraderBuilder()
			.setName("alice")
			.setCash(100l)
			.setRandom(random)
			.addStock(lemons, 5)
			.setBuyOrderRange(lemons, 1, 5, -5l, +5l)
			.setSellOrderRange(lemons, 1, 5, -5l, +5l)
			.build();
	}

	@Test
	public void testPlaceOneOrder() {
		LimitBuyOrder limitBuyOrder = new DefaultLimitBuyOrder(trader, lemons, 4, 25l);
		
		expect(random.nextInt(1)).andReturn(0);
				
		expect(random.nextBoolean()).andReturn(false);
		expect(mockExchange.getLastKnownBestBidPrice(lemons)).andReturn(25l);
		expect(random.nextDouble()).andReturn(0.5);
		expect(random.nextInt(5-1)).andReturn(4);

		mockExchange.placeLimitBuyOrder(limitBuyOrder);
		
		replayAll ();
		
		trader.speak(mockExchange);
		
		verifyAll ();

	}
	
	@Test
	public void testPlaceAndCancelAnOrder() {	
		
		LimitBuyOrder limitBuyOrder = new DefaultLimitBuyOrder(trader, lemons, 4, 25l);

		expect(random.nextInt(1)).andReturn(0);
		expect(random.nextBoolean()).andReturn(false);
		expect(mockExchange.getLastKnownBestBidPrice(lemons)).andReturn(25l);
		expect(random.nextDouble()).andReturn(0.5);
		expect(random.nextInt(4)).andReturn(4);

		mockExchange.placeLimitBuyOrder(limitBuyOrder);
		
		expect(random.nextInt(1)).andReturn(0);
		expect(random.nextBoolean()).andReturn(false);
		expect(mockExchange.getLastKnownBestBidPrice(lemons)).andReturn(25l);
		expect(random.nextDouble()).andReturn(0.5);
		expect(random.nextInt(4)).andReturn(0);
		
		expect(random.nextInt(1)).andReturn(0);
		mockExchange.cancelLimitBuyOrder(limitBuyOrder);
		
		replayAll ();
		
		trader.speak(mockExchange);
		trader.speak(mockExchange);
		
		verifyAll ();
	
	}

}
