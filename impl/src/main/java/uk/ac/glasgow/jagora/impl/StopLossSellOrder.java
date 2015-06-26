package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.SafeAbstractTrader;

public class StopLossSellOrder {

    private StockExchangeLevel1View market;
    private Long price;
    private Integer quantity;
    private SafeAbstractTrader trader; //at the moment possible only for SafeAbstractTrader?
    private Stock stock;


    public StopLossSellOrder (StockExchangeLevel1View market, Long price , Stock stock, Integer quantity, SafeAbstractTrader trader){
        this.market = market;
        this.price = price;
        this.quantity = quantity;
        this.trader = trader;
        this.stock = stock;
    }

    public void checkPrice (){

    }

//    private void Sell () {
//        MarketSellOrder order = new MarketSellOrder(trader,stock,quantity,market);
//        trader.placeSafeSellOrder(market,order);
//    }

}
