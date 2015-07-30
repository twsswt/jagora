package uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic;


import uk.ac.glasgow.jagora.StockWarehouse;

/**
 * Used to hold market information about a particular stock
 */
public class MarketDatum {

    final StockWarehouse stockWarehouse;

    final int totalQuantity;

    Long lastPriceTraded;
    Boolean lastTradeWasSell;

    Integer buySideLiquidity = 0;
    Integer sellSideLiquidity = 0;

    public MarketDatum(StockWarehouse stockWarehouse) {

        this.stockWarehouse = stockWarehouse;
        this.totalQuantity = stockWarehouse.getInitialQuantity();

    }

    protected void addBuySideLiquidity(Integer quantity){
        buySideLiquidity += quantity;
    }

    protected void addSellSideLiquidity(Integer quantity){
        sellSideLiquidity += quantity;
    }
    //At the moment canceled orders can't be accounted for
    protected void removeLiquidity (Integer quantity){
        buySideLiquidity -= quantity;
        sellSideLiquidity -= quantity;
    }
    protected void setLastPriceTraded(Long lastPriceTraded) {
        this.lastPriceTraded = lastPriceTraded;
    }

}
