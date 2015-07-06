package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.MarketBuyOrder;
import uk.ac.glasgow.jagora.trader.Trader;


public class BuyTradePriceListener extends TradePriceListener {

    public BuyTradePriceListener(Long price, Trader trader, Stock stock, StockExchangeLevel1View market) {
        super(price, trader, stock, market);
    }

    @Override
    public void priceReached() {
        getMarket().placeBuyOrder(
                new MarketBuyOrder(getTrader(), getStock(), getTrader().getInventory(getStock()), getMarket()));
        //need to implement a good way to figure out how much inventory it is going to buy!
    }

    /**
     *
     * @param o
     * @return reversed compare method results as needed for this implementation
     */
    @Override
    public int compareTo(TradePriceListener o) {
        return o.getPrice().compareTo(this.getPrice());
    }
}
