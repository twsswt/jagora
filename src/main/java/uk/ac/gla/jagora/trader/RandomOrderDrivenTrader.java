package uk.ac.gla.jagora.trader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uk.ac.gla.jagora.BuyOrder;
import uk.ac.gla.jagora.SellOrder;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.StockExchangeTraderView;
import uk.ac.gla.jagora.orderdriven.OrderDrivenStockExchangeTraderView;
import uk.ac.gla.jagora.util.Random;

public class RandomOrderDrivenTrader extends AbstractTrader {
	
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
	
	private final Collection<BuyOrder> buyOrders;
	private final Collection<SellOrder> sellOrders;

	public RandomOrderDrivenTrader(
		String name, Double cash, Map<Stock, Integer> inventory,
		Random random, Map<Stock,TradeRange> tradeRanges) {
		
		super(name, cash, inventory);
		this.random = random;
		this.tradeRanges = new HashMap<Stock,TradeRange>(tradeRanges);
		this.buyOrders = new ArrayList<BuyOrder>();
		this.sellOrders = new ArrayList<SellOrder>();
	}

	@Override
	public void speak(StockExchangeTraderView traderMarketView){
		speak((OrderDrivenStockExchangeTraderView)traderMarketView);
	}
	
	public void speak(OrderDrivenStockExchangeTraderView traderOrderDrivenMarketView) {
		Stock randomStock = random.chooseElement(tradeRanges.keySet());
		if (random.nextBoolean())
			performRandomSellAction(randomStock, traderOrderDrivenMarketView);
		else 
			performRandomBuyAction(randomStock, traderOrderDrivenMarketView);
	}

	private void performRandomSellAction(
		Stock randomStock, OrderDrivenStockExchangeTraderView traderMarketView) {
		
		Integer uncommittedQuantity = 
			getAvailableQuantity(randomStock);
		
		if (uncommittedQuantity > 0){
			
			Integer quantity = random.nextInt(uncommittedQuantity);
			Double price = createRandomPrice(randomStock);
			
			SellOrder sellOrder =
				new SellOrder(this, randomStock, quantity, price);

			traderMarketView.registerSellOrder(sellOrder);
			sellOrders.add(sellOrder);
			
		} else {
			SellOrder randomSellOrder = random.chooseElement(sellOrders);
			traderMarketView.cancelSellOrder(randomSellOrder);
			sellOrders.remove(randomSellOrder);
		}
	}
	
	private void performRandomBuyAction(
		Stock stock, OrderDrivenStockExchangeTraderView traderOrderDrivenMarketView) {
		
		Double price = createRandomPrice(stock);
		
		Integer quantity = createRandomQuantity(stock);
		
		Double availableCash = getAvailableCash();
		
		if (price * quantity < availableCash){
			
			BuyOrder buyOrder =
				new BuyOrder(this, stock, quantity, price);
			
			traderOrderDrivenMarketView.registerBuyOrder(buyOrder);
			buyOrders.add(buyOrder);
			
		} else {
			BuyOrder randomBuyOrder = random.chooseElement(buyOrders);
			traderOrderDrivenMarketView.cancelBuyOrder(randomBuyOrder);
			buyOrders.remove(randomBuyOrder);
		}
		
	}

	private Double getAvailableCash() {
		Double committedCash =
			buyOrders.stream()
			.mapToDouble(buyOrder -> (buyOrder.price * buyOrder.getRemainingQuantity()))
			.sum();
		
		return getCash() - committedCash;
	}

	private Integer getAvailableQuantity(Stock stock) {
		
		Integer committedQuantity = 
			sellOrders.stream()
			.mapToInt(sellOrder -> sellOrder.getRemainingQuantity())
			.sum();
		
		return inventory.getOrDefault(stock, 0) - committedQuantity;
	}

	private Double createRandomPrice(Stock stock) {
		TradeRange tradeRange = tradeRanges.get(stock);
		return
			random.nextDouble() * (tradeRange.high - tradeRange.low) + tradeRange.low;
	}
	
	private Integer createRandomQuantity(Stock stock) {
		TradeRange tradeRange = tradeRanges.get(stock);
		return 
			random.nextInt(tradeRange.maxQuantity-tradeRange.minQuantity) + tradeRange.minQuantity;
	}
}