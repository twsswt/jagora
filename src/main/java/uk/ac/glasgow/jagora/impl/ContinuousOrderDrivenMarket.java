package uk.ac.glasgow.jagora.impl;

import java.util.ArrayList;
import java.util.List;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.Market;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.TradeExecutionException;
import uk.ac.glasgow.jagora.pricer.Pricer;
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
	
	private final Pricer pricer;
			
	public ContinuousOrderDrivenMarket (Stock stock, World world, Pricer pricer){
		this.stock = stock;
		this.world = world;
		this.pricer = pricer;

		sellBook = new OrderBook<SellOrder>(world);
		buyBook = new OrderBook<BuyOrder>(world);
	}
	
	@Override
	public TickEvent<BuyOrder> recordBuyOrder(BuyOrder order) {
		return buyBook.recordOrder(order);
	}
	
	@Override
	public TickEvent<SellOrder> recordSellOrder(SellOrder order) {
		return sellBook.recordOrder(order);
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
		
		TickEvent<SellOrder> lowestSellEvent = sellBook.getBestOrder();
		TickEvent<BuyOrder> highestBuyEvent = buyBook.getBestOrder();

		while (aTradeCanBeExecuted(lowestSellEvent, highestBuyEvent)){
			SellOrder lowestSell = lowestSellEvent.event;
			BuyOrder highestBid = highestBuyEvent.event;
			
			Integer quantity = 
				Math.min(
					lowestSell.getRemainingQuantity(), 
					highestBid.getRemainingQuantity()
				);
			
			Double price = pricer.priceTrade(highestBuyEvent, lowestSellEvent);	
			
			Trade trade = 
				new DefaultTrade (stock, quantity, price, lowestSell, highestBid);
			
			try {
				TickEvent<Trade> executedTrade = trade.execute(world);
				executedTrades.add(executedTrade);
											
			} catch (TradeExecutionException e) {
				Trader culprit = e.getCulprit();
								
				if (culprit.equals(lowestSell.getTrader()))
					sellBook.cancelOrder(lowestSell);
				else if (culprit.equals(highestBid.getTrader()))
					buyBook.cancelOrder(highestBid);
				
				//TODO Penalise the trader that caused the trade to fail.
				e.printStackTrace();
			}
			
			lowestSellEvent = sellBook.getBestOrder();
			highestBuyEvent = buyBook.getBestOrder();
			
		}
		return executedTrades;
	}

	private boolean aTradeCanBeExecuted(TickEvent<SellOrder> lowestSell, TickEvent<BuyOrder> highestBuy) {
		return 
			lowestSell != null &&
			highestBuy != null &&
			highestBuy.event.getPrice() >= lowestSell.event.getPrice();
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

	@Override
	public Double getBestBidPrice() {
		return buyBook.getBestPrice();
	}

	@Override
	public Double getBestOfferPrice() {
		return sellBook.getBestPrice();
	}

	@Override
	public Double getLastKnownBestBidPrice() {
		return buyBook.getLastKnownBestPrice();
	}

	@Override
	public Double getLastKnownBestOfferPrice() {
		return sellBook.getLastKnownBestPrice();
	}
}
