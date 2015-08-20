package uk.ac.glasgow.jagora.trader.impl.random;


import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.util.Random;

public class RandomTraderPercentageBuilder extends RandomTraderBuilder {
    private final Long LongChange = 0l;

    public RandomTraderPercentageBuilder() {
        super();
    }

    public RandomTraderPercentageBuilder setTradeRange(
            Stock stock, Integer minQuantity, Integer maxQuantity,
            Double sellLow, Double sellHigh, Double buyLow, Double buyHigh) {
        sellRangeData.put(stock, new RangeData(stock, LongChange, LongChange, minQuantity, maxQuantity,sellLow,sellHigh));
        buyRangeData.put(stock, new RangeData(stock, LongChange, LongChange, minQuantity, maxQuantity,buyLow, buyHigh));
        return this;
    }

    @Override
    public RandomTraderPercentageBuilder setSeed(Integer seed) {
        this.seed = seed;
        return this;
    }

    @Override
    public RandomTraderPercentageBuilder addStock(Stock stock, Integer quantity) {
        inventory.put(stock,quantity);
        return this;
    }

    @Override
    public RandomTraderPercentageBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public RandomTraderPercentageBuilder setCash(Long cash) {
        this.cash = cash;
        return this;
    }

    @Override
    public RandomTraderPercentage build() {
        return new RandomTraderPercentage(name, cash, inventory, new Random(seed), sellRangeData, buyRangeData);
    }
}
