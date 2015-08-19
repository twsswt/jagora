package uk.ac.glasgow.jagora.trader.impl.RandomTraders;


import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.util.Random;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class RandomSpreadCrossingTraderPct extends RandomSpreadCrossingTrader {

    protected static class TradeRangePct extends TradeRange {
        private Double pricePct;

        public TradeRangePct(Integer minQuantity, Integer maxQuantity, Double price) {
            super(minQuantity, maxQuantity, null);
            this.pricePct = price;
        }
    }

    private  final Map<Stock,TradeRangePct> tradeRangePcts;

    protected RandomSpreadCrossingTraderPct(String name, Long cash, Map<Stock, Integer> inventory,
                                         Random random, Map<Stock,TradeRangePct> tradeRangePcts) {
        super(name, cash, inventory, random, new HashMap<Stock,TradeRange>());
        this.tradeRangePcts = tradeRangePcts;
    }

    @Override
    protected Long createRandomPrice(Stock stock, Long basePrice, boolean isSell) {
        TradeRangePct tradeRange = tradeRangePcts.get(stock);
        Long randomPrice =
                (isSell?-1:1) * (long)(random.nextDouble() *  tradeRange.pricePct *basePrice) + basePrice;

        return max(randomPrice, 0l);
    }

    @Override
    protected Integer createRandomQuantity(Stock stock, Integer ceiling) {
        TradeRangePct stockData = tradeRangePcts.get(stock);

        Integer tradeQuantityRange = stockData.maxQuantity - stockData.minQuantity;

        return min(random.nextInt(tradeQuantityRange) + stockData.minQuantity, ceiling);
    }
}
