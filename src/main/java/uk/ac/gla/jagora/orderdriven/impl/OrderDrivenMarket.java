package uk.ac.gla.jagora.orderdriven.impl;

import java.util.ArrayList;
import java.util.List;

import uk.ac.gla.jagora.BuyOrder;
import uk.ac.gla.jagora.ExecutedTrade;
import uk.ac.gla.jagora.SellOrder;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.Trade;
import uk.ac.gla.jagora.TradeExecutionException;
import uk.ac.gla.jagora.Trader;
import uk.ac.gla.jagora.World;

/**
 * Manages orders and trades for a single stock type on an order driven stock exchange.
 * @author tws
 *
 */
public class OrderDrivenMarket {
	
	public final Stock stock;
	public final World world;

	private final OrderBook<SellOrder> sellBook;
	private final OrderBook<BuyOrder> buyBook;
	
	private final List<ExecutedTrade> tradeHistory;
	
	public OrderDrivenMarket (Stock stock, World world){
		this.stock = stock;
		this.world = world;

		sellBook = new OrderBook<SellOrder>(world);
		buyBook = new OrderBook<BuyOrder>(world);
		
		tradeHistory = new ArrayList<ExecutedTrade>();

	}
	
	public void recordBuyOrder(BuyOrder order) {
		buyBook.recordOrder(order);
	}
	
	public void recordSellOrder(SellOrder order) {
		sellBook.recordOrder(order);
	}
	
	public void cancelBuyOrder(BuyOrder order) {
		buyBook.cancelOrder(order);
	}
	
	public void cancelSellOrder(SellOrder order) {
		sellBook.cancelOrder(order);
	}
	
	
	public List<ExecutedTrade> getTradeHistory() {
		return new ArrayList<ExecutedTrade>(tradeHistory);
	}
	
	public void doClearing (){
		
		SellOrder lowestSell = sellBook.getBestOrder();
		BuyOrder highestBuy = buyBook.getBestOrder();
			
		while (aTradeCanBeExecuted(lowestSell, highestBuy)){
			Integer quantity = 
				Math.min(lowestSell.getRemainingQuantity(), highestBuy.getRemainingQuantity());
			
			Double price = lowestSell.price;		
			
			Trade trade = 
				new OrderTrade (stock, quantity, price, lowestSell, highestBuy);
			
			try {
				ExecutedTrade executedTrade = trade.execute (world);
				tradeHistory.add(executedTrade);
											
			} catch (TradeExecutionException e) {
				Trader culprit = e.getCulprit();
				if (culprit.equals(lowestSell.trader))
					sellBook.cancelOrder(lowestSell);
				else if (culprit.equals(highestBuy.trader))
					buyBook.cancelOrder(highestBuy);
				
				//TODO Penalise the trader that caused the trade to fail.
				
				e.printStackTrace();
			}
			
			lowestSell = sellBook.getBestOrder();
			highestBuy = buyBook.getBestOrder();
			
		}			
	}
	
	private boolean aTradeCanBeExecuted(
		SellOrder lowestSell, BuyOrder highestBuy) {
		return 
			lowestSell != null &&
			highestBuy != null &&
			highestBuy.price >= lowestSell.price;
	}

	public List<BuyOrder> getBuyOrders() {
		return buyBook.getOpenOrders();
	}

	public List<SellOrder> getSellOrders() {
		return sellBook.getOpenOrders();
	}

	@Override
	public String toString() {
		return String.format("bids%s:asks%s", buyBook, sellBook);
	}
	
}
