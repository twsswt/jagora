package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.MarketSellOrder;
import uk.ac.glasgow.jagora.trader.Trader;


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
    public int compareTo(TradePriceListener o) {
        return this.getPrice().compareTo(o.getPrice());
    }
}
