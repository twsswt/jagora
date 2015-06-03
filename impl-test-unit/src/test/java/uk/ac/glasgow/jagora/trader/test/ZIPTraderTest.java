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
			.setCash(1000.0)
			.setSeed(1)
			.setMaximumAbsoluteChange(1.0)
			.setMaximumRelativeChange(0.1)
			.setLearningRate(0.1);
	}
	
	@Test 
	public void testBuyingStrategy(){
		
		Double limitPrice = 25.0;
		Double floorPrice = 0.0;
				
		ZIPTrader trader = traderBuilder
			.addBuyOrderJob(lemons, floorPrice, limitPrice)
			.build();
		
	}

	@Test
	public void testSellingStrategy (){
		
		Double limitPrice = 20.0;	
		Double ceilPrice = 50.0;
		
		ZIPTrader trader = traderBuilder
			.addStock(lemons, 1)
			.addSellOrderJob(lemons, limitPrice, ceilPrice)
			.build();
				
		mockExchange.registerOrderListener(trader);
		mockExchange.registerTradeListener(trader);
		mockExchange.cancelSellOrder(new LimitSellOrder(trader, lemons, 1, 41.92634572109873));
		mockExchange.placeSellOrder(new LimitSellOrder(trader, lemons, 1, 39.63092350255944));
		
		replayAll();
		
		trader.orderEntered(new OrderEntryEvent(0l, mockTrader, lemons, limitPrice, true));		
		trader.speak(mockExchange);

		verifyAll();
	}
	
}
