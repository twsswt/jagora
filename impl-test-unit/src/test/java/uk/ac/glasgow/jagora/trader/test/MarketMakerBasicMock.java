package uk.ac.glasgow.jagora.trader.test;

import org.easymock.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.impl.AbstractBuyOrder;
import uk.ac.glasgow.jagora.impl.AbstractOrder;
import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic.MarketMakerBasic;
import uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic.MarketMakerBasicBuilder;

import static org.easymock.EasyMock.*;


public class MarketMakerBasicMock {

    private StockWarehouse lemonsWarehouse;
    private Stock lemons;

    private MarketMakerBasicBuilder traderBuilder;

    @Rule
    public EasyMockRule rule = new EasyMockRule(this);

    @Mock
    StockExchangeLevel2View mockExchange = createMock(StockExchangeLevel2View.class);

    @Before
    public void setUp() {
        lemons = new Stock("lemons");
        lemonsWarehouse = new StockWarehouse(lemons, 10000 );

        traderBuilder = new MarketMakerBasicBuilder("Goldman")
                .addStockWarehouse(lemonsWarehouse)
                .setCash(1000000l)
                .setMarketShare(0.1f)
                .setSeed(1)
                .setSpread(10l)
                .addStock(lemons, Math.round(lemonsWarehouse.getInitialQuantity() * 0.1f));
    }

    @Test
    public void testInventoryPriceAdjustment () {


        Capture<BuyOrder> captured = newCapture();
        expect(mockExchange.getBestOfferPrice(lemons));

        MarketMakerBasic marketMaker = traderBuilder.build();

        //Put some orders in the marketMaker so that liquidity calculations are not affected
        marketMaker.orderEntered(new OrderEntryEvent(null,null,lemons, 1000,1000l, OrderEntryEvent.OrderDirection.BUY));
        marketMaker.orderEntered(new OrderEntryEvent(null,null,lemons, 1000,1000l, OrderEntryEvent.OrderDirection.SELL));

        //Put at least one trade executed so the pricing algorithm can function properly
        marketMaker.tradeExecuted(new TradeExecutionEvent(lemons,null,null, null, 10000l, 500, true ));

        marketMaker.speak(mockExchange);


    }

    @Test
    public void testBuyLiquidityPriceAdjustment () {

    }

    @Test
    public void testSellLiquidityPriceAdjustment () {

    }


}

