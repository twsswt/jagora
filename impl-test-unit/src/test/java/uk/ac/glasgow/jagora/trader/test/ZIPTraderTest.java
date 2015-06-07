package uk.ac.glasgow.jagora.trader.test;

import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.ZIPTrader;
import uk.ac.glasgow.jagora.trader.impl.ZIPTraderBuilder;

public class ZIPTraderTest extends EasyMockSupport {
	
	@Rule
    public EasyMockRule rule = new EasyMockRule(this);
	
	private Stock lemons;
	
	private ZIPTraderBuilder traderBuilder;
	
	@Mock
	private StockExchangeLevel2View mockExchange;
	
	@Mock
	private Trader mockTrader;
	
	@Before
	public void setUp() throws Exception {
		
		lemons = new Stock("lemons");
		
		traderBuilder = new ZIPTraderBuilder("test")
			.setCash(10000l)
			.setSeed(1)
			.setMaximumAbsoluteChange(10l)
			.setMaximumRelativeChange(0.1)
			.setLearningRate(0.1);
	}
	
	@Test 
	public void testBuyingStrategy(){
		
		Long limitPrice = 2500l;
		Long floorPrice = 0l;
				
		ZIPTrader trader = traderBuilder
			.addBuyOrderJob(lemons, floorPrice, limitPrice)
			.build();
		
	}

	@Test
	public void testSellingStrategy (){
		
		Long limitPrice = 2000l;	
		Long ceilPrice = 5000l;
		
		ZIPTrader trader = traderBuilder
			.addStock(lemons, 1)
			.addSellOrderJob(lemons, limitPrice, ceilPrice)
			.build();
				
		mockExchange.registerOrderListener(trader);
		mockExchange.registerTradeListener(trader);
		mockExchange.cancelSellOrder(new LimitSellOrder(trader, lemons, 1, 4192l));
		mockExchange.placeSellOrder(new LimitSellOrder(trader, lemons, 1, 3964l));
		
		replayAll();
		
		trader.orderEntered(new OrderEntryEvent(0l, mockTrader, lemons, 1, limitPrice, true));		
		trader.speak(mockExchange);

		verifyAll();
	}
	
}
