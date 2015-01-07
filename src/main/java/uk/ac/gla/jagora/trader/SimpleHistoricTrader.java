package uk.ac.gla.jagora.trader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.OptionalDouble;

import uk.ac.gla.jagora.BuyOrder;
import uk.ac.gla.jagora.SellOrder;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.StockExchangeTraderView;
import uk.ac.gla.jagora.TickerTapeListener;
import uk.ac.gla.jagora.TradeExecutionEvent;
import uk.ac.gla.jagora.util.Random;

/**
 * A trader that places a bid if the current best offer is below the historic
 * trading price; or places an offer if the current best bid is below the
 * historic trading price. The historic average is calculated from all executed
 * trades, without time limit.
 * 
 * @author tws
 *
 */
public class SimpleHistoricTrader extends SafeAbstractTrader implements TickerTapeListener {
	
	private Collection<TradeExecutionEvent> tradeExecutionEvents;

	private Random random;
	
	public SimpleHistoricTrader(
		String name, Double cash, Map<Stock, Integer> inventory, Random random) {
		
		super(name, cash, inventory);
		this.random = random;
		tradeExecutionEvents = new ArrayList<TradeExecutionEvent>();
	}

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		synchronized(tradeExecutionEvents){
			tradeExecutionEvents.add(tradeExecutionEvent);
		}
	}

	@Override
	public void speak(StockExchangeTraderView traderMarketView) {
		Stock randomStock = random.chooseElement(inventory.keySet());
		
		Double averageTradePrice = 
			computeAverageTradePrice(randomStock);
		
		Double bestBidPrice =
			traderMarketView.getBestBidPrice(randomStock);
		
		Double bestOfferPrice = 
			traderMarketView.getBestOfferPrice(randomStock);
		
		if (averageTradePrice != null)
			if (bestOfferPrice != null && bestOfferPrice < averageTradePrice)
				placeBuyOrder(traderMarketView, randomStock, bestOfferPrice);
			else if (bestBidPrice != null && bestBidPrice > averageTradePrice)
				placeSellOrder(traderMarketView, randomStock, bestOfferPrice);		
	}

	private void placeBuyOrder(
		StockExchangeTraderView traderMarketView, Stock randomStock, Double bestOfferPrice) {
		
		Integer quantity = computeAverageQuantity(randomStock);
		
		BuyOrder buyOrder = 
			new BuyOrder(this, randomStock, quantity, bestOfferPrice);
		placeSafeBuyOrder(traderMarketView, buyOrder);
	}

	private void placeSellOrder(
		StockExchangeTraderView traderMarketView, Stock randomStock, Double bestOfferPrice) {
		
		Integer quantity = computeAverageQuantity(randomStock);
		
		SellOrder sellOrder = 
			new SellOrder(this, randomStock, quantity, bestOfferPrice);
		placeSafeSellOrder(traderMarketView, sellOrder);
	}

	
	private Double computeAverageTradePrice(Stock randomStock) {
		// TODO Recalculating the average like this each time isn't very efficient.  
		// It would be better to maintain a running average.
		
		synchronized (tradeExecutionEvents){
			OptionalDouble average = 
				tradeExecutionEvents.stream()
				.filter(tradeExecutionEvent -> tradeExecutionEvent.stock == randomStock)
				.mapToDouble(tradeExecutionEvent -> tradeExecutionEvent.price)
				.average();
			return average.isPresent()? average.getAsDouble() : null;
		}
	}
	
	private Integer computeAverageQuantity(Stock stock) {
		// TODO Recalculating the average like this each time isn't very efficient.  
		// It would be better to maintain a running average.
		
		synchronized (tradeExecutionEvents){
			OptionalDouble average = 
				tradeExecutionEvents.stream()
				.filter(tradeExecutionEvent -> tradeExecutionEvent.stock == stock)
				.mapToInt(tradeExecutionEvent -> tradeExecutionEvent.quantity)
				.average();
			return average.isPresent()? (int)average.getAsDouble() : null;
		}
	}
}
