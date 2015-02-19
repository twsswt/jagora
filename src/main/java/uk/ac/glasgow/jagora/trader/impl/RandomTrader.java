package uk.ac.glasgow.jagora.trader.impl;

import java.util.HashMap;
import java.util.Map;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeTraderView;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.util.Random;

public class RandomTrader extends SafeAbstractTrader {
	
	public static class TradeRange {
		
		public final Double low;
		public final Double high;
		public final Integer minQuantity;
		public final Integer maxQuantity;
				
		public TradeRange(Double lowPrice, Double highPrice, Integer minQuantity, Integer maxQuantity){
			this.low = lowPrice;
			this.high = highPrice;
			this.minQuantity = minQuantity;
			this.maxQuantity = maxQuantity;
		}
	}
	
	private final Map<Stock,TradeRange> tradeRanges;
	private final Random random;
	
	public RandomTrader(
		String name, Double cash, Map<Stock, Integer> inventory,
		Random random, Map<Stock,TradeRange> tradeRanges) {
		
		super(name, cash, inventory);
		this.random = random;
		this.tradeRanges = new HashMap<Stock,TradeRange>(tradeRanges);
	}

	@Override
	public void speak(StockExchangeTraderView traderMarketView) {
		Stock randomStock = random.chooseElement(tradeRanges.keySet());
		
		if (random.nextBoolean())
			performRandomSellAction(randomStock, traderMarketView);
		else 
			performRandomBuyAction(randomStock, traderMarketView);
	}

	private void performRandomSellAction(
		Stock stock, StockExchangeTraderView stockExchangeTraderView) {
		
		Integer uncommittedQuantity = 
			getAvailableQuantity(stock);
		
		if (uncommittedQuantity > 0){
			
			Integer quantity = random.nextInt(uncommittedQuantity);
			Double bestOfferPrice = stockExchangeTraderView.getBestOfferPrice(stock);
			
			Double price = createRandomPrice(stock, bestOfferPrice);
			
			SellOrder sellOrder =
				new LimitSellOrder(this, stock, quantity, price);

			placeSafeSellOrder(stockExchangeTraderView, sellOrder);
			
		} else {
			SellOrder randomSellOrder = random.chooseElement(openSellOrders);
			if (randomSellOrder != null){
				cancelSafeSellOrder(stockExchangeTraderView, randomSellOrder);
			}
		}
	}

	private void performRandomBuyAction(
		Stock stock, StockExchangeTraderView stockExchangeTraderView) {
		
		Double bestBidPrice = stockExchangeTraderView.getBestBidPrice(stock);
		Double price = createRandomPrice(stock, bestBidPrice);
		
		Integer quantity = createRandomQuantity(stock);
		
		Double availableCash = getAvailableCash();
		
		if (price * quantity < availableCash){
			
			BuyOrder buyOrder =
				new LimitBuyOrder(this, stock, quantity, price);
			
			placeSafeBuyOrder(stockExchangeTraderView, buyOrder);
			
		} else {
			BuyOrder randomBuyOrder = random.chooseElement(openBuyOrders);
			if (randomBuyOrder != null){
				cancelSafeBuyOrder(stockExchangeTraderView, randomBuyOrder);
			}
		}
		
	}

	private Double createRandomPrice(Stock stock, Double bestPrice) {
				
		TradeRange tradeRange = tradeRanges.get(stock);
			return
				random.nextDouble() * 
					(tradeRange.high - tradeRange.low) + tradeRange.low + bestPrice;
	}
	
	private Integer createRandomQuantity(Stock stock) {
		TradeRange tradeRange = tradeRanges.get(stock);
		return 
			random.nextInt(tradeRange.maxQuantity-tradeRange.minQuantity) + tradeRange.minQuantity;
	}
}