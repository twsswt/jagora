package uk.ac.glasgow.jagora.trader.ivo.impl;


import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.impl.SafeAbstractTrader;
import uk.ac.glasgow.jagora.trader.impl.random.RandomTrader;
import uk.ac.glasgow.jagora.trader.impl.random.RangeData;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static uk.ac.glasgow.jagora.util.CollectionsRandom.chooseElement;

/**
 * Alternative implementation of a RandomTrader that works
 * with percentage proportions for pricing ranges, instead
 * of absolute variations.
 * 
 * @Ivelin
 * @see RandomTrader
 */
public class RandomTraderPercentage extends SafeAbstractTrader implements Level1Trader {

	private final Map<Stock,PercentageRangeData> sellRangeData;
	private final Map<Stock,PercentageRangeData> buyRangeData;

	protected final Random random;

	
	public RandomTraderPercentage(
		String name, Long cash, Map<Stock, Integer> inventory,
		Random random,
		Map<Stock,PercentageRangeData> sellRangeDatas, Map<Stock,PercentageRangeData> buyRangeDatas) {
		
		super(name, cash, inventory);
		this.random = random;
		this.sellRangeData = new HashMap<Stock,PercentageRangeData>(sellRangeDatas);
		this.buyRangeData = new HashMap<Stock,PercentageRangeData>(buyRangeDatas);
	}

	@Override
	public void speak(StockExchangeLevel1View traderMarketView) {
		Stock randomStock = chooseElement(sellRangeData.keySet(), random);


		if (random.nextBoolean())
			performRandomSellAction(randomStock, traderMarketView);
		else 
			performRandomBuyAction(randomStock, traderMarketView);
	}

	private void performRandomSellAction(
		Stock stock, StockExchangeLevel1View stockExchangeLevel1View) {
		
		Integer uncommittedQuantity = 
			getAvailableQuantity(stock);
		
		PercentageRangeData percentageRangeData = sellRangeData.get(stock);
		
		Integer quantity = createRandomQuantity(uncommittedQuantity, percentageRangeData);

		if (quantity > 0){
			
			Long offerPrice = stockExchangeLevel1View.getLastKnownBestOfferPrice(stock);
			
			if (offerPrice == null) return;
			Long price = createRandomPrice(offerPrice, percentageRangeData);

			LimitSellOrder limitSellOrder =
				new DefaultLimitSellOrder(this, stock, quantity, price);
						
			placeSafeSellOrder(stockExchangeLevel1View, limitSellOrder);
			
		} else {
			LimitSellOrder randomSellOrder = chooseElement(openSellOrders, random);

			if (randomSellOrder != null)
				cancelSafeSellOrder(stockExchangeLevel1View, randomSellOrder);
		}
	}

	private void performRandomBuyAction(
		Stock stock, StockExchangeLevel1View stockExchangeLevel1View) {
		
		Long bestBidPrice = stockExchangeLevel1View.getLastKnownBestBidPrice(stock);
		if (bestBidPrice == null) return;
		
		PercentageRangeData percentageRangeData = buyRangeData.get(stock);
		Long price = createRandomPrice(bestBidPrice, percentageRangeData);

		Long availableCash = getAvailableCash();
		
		Integer quantity = createRandomQuantity( (int)(availableCash/price), percentageRangeData);
		if (quantity > 0){
			
			LimitBuyOrder limitBuyOrder =
				new DefaultLimitBuyOrder(this, stock, quantity, price);

			placeSafeBuyOrder(stockExchangeLevel1View, limitBuyOrder);
			
		} else {
			LimitBuyOrder limitBuyOrder = chooseElement(openBuyOrders, random);
			if (limitBuyOrder != null)
				cancelSafeBuyOrder(stockExchangeLevel1View, limitBuyOrder);
		}
	}

	private Integer createRandomQuantity( Integer ceiling, RangeData percentageRangeData) {
		
		Integer relativeQuantity = 
			percentageRangeData.maxQuantity-percentageRangeData.minQuantity;
		
		Integer randomQuantity = 
			random.nextInt(relativeQuantity) + percentageRangeData.minQuantity;

		return min(randomQuantity, ceiling);
	}
	
	private Long createRandomPrice(Long midPoint, PercentageRangeData percentageRangeData) {
		
		Double percentagePriceRange =
			(percentageRangeData.high - percentageRangeData.low) * random.nextDouble() * midPoint;
		
		Long randomPrice =
			round(percentagePriceRange) + midPoint +
				round(percentageRangeData.low * midPoint);

		return max(randomPrice, 0l);
	}
}
