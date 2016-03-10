package uk.ac.glasgow.jagora.trader.test;

import static java.util.Arrays.asList;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

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
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.marketmaker.MarketMaker;
import uk.ac.glasgow.jagora.trader.impl.marketmaker.MarketMakerBuilder;


public class MarketMakerMock extends EasyMockSupport{

	private Stock lemons;

	private MarketMakerBuilder marketMakerBuilder;
	
	@Mock
	private Trader alice;
	
	@Rule
	public EasyMockRule rule = new EasyMockRule(this);

	@Mock
	public StockExchangeLevel2View mockExchange;


	@Before
	public void setUp() {
		lemons = new Stock("lemons");

		marketMakerBuilder = new MarketMakerBuilder()
			.setName("Goldman")
			.setCash(100000000l);
	}

	/**
	 * The market maker should ensure liquidity in an empty
	 * market by creating a position with a bid-ask spread
	 * as wide as the available price range.
	 */
	@Test
	public void testNewMarket() throws Exception {
		
		MarketMaker marketMaker = 
			marketMakerBuilder
				.addStock(lemons, 500)
				.addMarketPositionSpecification(lemons, 500, 250)
				.build();
				
		expect(mockExchange.getBestBidPrice(lemons)).andReturn(null);
		expect(mockExchange.getBuyLimitOrders(lemons)).andReturn(new ArrayList<>());
		mockExchange.placeLimitBuyOrder(new DefaultLimitBuyOrder(marketMaker, lemons, 250, 1l));
		
		expect(mockExchange.getBestOfferPrice(lemons)).andReturn(Long.MAX_VALUE);
		expect(mockExchange.getSellLimitOrders(lemons)).andReturn(new ArrayList<>());
		mockExchange.placeLimitSellOrder(new DefaultLimitSellOrder(marketMaker, lemons, 250, Long.MAX_VALUE));

		replayAll();

		marketMaker.speak(mockExchange);

		verify(mockExchange);
		
	}
	
	/**
	 * The market maker should ensure that a minimal
	 * position is maintained in a liquid market by placing
	 * small competitive symmetric position.
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	@Test
	public void testLiquidMarket () {
		
		MarketMaker marketMaker = 
			marketMakerBuilder
				.addStock(lemons, 500)
				.addMarketPositionSpecification(lemons, 500, 250)
				.build();


		List buySideDepth = asList(new DefaultLimitBuyOrder(alice, lemons, 250, 49l));
		
		List sellSideDepth = asList(new DefaultLimitSellOrder(alice, lemons, 250, 51l));
		
		expect(mockExchange.getBestBidPrice(lemons)).andReturn(49l);
		expect(mockExchange.getBuyLimitOrders(lemons)).andReturn(buySideDepth);
		mockExchange.placeLimitBuyOrder(new DefaultLimitBuyOrder(marketMaker, lemons, 1, 48l));
		
		expect(mockExchange.getBestOfferPrice(lemons)).andReturn(51l);
		expect(mockExchange.getSellLimitOrders(lemons)).andReturn(sellSideDepth);
		mockExchange.placeLimitSellOrder(new DefaultLimitSellOrder(marketMaker, lemons, 1, 51l));

		replayAll();

		marketMaker.speak(mockExchange);

		verify(mockExchange);
	}
	

	/**
	 * The market maker should seek to recover a stock
	 * shortage. In a liquid market with sufficient depth
	 * this should mean creating a competitive buy
	 * side position.
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	@Test
	public void testStockShortageInLiquidMarket () {
		
		MarketMaker marketMaker = 
			marketMakerBuilder
				.addStock(lemons, 400)
				.addMarketPositionSpecification(lemons, 500, 250)
				.build();


		List buySideDepth = asList(new DefaultLimitBuyOrder(alice, lemons, 250, 49l));
		List sellSideDepth = asList(new DefaultLimitSellOrder(alice, lemons, 250, 51l));
		
		expect(mockExchange.getBestBidPrice(lemons)).andReturn(49l);
		expect(mockExchange.getBuyLimitOrders(lemons)).andReturn(buySideDepth);
		mockExchange.placeLimitBuyOrder(new DefaultLimitBuyOrder(marketMaker, lemons, 100, 49l));
		
		expect(mockExchange.getBestOfferPrice(lemons)).andReturn(51l);
		expect(mockExchange.getSellLimitOrders(lemons)).andReturn(sellSideDepth);
		mockExchange.placeLimitSellOrder(new DefaultLimitSellOrder(marketMaker, lemons, 1, 71l));

		replayAll();

		marketMaker.speak(mockExchange);

		verify(mockExchange);
	}
	
	/**
	 * The market maker should seek to reduce a stock
	 * excess. In a liquid market with sufficient depth
	 * this should mean creating a competitive sell
	 * side position.
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	@Test
	public void testStockExcessInLiquidMarket () {
		
		MarketMaker marketMaker = 
			marketMakerBuilder
				.addStock(lemons, 600)
				.addMarketPositionSpecification(lemons, 500, 250)
				.build();


		List buySideDepth = asList(new DefaultLimitBuyOrder(alice, lemons, 250, 49l));
		List sellSideDepth = asList(new DefaultLimitSellOrder(alice, lemons, 250, 51l));
		
		expect(mockExchange.getBestBidPrice(lemons)).andReturn(49l);
		expect(mockExchange.getBuyLimitOrders(lemons)).andReturn(buySideDepth);
		mockExchange.placeLimitBuyOrder(new DefaultLimitBuyOrder(marketMaker, lemons, 1, 29l));
		
		expect(mockExchange.getBestOfferPrice(lemons)).andReturn(51l);
		expect(mockExchange.getSellLimitOrders(lemons)).andReturn(sellSideDepth);
		mockExchange.placeLimitSellOrder(new DefaultLimitSellOrder(marketMaker, lemons, 100, 51l));

		replayAll();

		marketMaker.speak(mockExchange);

		verify(mockExchange);
	}

	/**
	 * The market maker should seek to recover a stock
	 * shortage. This needs to be balanced with the
	 * increased risk of trading in an illiquid market, so
	 * spreads should widen to account for this.
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	@Test
	public void testStockShortageInIlliquidMarket () {
		
		MarketMaker marketMaker = 
			marketMakerBuilder
				.addStock(lemons, 400)
				.addMarketPositionSpecification(lemons, 500, 250)
				.build();


		List buySideDepth = asList(new DefaultLimitBuyOrder(alice, lemons, 50, 49l));
		List sellSideDepth = asList(new DefaultLimitSellOrder(alice, lemons, 50, 51l));
		
		expect(mockExchange.getBestBidPrice(lemons)).andReturn(49l);
		expect(mockExchange.getBuyLimitOrders(lemons)).andReturn(buySideDepth);
		mockExchange.placeLimitBuyOrder(new DefaultLimitBuyOrder(marketMaker, lemons, 200, 29l));
		
		expect(mockExchange.getBestOfferPrice(lemons)).andReturn(51l);
		expect(mockExchange.getSellLimitOrders(lemons)).andReturn(sellSideDepth);
		mockExchange.placeLimitSellOrder(new DefaultLimitSellOrder(marketMaker, lemons, 200, 112l));

		replayAll();

		marketMaker.speak(mockExchange);

		verify(mockExchange);
	}
	
}

