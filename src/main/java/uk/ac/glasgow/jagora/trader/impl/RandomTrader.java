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

public class RandomTrader extends SafeAbstractTrader {
	
	protected static class StockData {
		
		public final Stock stock;
		public final Double low, high;
		public final Integer minQuantity, maxQuantity;
		
		private Double lastKnownBestBid;
		private Double lastKnownBestOffer;
		
		public StockData(Stock stock, Double lowPrice, Double highPrice, Integer minQuantity, Integer maxQuantity){
			this.stock = stock;
			this.low = lowPrice;
			this.high = highPrice;
			this.minQuantity = minQuantity;
			this.maxQuantity = maxQuantity;
		}

		public Double updateAndGetOfferPrice(StockExchangeTraderView stockExchangeTraderView) {
			Double currentBestOffer = stockExchangeTraderView.getBestOfferPrice(stock);
			if (currentBestOffer != null)
				lastKnownBestOffer = currentBestOffer;
			return lastKnownBestOffer;
		}
		
		public Double updateAndGetBidPrice(StockExchangeTraderView stockExchangeTraderView) {
			Double currentBestBid = stockExchangeTraderView.getBestBidPrice(stock);
			if (currentBestBid != null)
				lastKnownBestBid = currentBestBid;
			return lastKnownBestBid;
		}

	}
	
	private final Map<Stock,StockData> stockDatas;
	private final Random random;
	
	
	public RandomTrader(
		String name, Double cash, Map<Stock, Integer> inventory,
		Random random, Map<Stock,StockData> stockDatas) {
		
		super(name, cash, inventory);
		this.random = random;
		this.stockDatas = new HashMap<Stock,StockData>(stockDatas);
	}

	@Override
	public void speak(StockExchangeTraderView traderMarketView) {
		Stock randomStock = random.chooseElement(stockDatas.keySet());
		
		if (random.nextBoolean())
			performRandomSellAction(randomStock, traderMarketView);
		else 
			performRandomBuyAction(randomStock, traderMarketView);
	}

	private void performRandomSellAction(
		Stock stock, StockExchangeTraderView stockExchangeTraderView) {
		
		Integer uncommittedQuantity = 
			getAvailableQuantity(stock);
		
		Integer quantity = createRandomQuantity(stock, uncommittedQuantity);
		
		if (quantity > 0){
			
			Double offerPrice = stockDatas.get(stock).updateAndGetOfferPrice(stockExchangeTraderView);
			if (offerPrice == null) return;
			
			Double price = createRandomPrice(stock, offerPrice);

			SellOrder sellOrder =
				new LimitSellOrder(this, stock, quantity, price);

			placeSafeSellOrder(stockExchangeTraderView, sellOrder);
			
		} else {
			SellOrder randomSellOrder = random.chooseElement(openSellOrders);
			if (randomSellOrder != null)
				cancelSafeSellOrder(stockExchangeTraderView, randomSellOrder);
		}
	}

	private void performRandomBuyAction(
		Stock stock, StockExchangeTraderView stockExchangeTraderView) {
		
		Double bestBidPrice = stockDatas.get(stock).updateAndGetBidPrice(stockExchangeTraderView);
		if (bestBidPrice == null) return;
		Double price = createRandomPrice(stock, bestBidPrice);

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

	private Double createRandomPrice(Stock stock, Double midPoint) {
		
		StockData stockData = stockDatas.get(stock);
		Double relativePriceRange = stockData.high - stockData.low;
		Double randomPrice = 
			random.nextDouble() *  relativePriceRange + stockData.low + midPoint;
		
		return max(randomPrice, 0.0);
	}
	
	private Integer createRandomQuantity(Stock stock, Integer ceiling) {
		StockData stockData = stockDatas.get(stock);
		
		Integer relativeRange = 
			stockData.maxQuantity-stockData.minQuantity;
		
		Integer randomQuantity = 
			random.nextInt(relativeRange) + stockData.minQuantity;

		return min(randomQuantity, ceiling);
	}
	
}