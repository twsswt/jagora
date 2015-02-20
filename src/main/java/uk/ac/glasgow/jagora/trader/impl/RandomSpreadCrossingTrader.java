package uk.ac.glasgow.jagora.trader.impl;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.HashMap;
import java.util.Map;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeTraderView;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.util.Random;

public class RandomSpreadCrossingTrader extends SafeAbstractTrader {
	
	protected static class TradeRange {
		public final Integer quantity;
		public final Double price;
		
		public TradeRange(Integer quantity, Double price){
			this.quantity = quantity;
			this.price = price;
		}
	}
		
	private final Map<Stock,TradeRange> tradeRanges;
	private final Random random;
	
	public RandomSpreadCrossingTrader(
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
		
		Double bestBidPrice = stockExchangeTraderView.getBestBidPrice(stock);
		if (bestBidPrice == null) return;
		
		Integer uncommittedQuantity = 
			getAvailableQuantity(stock);
		
		Integer quantity = 
			createRandomQuantity(stock, uncommittedQuantity);

		if (quantity > 0){
			
			Double price = createRandomPrice(stock, bestBidPrice, true);

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
		
		Double bestOfferPrice = stockExchangeTraderView.getBestOfferPrice(stock);
		if (bestOfferPrice == null) return;

		Double price = createRandomPrice(stock, bestOfferPrice, false);

		Double availableCash = getAvailableCash();
		
		Integer quantity = createRandomQuantity(stock, (int)(availableCash/price));
		
		if (quantity > 0){
			
			BuyOrder buyOrder =
				new LimitBuyOrder(this, stock, quantity, price);
			
			placeSafeBuyOrder(stockExchangeTraderView, buyOrder);
			
		} else {
			BuyOrder buyOrder = random.chooseElement(openBuyOrders);
			if (buyOrder != null)
				cancelSafeBuyOrder(stockExchangeTraderView, buyOrder);
		}
		
	}

	private Double createRandomPrice(Stock stock, Double basePrice, boolean isSell) {
				
		TradeRange tradeRange = tradeRanges.get(stock);
		Double randomPrice = 
			(isSell?-1:1) * random.nextDouble() *  tradeRange.price + basePrice;
		
		return max(randomPrice, 0.0);
	}
	
	private Integer createRandomQuantity(Stock stock, Integer ceiling) {
		TradeRange tradeRange = tradeRanges.get(stock);

		return min(random.nextInt(tradeRange.quantity)+1, ceiling);
	}
}