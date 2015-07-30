package uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic;


import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockWarehouse;
import uk.ac.glasgow.jagora.util.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketMakerBasicBuilder {

    private String name;
    private Long cash = 0l;
    private Integer seed;

    private Map<Stock,Integer> inventory;

    private List<StockWarehouse> stockWarehouses;

    private Float marketShare;
    private Long spread;

    public MarketMakerBasicBuilder (String name) {
        this.name = name;
        inventory = new HashMap<>();
        stockWarehouses = new ArrayList<>();
    }

    public MarketMakerBasicBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MarketMakerBasicBuilder setCash(Long cash) {
        this.cash = cash;
        return this;
    }

    public MarketMakerBasicBuilder setSeed(Integer seed) {
        this.seed = seed;
        return this;
    }

    public MarketMakerBasicBuilder addStock(Stock stock, Integer quantity) {
        inventory.put(stock, quantity);
        return this;
    }

    public MarketMakerBasicBuilder addStockWarehouse(StockWarehouse stockWarehouse){
        stockWarehouses.add(stockWarehouse);
        return this;
    }

    public MarketMakerBasicBuilder setMarketShare(Float marketShare) {
        this.marketShare = marketShare;
        return this;
    }

    public MarketMakerBasicBuilder setSpread(Long spread) {
        this.spread = spread;
        return this;
    }

    public MarketMakerBasic build() {
       return new MarketMakerBasic(
               name,cash,inventory, stockWarehouses,
               marketShare, new Random(seed),spread);
    }
}
