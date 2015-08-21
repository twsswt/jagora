package uk.ac.glasgow.jagora.trader.impl.random;


import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.impl.SafeAbstractTrader;
import uk.ac.glasgow.jagora.util.Random;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

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
		Stock randomStock = random.chooseElement(sellRangeData.keySet());


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

			SellOrder sellOrder =
				new LimitSellOrder(this, stock, quantity, price);
						
			placeSafeSellOrder(stockExchangeLevel1View, sellOrder);
			
		} else {
			SellOrder randomSellOrder = random.chooseElement(openSellOrders);

			if (randomSellOrder != null)
				cancelSafeSellOrder(stockExchangeLevel1View, randomSellOrder);
		}
	}

	private void performRandomBuyAction(
		Stock stock, StockExchangeLevel1View stockExchangeLevel1View) {
		
		Long bestBidPrice = stockExchangeLevel1View.getLastKnownBestBidPrice(stock);
		if (bestBidPrice == null) return;
		
		PercentageRangeData percentageRangeData = buyRangeData.get(stock);
		Long price = createRandomPrice( bestBidPrice, percentageRangeData);

		Long availableCash = getAvailableCash();
		
		Integer quantity = createRandomQuantity( (int)(availableCash/price), percentageRangeData);
		if (quantity > 0){
			
			BuyOrder buyOrder =
				new LimitBuyOrder(this, stock, quantity, price);

			placeSafeBuyOrder(stockExchangeLevel1View, buyOrder);
			
		} else {
			BuyOrder buyOrder = random.chooseElement(openBuyOrders);
			if (buyOrder != null)
				cancelSafeBuyOrder(stockExchangeLevel1View, buyOrder);
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