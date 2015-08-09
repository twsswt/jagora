package uk.ac.glasgow.jagora.trader.test;

import org.easymock.EasyMock;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.StockWarehouse;
import uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic.MarketMakerBasic;
import uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic.MarketMakerBasicBuilder;


public class MarketMakerBasicMock {

    private StockWarehouse lemonsWarehouse;
    private Stock lemons;

    private MarketMakerBasicBuilder traderBuilder;

    @Rule
    EasyMockRule rule = new EasyMockRule(this);

    @Mock
    StockExchangeLevel2View mockExchange;

    @Before
    public void setUp() {
        lemons = new Stock("lemons");
        lemonsWarehouse = new StockWarehouse(lemons, 1000 );

        traderBuilder = new MarketMakerBasicBuilder("Goldman")
                .addStockWarehouse(lemonsWarehouse)
                .setCash(1000000l)
                .setMarketShare(0.1f)
                .setSeed(1)
                .setSpread(2l)
                .addStock(lemons, Math.round(lemonsWarehouse.getInitialQuantity() * 0.1f));
    }

    @Test
    public void testInventoryPriceAdjustment () {

    }

    @Test
    public void testBuyLiquidityPriceAdjustment () {

    }

    @Test
    public void testSellLiquidityPriceAdjustment () {

    }


}

