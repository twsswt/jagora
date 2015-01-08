package uk.ac.gla.jagora.trader;

import java.util.HashMap;
import java.util.Map;

import uk.ac.gla.jagora.BuyOrder;
import uk.ac.gla.jagora.SellOrder;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.StockExchangeTraderView;
import uk.ac.gla.jagora.util.Random;

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

	private Map<Stock,Double> lastKnownPrices;

	
	public RandomTrader(
		String name, Double cash, Map<Stock, Integer> inventory,
		Random random, Map<Stock,TradeRange> tradeRanges) {
		
		super(name, cash, inventory);
		this.random = random;
		this.tradeRanges = new HashMap<Stock,TradeRange>(tradeRanges);
		this.lastKnownPrices = new HashMap<Stock,Double>();
	}

	@Override
	public void speak(StockExchangeTraderView traderMarketView) {
		Stock randomStock = random.chooseElement(tradeRanges.keySet());
		
		updateLastKnownPrice(traderMarketView, randomStock);

		if (random.nextBoolean())
			performRandomSellAction(randomStock, traderMarketView);
		else 
			performRandomBuyAction(randomStock, traderMarketView);
	}

	private void updateLastKnownPrice(
			StockExchangeTraderView traderMarketView, Stock stock) {

		Double bestBidPrice = traderMarketView.getBestBidPrice(stock);
		Double bestOfferPrice = traderMarketView.getBestOfferPrice(stock);
		
		if (bestBidPrice != null && bestOfferPrice != null)
			lastKnownPrices.put(stock, (bestBidPrice + bestOfferPrice) / 2 );
	}

	private void performRandomSellAction(
		Stock randomStock, StockExchangeTraderView traderMarketView) {
		
		Integer uncommittedQuantity = 
			getAvailableQuantity(randomStock);
		
		if (uncommittedQuantity > 0){
			
			Integer quantity = random.nextInt(uncommittedQuantity);
			Double price = createRandomPrice(randomStock);
			
			SellOrder sellOrder =
				new SellOrder(this, randomStock, quantity, price);

			placeSafeSellOrder(traderMarketView, sellOrder);
			
		} else {
			SellOrder randomSellOrder = random.chooseElement(openSellOrders);
			if (randomSellOrder != null){
				cancelSafeSellOrder(traderMarketView, randomSellOrder);
			}
		}
	}

	private void performRandomBuyAction(
		Stock stock, StockExchangeTraderView traderMarketView) {
		
		Double price = createRandomPrice(stock);
		
		Integer quantity = createRandomQuantity(stock);
		
		Double availableCash = getAvailableCash();
		
		if (price * quantity < availableCash){
			
			BuyOrder buyOrder =
				new BuyOrder(this, stock, quantity, price);
			
			placeSafeBuyOrder(traderMarketView, buyOrder);
			
		} else {
			BuyOrder randomBuyOrder = random.chooseElement(openBuyOrders);
			if (randomBuyOrder != null){
				cancelSafeBuyOrder(traderMarketView, randomBuyOrder);
			}
		}
		
	}

	private Double createRandomPrice(Stock stock) {
		
		Double lastKnownPrice = lastKnownPrices.get(stock);
		
		TradeRange tradeRange = tradeRanges.get(stock);
			return
				random.nextDouble() * 
					(tradeRange.high - tradeRange.low) + tradeRange.low + lastKnownPrice;
	}
	
	private Integer createRandomQuantity(Stock stock) {
		TradeRange tradeRange = tradeRanges.get(stock);
		return 
			random.nextInt(tradeRange.maxQuantity-tradeRange.minQuantity) + tradeRange.minQuantity;
	}
}