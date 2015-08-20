package uk.ac.glasgow.jagora.impl;

import static java.lang.Math.min;
import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.pricer.TradePricer;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tws
 *
 */
public class ContinuousOrderDrivenMarket implements Market {

	public final Stock stock;
	public final World world;

	private final OrderBook<SellOrder> sellBook;
	private final OrderBook<BuyOrder> buyBook;
	
	private final TradePricer tradePricer;

	private OrderBook<SellOrder> marketSellOrders;
	private OrderBook<BuyOrder> marketBuyOrders;
	
	public ContinuousOrderDrivenMarket (Stock stock, World world, TradePricer tradePricer){
		this.world = world;
		this.tradePricer = tradePricer;

		sellBook = new OrderBook<SellOrder>(world);
		buyBook = new OrderBook<BuyOrder>(world);
		
		marketSellOrders = new OrderBook<SellOrder>(world);
		marketBuyOrders = new OrderBook<BuyOrder>(world);

		this.stock = stock;
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
	public TickEvent<BuyOrder> cancelBuyOrder(BuyOrder order) {
		return buyBook.cancelOrder(order);
	}
	
	@Override
	public TickEvent<SellOrder> cancelSellOrder(SellOrder order) {
		return sellBook.cancelOrder(order);
	}
	
	@Override
	public TickEvent<BuyOrder> recordMarketBuyOrder (MarketBuyOrder order){
		return marketBuyOrders.recordOrder(order);
	}
	
	@Override
	public TickEvent<SellOrder> recordMarketSellOrder (MarketSellOrder order){
		return marketSellOrders.recordOrder(order);
	}


	/**
	 * The operation executes trades,
	 * if the lowest offer is lower than the highest bid.
     * There is a possibility of failed offer if one of the sides
     * cancels its order.
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
				min(
					lowestSell.getRemainingQuantity(), 
					highestBid.getRemainingQuantity()
				);
			
			Long price = tradePricer.priceTrade(highestBuyEvent, lowestSellEvent);	
			
			Trade trade = 
				new DefaultTrade (stock, quantity, price, lowestSell, highestBid);
			
			try {
				TickEvent<Trade> executedTrade = trade.execute(world);
				executedTrades.add(executedTrade);
											
			} catch (TradeExecutionException e) {
				Trader culprit = e.getCulprit();
				if (culprit.equals(lowestSell.getTrader())){
					sellBook.cancelOrder(lowestSell);
				}
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
		return String.format("best bid: %d, best offer: %d", getBestBidPrice(), getBestOfferPrice());
	}

	@Override
	public Long getBestBidPrice() {return buyBook.getBestPrice();}

	@Override
	public Long getBestOfferPrice() {return sellBook.getBestPrice();}

	@Override
	public Long getLastKnownBestBidPrice() {
		return buyBook.getLastKnownBestPrice();
	}

	@Override
	public Long getLastKnownBestOfferPrice() {
		return sellBook.getLastKnownBestPrice();
	}
}
