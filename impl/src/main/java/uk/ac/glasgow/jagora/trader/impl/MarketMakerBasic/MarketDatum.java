package uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic;


import uk.ac.glasgow.jagora.StockWarehouse;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to hold market information about a particular stock
 */
public class MarketDatum {

    private class OrderEvent implements Comparable<OrderEvent>  {
        private Long price;
        private Integer quantity;

        @Override
        public int compareTo(OrderEvent o) {
            return 0;
        }
    }

    final StockWarehouse stockWarehouse;

    final int totalQuantity;

    final List<OrderEvent> buyLiquidity = new ArrayList<>();
    final List<OrderEvent> sellLiquidity = new ArrayList<>();

    Long lastPriceTraded = 0l;
    Boolean lastTradeWasSell;

    Integer buySideLiquidity = 0;
    Integer sellSideLiquidity = 0;

    public MarketDatum(StockWarehouse stockWarehouse) {

        this.stockWarehouse = stockWarehouse;
        this.totalQuantity = stockWarehouse.getInitialQuantity();

    }

    protected Boolean liquidityInformation (){return (buySideLiquidity != 0 && sellSideLiquidity != 0);}

    protected void addBuySideLiquidity(Integer quantity, Long price){

        buySideLiquidity += quantity;
    }

    protected void addSellSideLiquidity(Integer quantity, Long price){

        sellSideLiquidity += quantity;
    }

    //At the moment canceled orders can't be accounted for
    protected void removeLiquidity (Integer quantity, Long price){
        if (buySideLiquidity <= 0 || sellSideLiquidity <= 0)
            return;

        buySideLiquidity -= quantity;
        sellSideLiquidity -= quantity;
    }

    protected void removeBuySideLiquidity(Integer quantity){buySideLiquidity -=quantity;
     }
    protected void removeSellSideLiquidity (Integer quantity){sellSideLiquidity -= quantity;
     }

    protected void setLastPriceTraded(Long lastPriceTraded) {
        this.lastPriceTraded = lastPriceTraded;
    }

    protected void setLastTradeDirection(Boolean lastTradeWasSell) {this.lastTradeWasSell = lastTradeWasSell;}

    @Override
    public String toString() {
        return String.format("Buy Side liquidity is " + this.buySideLiquidity
        + " SellSide liquidity is " + this.sellSideLiquidity);
    }
}
