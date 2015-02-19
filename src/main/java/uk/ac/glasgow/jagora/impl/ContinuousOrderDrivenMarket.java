package uk.ac.glasgow.jagora.impl;

import java.util.ArrayList;
import java.util.List;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.Market;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.TradeExecutionException;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

/**
 * @author tws
 *
 */
public class ContinuousOrderDrivenMarket implements Market {
	
	public final Stock stock;
	public final World world;

	private final OrderBook<SellOrder> sellBook;
	private final OrderBook<BuyOrder> buyBook;
			
	public ContinuousOrderDrivenMarket (Stock stock, World world){
		this.stock = stock;
		this.world = world;

		sellBook = new OrderBook<SellOrder>(world);
		buyBook = new OrderBook<BuyOrder>(world);
	}
	
	@Override
	public void recordBuyOrder(BuyOrder order) {
		buyBook.recordOrder(order);
	}
	
	@Override
	public void recordSellOrder(SellOrder order) {
		sellBook.recordOrder(order);
	}
	
	@Override
	public void cancelBuyOrder(BuyOrder order) {
		buyBook.cancelOrder(order);
	}
	
	@Override
	public void cancelSellOrder(SellOrder order) {
		sellBook.cancelOrder(order);
	}
		
	/**
	 * @see uk.Market.ac.glasgow.jagora.OrderDrivenMarket#doClearing()
	 */
	@Override
	public List<TickEvent<Trade>> doClearing (){
		
		List<TickEvent<Trade>> executedTrades =
			new ArrayList<TickEvent<Trade>>();
		
		SellOrder lowestSell = sellBook.getBestOrder();
		BuyOrder highestBuy = buyBook.getBestOrder();

		while (aTradeCanBeExecuted(lowestSell, highestBuy)){
			Integer quantity = 
				Math.min(
					lowestSell.getRemainingQuantity(), 
					highestBuy.getRemainingQuantity()
				);
			
			Double price = lowestSell.getPrice();		
			
			AbstractTrade trade = 
				new AbstractTrade (stock, quantity, price, lowestSell, highestBuy);
			
			try {
				TickEvent<Trade> executedTrade = trade.execute(world);
				executedTrades.add(executedTrade);
											
			} catch (TradeExecutionException e) {
				Trader culprit = e.getCulprit();
				if (culprit.equals(lowestSell.getTrader()))
					sellBook.cancelOrder(lowestSell);
				else if (culprit.equals(highestBuy.getTrader()))
					buyBook.cancelOrder(highestBuy);
				
				//TODO Penalise the trader that caused the trade to fail.
				
				e.printStackTrace();
			}
			
			lowestSell = sellBook.getBestOrder();
			highestBuy = buyBook.getBestOrder();
			
		}
		return executedTrades;
	}

	private boolean aTradeCanBeExecuted(SellOrder lowestSell, BuyOrder highestBuy) {
		return 
			lowestSell != null &&
			highestBuy != null &&
			highestBuy.getPrice() >= lowestSell.getPrice();
	}
	
	@Override
	public List<BuyOrder> getBuyOrders() {
		return buyBook.getOpenOrders();
	}

	@Override
	public List<SellOrder> getSellOrders() {
		return sellBook.getOpenOrders();
	}

	@Override
	public String toString() {
		return String.format("bids%s:asks%s", buyBook, sellBook);
	}
}
