package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.Level2Trader;

import java.util.Map;


public class MarketMakerBasic extends SafeAbstractTrader implements Level2Trader {

    public MarketMakerBasic (String name, Long cash, Map<Stock, Integer> inventory){
        super(name,cash,inventory);
        
    }

    @Override
    public void speak(StockExchangeLevel2View traderMarketView) {

    }

}
