package uk.ac.glasgow.jagora.trader.test;

import static uk.ac.glasgow.jagora.ticker.OrderEvent.OrderDirection.BUY;
import static uk.ac.glasgow.jagora.ticker.OrderEvent.OrderDirection.SELL;
import static uk.ac.glasgow.jagora.ticker.LimitOrderEvent.Action.PLACED;

import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.ticker.LimitOrderEvent;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.zip.ZIPTrader;
import uk.ac.glasgow.jagora.trader.impl.zip.ZIPTraderBuilder;

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
			.setLearningRate(0.1)
			.setMomentum(0.0)
			.setMaxInitialProfit(1.0)
			.setMinInitialProfit(0.0);
	}
	
	/**
	 * A regression test.  Fundamentally the placed price should be higher than the cancelled price.
	 */
	@Test 
	public void testBuyingStrategy(){
		
		Long limitPrice = 2500l;
		Long floorPrice = 0l;
				
		ZIPTrader trader = traderBuilder
			.addBuyOrderJobSpecification(lemons, floorPrice, limitPrice)
			.build();
		
		mockExchange.registerOrderListener(trader);
		mockExchange.registerTradeListener(trader);
		mockExchange.cancelLimitBuyOrder(new DefaultLimitBuyOrder(trader, lemons, 1, 672l));
		mockExchange.placeLimitBuyOrder(new DefaultLimitBuyOrder(trader, lemons, 1, 865l));
		
		replayAll();
		
		trader.limitOrderEvent(new LimitOrderEvent(0l, mockTrader, lemons, 1, BUY, limitPrice, PLACED));		
		trader.speak(mockExchange);

		verifyAll();
		
	}

	/**
	 * A regression test.  Fundamentally the placed price should be lower than the cancelled price.
	 */

	@Test
	public void testSellingStrategy (){
		
		Long limitPrice = 2000l;	
		Long ceilPrice = 5000l;
		
		ZIPTrader trader = traderBuilder
			.addStock(lemons, 1)
			.addSellOrderJobSpecification(lemons, limitPrice, ceilPrice)
			.build();
				
		mockExchange.registerOrderListener(trader);
		mockExchange.registerTradeListener(trader);
		mockExchange.cancelLimitSellOrder(new DefaultLimitSellOrder(trader, lemons, 1, 4192l));
		mockExchange.placeLimitSellOrder(new DefaultLimitSellOrder(trader, lemons, 1, 3964l));
		
		replayAll();
		
		trader.limitOrderEvent(new LimitOrderEvent(0l, mockTrader, lemons, 1, SELL, limitPrice, PLACED));		
		trader.speak(mockExchange);

		verifyAll();
	}
	
}
