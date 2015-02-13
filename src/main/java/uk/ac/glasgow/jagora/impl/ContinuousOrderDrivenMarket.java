package uk.ac.glasgow.jagora.impl;

import java.util.ArrayList;
import java.util.List;

import uk.ac.glasgow.jagora.Market;
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

	private final OrderBook<AbstractSellOrder> sellBook;
	private final OrderBook<AbstractBuyOrder> buyBook;
			
	public ContinuousOrderDrivenMarket (Stock stock, World world){
		this.stock = stock;
		this.world = world;

		sellBook = new OrderBook<AbstractSellOrder>(world);
		buyBook = new OrderBook<AbstractBuyOrder>(world);
	}
	
	@Override
	public void recordBuyOrder(AbstractBuyOrder order) {
		buyBook.recordOrder(order);
	}
	
	@Override
	public void recordSellOrder(AbstractSellOrder order) {
		sellBook.recordOrder(order);
	}
	
	@Override
	public void cancelBuyOrder(AbstractBuyOrder order) {
		buyBook.cancelOrder(order);
	}
	
	@Override
	public void cancelSellOrder(AbstractSellOrder order) {
		sellBook.cancelOrder(order);
	}
		
	/**
	 * @see uk.Market.ac.glasgow.jagora.OrderDrivenMarket#doClearing()
	 */
	@Override
	public List<TickEvent<Trade>> doClearing (){
		
		List<TickEvent<Trade>> executedTrades =
			new ArrayList<TickEvent<Trade>>();
		
		AbstractSellOrder lowestSell = sellBook.getBestOrder();
		AbstractBuyOrder highestBuy = buyBook.getBestOrder();

		while (aTradeCanBeExecuted(lowestSell, highestBuy)){
			Integer quantity = 
				Math.min(
					lowestSell.getRemainingQuantity(), 
					highestBuy.getRemainingQuantity()
				);
			
			Double price = lowestSell.getPrice();		
			
			Trade trade = 
				new Trade (stock, quantity, price, lowestSell, highestBuy);
			
			try {
				TickEvent<Trade> executedTrade = trade.execute(world);
				executedTrades.add(executedTrade);
											
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
		return executedTrades;
	}

	private boolean aTradeCanBeExecuted(
		AbstractSellOrder lowestSell, AbstractBuyOrder highestBuy) {
		return 
			lowestSell != null &&
			highestBuy != null &&
			highestBuy.getPrice() >= lowestSell.getPrice();
	}
	
	@Override
	public List<AbstractBuyOrder> getBuyOrders() {
		return buyBook.getOpenOrders();
	}

	@Override
	public List<AbstractSellOrder> getSellOrders() {
		return sellBook.getOpenOrders();
	}

	@Override
	public String toString() {
		return String.format("bids%s:asks%s", buyBook, sellBook);
	}
}
