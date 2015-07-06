package uk.ac.glasgow.jagora.ticker.impl;



import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.ticker.PriceListener;
import uk.ac.glasgow.jagora.trader.Trader;

public abstract class TradePriceListener implements PriceListener,Comparable<TradePriceListener> {

    private Long price;
    private Trader trader;
    private Stock stock;
    private StockExchangeLevel1View market;

    public TradePriceListener (Long price, Trader trader, Stock stock, StockExchangeLevel1View market) {
        this.price = price;
        this.trader = trader;
        this.stock = stock;
        this.market = market;
    }

    public Trader getTrader() {
        return trader;
    }

    public Stock getStock() {
        return stock;
    }

    public StockExchangeLevel1View getMarket() {
        return market;
    }

    public Long getPrice() {
        return price;
    }

    @Override
    public abstract void priceReached() ;

    @Override
    public abstract int compareTo(TradePriceListener o);

}
