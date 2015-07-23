package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.MarketSellOrder;
import uk.ac.glasgow.jagora.trader.Trader;

/**
 * Warning! not safe to use with traders that need their
 * ORDERS for their trading algorithm
 * (This puts order directly on the market)
 */
public class SellTradePriceListener extends TradePriceListener {

    public SellTradePriceListener(Long price, Trader trader, Stock stock, StockExchangeLevel1View market) {
        super(price, trader, stock, market);
    }

    @Override
    public void priceReached() {
        getMarket().placeSellOrder(
                new MarketSellOrder(getTrader(), getStock(), getTrader().getInventory(getStock()), getMarket()));
    }

    @Override
    public int compareTo(TradePriceListener o) {return o.getPrice().compareTo(this.getPrice());}
}
