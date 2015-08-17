package uk.ac.glasgow.jagora.trader.impl.RandomTraders;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.AbstractTraderBuilder;
import uk.ac.glasgow.jagora.util.Random;

import java.util.HashMap;
import java.util.Map;


public class HighFrequencyRandomTraderBuilder extends AbstractTraderBuilder {
    private String name;
    private Long cash;

    private Map <Stock, Integer> inventory;

    private Integer seed;

    private RangeData buyRangeDatum;
    private RangeData sellRangeDatum;

    private final Long LongChange = 0l;

    public HighFrequencyRandomTraderBuilder() {
        this.inventory = new HashMap<>();
    }

    @Override
    public HighFrequencyRandomTraderBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public HighFrequencyRandomTraderBuilder setCash(Long cash) {
        this.cash = cash;
        return this;
    }

    public HighFrequencyRandomTraderBuilder addStock(Stock stock,Integer quantity) {
        this.inventory.put(stock,quantity);
        return this;
    }

    public HighFrequencyRandomTraderBuilder setSeed(Integer seed) {
        this.seed = seed;
        return this;
    }

    public HighFrequencyRandomTraderBuilder setTradeRange(
            Stock stock, Integer minQuantity, Integer maxQuantity,
             Double sellLow, Double sellHigh, Double buyLow, Double buyHigh) {
        this.buyRangeDatum = new RangeData(stock, LongChange, LongChange, minQuantity, maxQuantity,buyLow,buyHigh);
        this.sellRangeDatum = new RangeData(stock, LongChange, LongChange, minQuantity, maxQuantity,sellLow,sellHigh);
        return  this;
    }

    public HighFrequencyRandomTraderBuilder setBuyRangeDatum(
            Stock stock, Integer minQuantity, Integer maxQuantity,
            Double sellLow, Double sellHigh, Double buyLow, Double buyHigh) {
        this.buyRangeDatum = new RangeData(stock, LongChange, LongChange, minQuantity, maxQuantity,sellLow,sellHigh);
        return this;
    }

    public HighFrequencyRandomTraderBuilder setSellRangeDatum(
            Stock stock, Integer minQuantity, Integer maxQuantity,
            Double sellLow, Double sellHigh, Double buyLow, Double buyHigh) {
        this.sellRangeDatum = new RangeData(stock, LongChange, LongChange, minQuantity, maxQuantity,sellLow,sellHigh);
        return this;
    }

    public HighFrequencyRandomTrader build () {
        return  new HighFrequencyRandomTrader(name,cash, inventory,
                buyRangeDatum, sellRangeDatum, new Random(seed));
    }
}
