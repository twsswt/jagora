package uk.ac.glasgow.jagora.test;

import org.junit.*;
import org.junit.Test;
import uk.ac.glasgow.jagora.MarketFactory;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockWarehouse;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.pricer.impl.SellOrderPricer;
import uk.ac.glasgow.jagora.test.stub.StubTrader;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

import static org.testng.AssertJUnit.assertEquals;



public class StockWarehouseTest {

    private static final Stock lemons = new Stock("lemons");

    private StubTrader alice;
    private StubTrader bruce;
    private StubTrader george;

    private StockWarehouse stockWarehouse;

    @Before
    public void setUp() {
        stockWarehouse = new StockWarehouse(lemons,1000);
    }

    @Test(expected = Exception.class)
    public void testStockWarehouse() throws Exception{
        alice = new StubTraderBuilder("alice", 50000l)
                .addStock(stockWarehouse.getStock(),stockWarehouse.getStock(400))
                .build();

        george = new StubTraderBuilder("george", 50000l)
                .addStock(stockWarehouse.getStock(), stockWarehouse.getRemainingStock())
                .build();

        assertEquals("",alice.getInventory(lemons),(Integer) 400);
        assertEquals("",george.getInventory(lemons),(Integer) 600);

        bruce = new StubTraderBuilder("bruce", 50000l)
                .addStock(stockWarehouse.getStock(), stockWarehouse.getStock(400))
                .build();
    }
}
