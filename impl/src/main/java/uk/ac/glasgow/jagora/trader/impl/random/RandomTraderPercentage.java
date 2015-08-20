package uk.ac.glasgow.jagora.trader.impl.random;


import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.util.Random;

import java.util.Map;

import static java.lang.Math.max;

/**
 * Extension of RandomTrader that works with percentages
 * rather than Long for its spread
 */
public class RandomTraderPercentage extends RandomTrader{

    public RandomTraderPercentage(String name, Long cash, Map<Stock, Integer> inventory,
                                  Random random, Map<Stock, RangeData> sellRangeDatas,
                                  Map<Stock, RangeData> buyRangeDatas) {
        super(name, cash, inventory, random, sellRangeDatas, buyRangeDatas);
    }

    @Override
    protected Long createRandomPrice(Long midPoint, RangeData rangeData) {
        Double relativePriceRange =
                (rangeData.highPct - rangeData.lowPct)*random.nextDouble()*midPoint;
        Long randomPrice =
                Math.round(relativePriceRange) + midPoint +
                        Math.round(rangeData.lowPct*midPoint);

        return max(randomPrice, 0l);
    }
}
