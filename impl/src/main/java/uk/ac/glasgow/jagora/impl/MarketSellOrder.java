package uk.ac.glasgow.jagora.impl;


import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.trader.Trader;

public class MarketSellOrder extends AbstractSellOrder {

    private StockExchangeLevel1View market;
    public MarketSellOrder (Trader trader, Stock stock, Integer quantity,StockExchangeLevel1View market){
        super(trader,stock,quantity);
        this.market = market;
    }

    @Override
    public Long getPrice(){
        Long price = market.getBestBidPrice(this.getStock());
        if (price == null) return  null;
        return price;
    }


}
