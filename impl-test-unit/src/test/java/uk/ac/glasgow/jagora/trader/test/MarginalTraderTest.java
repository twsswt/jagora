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
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.impl.MarginalTraderBuilder;

import static org.easymock.EasyMock.expect;

public class MarginalTraderTest extends EasyMockSupport {
		
	@Rule
	public EasyMockRule rule = new EasyMockRule(this);
	
	private Stock lemons;
	
	private Level1Trader alice;
	
	@Mock
	private StockExchangeLevel1View mockExchange;

	@Before
	public void setUp() throws Exception {
				
		lemons = new Stock("lemons");

		alice = new MarginalTraderBuilder()
			.setName("alice")
			.setCash(100l)
			.addStock(lemons, 10)
			.setSeed(1)
			.build();
	}

	@Test
	public void test() {	
		
		mockExchange.placeLimitBuyOrder(new DefaultLimitBuyOrder(alice, lemons, 50, 2l));		
		expect(mockExchange.getBestOfferPrice(lemons)).andReturn(1l);
		
		replayAll();
		alice.speak(mockExchange);		
		verifyAll();
		

	}



}
