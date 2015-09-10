package uk.ac.glasgow.jagora.trader.test;

import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.trader.ivo.impl.HighFrequencyRandomTrader;
import uk.ac.glasgow.jagora.trader.ivo.impl.HighFrequencyRandomTraderBuilder;
import static org.easymock.EasyMock.*;


public class HighFrequencyTraderRandomTest {

	@Rule
	public EasyMockRule rule = new EasyMockRule(this);

	private HighFrequencyRandomTrader trader;
	private Stock lemons;

	@Mock
	private StockExchangeLevel2View mockExchange;

	@Before
	public void setUp() throws Exception {
		lemons  = new Stock("lemons");
	}

	@Test
	public void testRandomOrders () {
		trader = new HighFrequencyRandomTraderBuilder()
			.setName("alice")
			.setCash(1000l)
			.addStock(lemons,1000)
			.setSeed(1)
			.setBuyRangeDatum(lemons, 1, 100, -0.05, 0.05)
			.setSellRangeDatum(lemons, 1, 100, 0.05, 0.05)
			.build();

		mockExchange.registerTradeListener(trader);
		expect(mockExchange.getLastKnownBestOfferPrice(lemons)).andReturn(150l);
		mockExchange.placeLimitSellOrder(new DefaultLimitSellOrder(trader, lemons, 97,158l));
		mockExchange.registerTradeListener(trader);
		mockExchange.cancelLimitSellOrder(new DefaultLimitSellOrder(trader,lemons, 97,158l));
		expect(mockExchange.getLastKnownBestBidPrice(lemons)).andReturn(100l);
		mockExchange.placeLimitBuyOrder(new DefaultLimitBuyOrder(trader,lemons,10, 95l));

		replay(mockExchange);

		trader.speak(mockExchange);
		trader.speak(mockExchange);


		verify(mockExchange);


	}

}
