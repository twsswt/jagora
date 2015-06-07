package uk.ac.glasgow.jagora.trader.impl;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.HashMap;
import java.util.Map;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.util.Random;

public class RandomTrader extends SafeAbstractTrader implements Level1Trader {
		
	protected static class RangeData {
		
		public final Stock stock;
		public final Long low, high;
		public final Integer minQuantity, maxQuantity;
		
		public RangeData(Stock stock, Long lowPrice, Long highPrice, Integer minQuantity, Integer maxQuantity){
			this.stock = stock;
			this.low = lowPrice;
			this.high = highPrice;
			this.minQuantity = minQuantity;
			this.maxQuantity = maxQuantity;
		}

	}
	
	private final Map<Stock,RangeData> sellRangeData;
	private final Map<Stock,RangeData> buyRangeData;

	private final Random random;
	
	
	public RandomTrader(
		String name, Long cash, Map<Stock, Integer> inventory,
		Random random, Map<Stock,RangeData> sellRangeDatas, Map<Stock,RangeData> buyRangeDatas) {
		
		super(name, cash, inventory);
		this.random = random;
		this.sellRangeData = new HashMap<Stock,RangeData>(sellRangeDatas);
		this.buyRangeData = new HashMap<Stock,RangeData>(buyRangeDatas);
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
		RangeData rangeData = sellRangeData.get(stock);
		
		Integer quantity = createRandomQuantity(stock, uncommittedQuantity, rangeData);
		
		if (quantity > 0){
			
			Long offerPrice = stockExchangeLevel1View.getLastKnownBestOfferPrice(stock);
			
			if (offerPrice == null) return;
			Long price = createRandomPrice(stock, offerPrice, rangeData);

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
		RangeData rangeData = buyRangeData.get(stock);
		Long price = createRandomPrice(stock, bestBidPrice, rangeData);

		Long availableCash = getAvailableCash();
		
		Integer quantity = createRandomQuantity(stock, (int)(availableCash/price), rangeData);
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

	private Long createRandomPrice(Stock stock, Long midPoint, RangeData rangeData) {
		
		Long relativePriceRange = rangeData.high - rangeData.low;
		Long randomPrice = 
			(long)(random.nextDouble() *  relativePriceRange) + rangeData.low + midPoint;
		
		return max(randomPrice, 0l);
	}
	
	private Integer createRandomQuantity(Stock stock, Integer ceiling, RangeData rangeData) {
		
		Integer relativeRange = 
			rangeData.maxQuantity-rangeData.minQuantity;
		
		Integer randomQuantity = 
			random.nextInt(relativeRange) + rangeData.minQuantity;

		return min(randomQuantity, ceiling);
	}
}