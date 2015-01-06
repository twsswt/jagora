package uk.ac.gla.jagora.orderdrivenmarket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.gla.jagora.ExecutedTrade;
import uk.ac.gla.jagora.Market;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.TraderMarketView;
import uk.ac.gla.jagora.World;

public class OrderDrivenMarket implements Market{

	private final World world;
	
	private final Map<Stock,OrderBookPair> orderBookPairs;
	
	public OrderDrivenMarket (World world){
		
		this.world = world;
		orderBookPairs = new HashMap<Stock,OrderBookPair>();
	}
	
	public void registerBuyOrder (BuyOrder order) {
		OrderBookPair orderBookPair = getOrderBookPair(order.stock);
		orderBookPair.recordBuyOrder(order);
	}
	
	public void registerSellOrder (SellOrder order) {
		OrderBookPair orderBookPair = getOrderBookPair(order.stock);
		orderBookPair.recordSellOrder(order);
	}
	
	public void cancelBuyOrder(BuyOrder order){
		OrderBookPair orderBookPair = getOrderBookPair(order.stock);
		orderBookPair.cancelBuyOrder(order);
	}
	
	public void cancelSellOrder(SellOrder order){
		OrderBookPair orderBookPair = getOrderBookPair(order.stock);
		orderBookPair.cancelSellOrder(order);
		
	}
	
	public List<ExecutedTrade> getTradeHistory(Stock stock) {
		OrderBookPair orderBookPair = this.getOrderBookPair(stock);
		if (orderBookPair != null)
			return orderBookPair.getTradeHistory();
		else return null;
	}
	
	public List<SellOrder> getSellOrders(Stock stock) {
		OrderBookPair orderBookPair = this.getOrderBookPair(stock);
		if (orderBookPair != null)
			return orderBookPair.getSellOrders();
		else return null;
	}
	
	public List<BuyOrder> getBuyOrders(Stock stock) {
		OrderBookPair orderBookPair = this.getOrderBookPair(stock);
		if (orderBookPair != null)
			return orderBookPair.getBuyOrders();
		else return null;
	}

	private OrderBookPair getOrderBookPair(Stock stock) {
		OrderBookPair orderBookPair = orderBookPairs.get(stock);
		
		if (orderBookPair == null){
			orderBookPair = new OrderBookPair(stock, world);
			orderBookPairs.put(stock, orderBookPair);
		}
		return orderBookPair;
	}
	
	@Override
	public void doClearing() {
		orderBookPairs.values().stream().forEach(t->t.doClearing());
	}
	
		
	@Override
	public TraderMarketView createTraderMarket() {
		// TODO Auto-generated method stub
		return null;
	}
}
