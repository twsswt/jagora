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
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.impl.InstitutionalInvestorTrader;
import uk.ac.glasgow.jagora.trader.impl.InstitutionalInvestorTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.MarginalTraderBuilder;

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

		alice = new MarginalTraderBuilder("alice",100l,1)
			.addStock(lemons, 10)
			.build();
	}

	@Test
	public void test() {	
		
		expect(mockExchange.getBestOfferPrice(lemons)).andReturn(1l);
		mockExchange.placeBuyOrder(new LimitBuyOrder(alice, lemons, 5, 2l));
		
		replayAll();
		https://docs.oracle.com/javase/tutorial/essential/concurrency/syncmeth.html
		alice.speak(mockExchange);
		
		verifyAll();
		

	}



}
