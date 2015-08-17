package uk.ac.glasgow.jagora.trader.test;

import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.trader.impl.RandomTraders.HighFrequencyRandomTrader;
import uk.ac.glasgow.jagora.trader.impl.RandomTraders.HighFrequencyRandomTraderBuilder;

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
                .setTradeRange(lemons, 1, 100, -0.05, +0.05, -0.05, 0.05)
                .build();

        expect(mockExchange.getLastKnownBestOfferPrice(lemons)).andReturn(150l);
        expect(mockExchange.getLastKnownBestBidPrice(lemons)).andReturn(100l);

        mockExchange.registerTradeListener(trader);
        mockExchange.registerTradeListener(trader);

        mockExchange.placeSellOrder(new LimitSellOrder(trader, lemons, 97,145l));
        mockExchange.cancelSellOrder(new LimitSellOrder(trader,lemons, 97,145l));
        mockExchange.placeBuyOrder(new LimitBuyOrder(trader,lemons,10, 95l));

        replay(mockExchange);

        trader.speak(mockExchange);
        trader.speak(mockExchange);


        verify(mockExchange);


    }

}
