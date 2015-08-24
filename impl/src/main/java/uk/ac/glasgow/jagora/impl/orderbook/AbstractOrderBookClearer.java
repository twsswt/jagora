package uk.ac.glasgow.jagora.impl.orderbook;

import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.List;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.TradeExecutionException;
import uk.ac.glasgow.jagora.impl.DefaultTrade;
import uk.ac.glasgow.jagora.pricer.TradePricer;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

public abstract class AbstractOrderBookClearer<S extends SellOrder, B extends BuyOrder> {

	private OrderBook<S> sellOrderBook;
	private OrderBook<B> buyOrderBook;
	
	private TradePricer<S,B> tradePricer;
	
	private Stock stock;
	private World world;
	
	public AbstractOrderBookClearer(
		OrderBook<S> sellOrderBook,
		OrderBook<B> buyOrderBook,
		Stock stock,
		World world,
		TradePricer<S,B> tradePricer){
		
		this.sellOrderBook = sellOrderBook;
		this.buyOrderBook = buyOrderBook;
		this.stock = stock;
		this.world = world;
		this.tradePricer = tradePricer;
	}
	
	public List<TickEvent<Trade>> clearOrderBooks (){
		List<TickEvent<Trade>> executedTrades =
			new ArrayList<TickEvent<Trade>>();
		
		TickEvent<S> sellOrderEvent = sellOrderBook.getBestOrder();
		TickEvent<B> buyOrderEvent = buyOrderBook.getBestOrder();

		while (
			sellOrderEvent != null && buyOrderEvent != null &&
			aTradeCanBeExecuted(sellOrderEvent, buyOrderEvent)){
			
			S sellOrder = sellOrderEvent.event;
			B buyOrder = buyOrderEvent.event;
			
			Long price = 
				tradePricer.priceTrade(buyOrderEvent, sellOrderEvent);	
			
			TickEvent<Trade> executedTrade = 
				executeTrade(sellOrder, buyOrder, price);

			if (executedTrade != null)
 				executedTrades.add(executedTrade);
			
			sellOrderEvent = sellOrderBook.getBestOrder();
			buyOrderEvent = buyOrderBook.getBestOrder();
			
		}
		
		return executedTrades;
	}
	
	public abstract boolean aTradeCanBeExecuted(TickEvent<S> sellOrderEvent, TickEvent<B> buyOrderEvent);
	
	private TickEvent<Trade> executeTrade (
		S sellOrder, 
		B buyOrder, 
		Long price
		) {
		
		Integer quantity = 
			calculateTradeQuantity(sellOrder, buyOrder);			
		
		Trade trade = 
			new DefaultTrade (stock, quantity, price, sellOrder, buyOrder);
		
		try {
			return trade.execute(world);				
			
		} catch (TradeExecutionException e) {
			Trader culprit = e.getCulprit();
			if (culprit.equals(sellOrder.getTrader())){
				sellOrderBook.cancelOrder(sellOrder);
			}
			else if (culprit.equals(buyOrder.getTrader()))
				buyOrderBook.cancelOrder(buyOrder);
			
			//TODO Penalise the trader that caused the trade to fail.
			e.printStackTrace();
		}
		return null;
	}
	
	private Integer calculateTradeQuantity(
		SellOrder sellOrder, BuyOrder buyOrder) {
		return min(
			sellOrder.getRemainingQuantity(), 
			buyOrder.getRemainingQuantity()
		);
	}

}
