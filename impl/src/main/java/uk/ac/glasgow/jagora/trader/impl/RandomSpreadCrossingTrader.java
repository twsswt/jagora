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

public class RandomSpreadCrossingTrader extends SafeAbstractTrader implements Level1Trader {
	
	protected static class TradeRange {
		public final Integer minQuantity;
		public final Integer maxQuantity;
		public final Long price;
		
		public TradeRange(Integer minQuantity, Integer maxQuantity, Long price){
			this.minQuantity = minQuantity;
			this.maxQuantity = maxQuantity;
			this.price = price;
		}
	}
		
	private final Map<Stock,TradeRange> tradeRanges;
	private final Random random;
	
	public RandomSpreadCrossingTrader(
		String name, Long cash, Map<Stock, Integer> inventory,
		Random random, Map<Stock,TradeRange> tradeRanges) {
		
		super(name, cash, inventory);
		this.random = random;
		this.tradeRanges = new HashMap<Stock,TradeRange>(tradeRanges);
	}

	@Override
	public void speak(StockExchangeLevel1View traderMarketView) {
		Stock randomStock = random.chooseElement(tradeRanges.keySet());
		
		if (random.nextBoolean())
			performRandomSellAction(randomStock, traderMarketView);
		else 
			performRandomBuyAction(randomStock, traderMarketView);
	}

	private void performRandomSellAction(
		Stock stock, StockExchangeLevel1View stockExchangeLevel1View) {
		
		Long bestBidPrice = stockExchangeLevel1View.getBestBidPrice(stock);
		if (bestBidPrice == null) return;
		
		Integer uncommittedQuantity = 
			getAvailableQuantity(stock);
		
		Integer quantity = 
			createRandomQuantity(stock, uncommittedQuantity);

		if (quantity > 0){
			
			Long price = createRandomPrice(stock, bestBidPrice, true);

			SellOrder sellOrder =
				new LimitSellOrder(this, stock, quantity, price);

			placeSafeSellOrder(stockExchangeLevel1View, sellOrder);
			
		} else {
			SellOrder randomSellOrder = random.chooseElement(openSellOrders);
			if (randomSellOrder != null){
				cancelSafeSellOrder(stockExchangeLevel1View, randomSellOrder);
			}
		}
	}

	private void performRandomBuyAction(
		Stock stock, StockExchangeLevel1View stockExchangeLevel1View) {
		
		Long bestOfferPrice = stockExchangeLevel1View.getBestOfferPrice(stock);
		if (bestOfferPrice == null) return;

		Long price = createRandomPrice(stock, bestOfferPrice, false);

		Long availableCash = getAvailableCash();
		
		Integer quantity = createRandomQuantity(stock, (int)(availableCash/price));
		
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

	private Long createRandomPrice(Stock stock, Long basePrice, boolean isSell) {
				
		TradeRange tradeRange = tradeRanges.get(stock);
		Long randomPrice = 
			(isSell?-1:1) * (long)(random.nextDouble() *  tradeRange.price) + basePrice;
		
		return max(randomPrice, 0l);
	}
	
	private Integer createRandomQuantity(Stock stock, Integer ceiling) {
		TradeRange stockData = tradeRanges.get(stock);
		
		Integer tradeQuantityRange = stockData.maxQuantity - stockData.minQuantity;
		
		return min(random.nextInt(tradeQuantityRange) + stockData.minQuantity, ceiling);
	}
}