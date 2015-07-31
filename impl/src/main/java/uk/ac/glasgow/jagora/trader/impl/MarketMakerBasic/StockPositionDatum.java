package uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;

/**
 * Used to hold information about the current position taken with a particular stock
 */
public class StockPositionDatum {

    final Stock stock;
    final Integer sharesAimed;

    BuyOrder currentBuyOrder = null;
    SellOrder currentSellOrder = null;

    Long newBuyPrice = 0l;
    Long newSellPrice = 0l;

    StockPositionDatum (Float marketShare, Integer initialQuantity,Stock stock) {
        sharesAimed = Math.round(marketShare*initialQuantity.floatValue());
        this.stock = stock;
    }



    void setNewBuyPrice(Long price){this.newBuyPrice = price;}

    void setNewSellPrice(Long price){
         this.newSellPrice = price;
    }
}
