package uk.ac.glasgow.jagora.impl;


import uk.ac.glasgow.jagora.Market;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.trader.Trader;

public class MarketBuyOrder extends AbstractBuyOrder {

    private  StockExchangeLevel1View market;

    public MarketBuyOrder (Trader trader,Stock stock,Integer quantity,StockExchangeLevel1View market){
        super(trader, stock, quantity);
        this.market =  market;
    }

    @Override
    public Long getPrice () {
        Long price = market.getBestOfferPrice(this.getStock());
        if (price == null) return null;
        return price;
    }
}
