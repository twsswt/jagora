package uk.ac.gla.jagora;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrderDrivenMarket implements Market{

	private final World world;
	
	private final Map<Stock,OrderBook<BuyOrder>> buyBooks;
	
	private final Map<Stock,OrderBook<SellOrder>> sellBooks;

	private final List<ExecutedTrade> tradeHistory;
	
	public OrderDrivenMarket (World world){
		
		this.world = world;
		
		buyBooks = new HashMap<Stock,OrderBook<BuyOrder>>();
		sellBooks = new HashMap<Stock,OrderBook<SellOrder>>();	
		
		tradeHistory = new ArrayList<ExecutedTrade>();
	}
	
	public void registerBuyOrder (BuyOrder order) {
		OrderBook<BuyOrder> buyBook = buyBooks.get(order.stock);
		
		if (buyBook == null){
			buyBook = new OrderBook<BuyOrder>(world);
			buyBooks.put(order.stock, buyBook);
		}
		buyBook.recordOrder(order);
	}
	
	public void registerSellOrder (SellOrder order) {

		OrderBook<SellOrder> sellBook = sellBooks.get(order.stock);
		if (sellBook == null){
			sellBook = new OrderBook<SellOrder>(world);
			sellBooks.put(order.stock, sellBook);
		}
		sellBook.recordOrder(order);
	}
	
	public OrderBook<SellOrder> getSellBook (Stock stock){
		return sellBooks.get(stock);
		
	}
	
	public OrderBook<BuyOrder> getBuyBook (Stock stock){
		return buyBooks.get(stock);
	}

	@Override
	public void doClearing() {
		
		Set<Stock> tradeableStocks = new HashSet<Stock>();
		tradeableStocks.addAll(buyBooks.keySet());
		tradeableStocks.retainAll(sellBooks.keySet());
		
		tradeableStocks.stream().forEach(OrderDrivenMarket.this::doClearing);
	}
	
	private void doClearing(Stock stock){
		
		OrderBook<SellOrder> sellBook = sellBooks.get(stock);
		OrderBook<BuyOrder> buyBook = buyBooks.get(stock);
		
		if (sellBook == null || buyBook == null) return;
		
		SellOrder lowestSell = sellBook.seeBestOrder();
		BuyOrder highestBuy = buyBook.seeBestOrder();
			
		while (aTradeCanBeExecuted(lowestSell, highestBuy)){
			Integer quantity = 
				Math.min(lowestSell.getRemainingQuantity(), highestBuy.getRemainingQuantity());
			
			Double price = lowestSell.price;		
			
			Trade trade = 
				new Trade (stock, quantity, price, lowestSell, highestBuy);
			
			try {
				ExecutedTrade executedTrade = trade.execute (world);
				tradeHistory.add(executedTrade);
								
				lowestSell = sellBook.seeBestOrder();
				highestBuy = buyBook.seeBestOrder();
				
			} catch (TradeExecutionException e) {
				Trader culprit = e.getCulprit();
				if (culprit.equals(lowestSell.trader))
					sellBook.cancelOrder(lowestSell);
				else if (culprit.equals(highestBuy.trader))
					buyBook.cancelOrder(highestBuy);
				
				//TODO Penalise the trader that caused the trade to fail.
				
				e.printStackTrace();
			}
			
			
		}			
	}
	
	private boolean aTradeCanBeExecuted(
		SellOrder lowestSell, BuyOrder highestBuy) {
		return 
			lowestSell != null &&
			highestBuy != null &&
			highestBuy.price >= lowestSell.price;
	}
	
}
