package uk.ac.gla.jagora.trader;

import static uk.ac.gla.jagora.util.RandomChoice.chooseRandomElement;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import uk.ac.gla.jagora.AbstractTrader;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.TraderMarketView;
import uk.ac.gla.jagora.orderdrivenmarket.BuyOrder;
import uk.ac.gla.jagora.orderdrivenmarket.SellOrder;

public class RandomOrderTrader extends AbstractTrader {
	
	public static class TradeRange {
		public final Double low;
		public final Double high;
		
		public TradeRange(Double low, Double high){
			this.low = low;
			this.high = high;
		}
	}
	
	private final Map<Stock,TradeRange> tradeRanges;
	private Random random;

	public RandomOrderTrader(
		String name, Double cash, Map<Stock, Integer> inventory,
		Integer seed, Map<Stock,TradeRange> tradeRanges) {
		
		super(name, cash, inventory);
		this.random = new Random(seed);
		this.tradeRanges = new HashMap<Stock,TradeRange>();
		tradeRanges.putAll(tradeRanges);
	}

	@Override
	public void speak(TraderMarketView traderMarket) {
		
		
		if (random.nextBoolean()){
			Stock randomStock = chooseRandomElement(tradeRanges.keySet());
			if (random.nextBoolean())
				performRandomSellOrder(randomStock, traderMarket);
			else 
				performRandomBuyOrder(randomStock, traderMarket);
			
		} else 
			if (random.nextBoolean())
				;
	}

	private void performRandomSellOrder(Stock randomStock, TraderMarketView marketState) {
		
		Integer currentQuantity = 
			inventory.getOrDefault(randomStock, 0);
		
		if (currentQuantity > 0){
			
			Integer quantity = random.nextInt(currentQuantity);
			
			Double price = createRandomPrice(randomStock);
			
			SellOrder sellOrder = new SellOrder(this, randomStock, quantity, price);
			
		} 
	}
	
	private void performRandomBuyOrder(Stock randomStock, TraderMarketView marketState) {
		
		Double price = createRandomPrice(randomStock);
		
		if (price < getCash()){
			Integer quantity = (int)(getCash()/price);
			
			BuyOrder buyOrder =
				new BuyOrder(this, randomStock, quantity, price);
		}			
		
	}
	
	private Double createRandomPrice(Stock randomStock) {
		TradeRange tradeRange = tradeRanges.get(randomStock);
		return
			random.nextDouble() * (tradeRange.high - tradeRange.low) + tradeRange.low;
	}
}