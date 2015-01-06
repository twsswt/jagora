package uk.ac.gla.jagora.orderdrivenmarket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.gla.jagora.ExecutedTrade;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.World;

public class OrderDrivenMarketImpl implements OrderDrivenMarket{

	private final World world;
	
	private final Map<Stock,OrderBookPair> orderBookPairs;
	
	public OrderDrivenMarketImpl (World world){
		
		this.world = world;
		orderBookPairs = new HashMap<Stock,OrderBookPair>();
	}
	
	@Override
	public List<ExecutedTrade> getTradeHistory(Stock stock) {
		OrderBookPair orderBookPair = this.getOrderBookPair(stock);
		if (orderBookPair != null)
			return orderBookPair.getTradeHistory();
		else return null;
	}
	
	@Override
	public List<SellOrder> getSellOrders(Stock stock) {
		OrderBookPair orderBookPair = this.getOrderBookPair(stock);
		if (orderBookPair != null)
			return orderBookPair.getSellOrders();
		else return null;
	}
	
	@Override
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
	
	/**
	 * @see uk.ac.gla.jagora.orderdrivenmarket.OrderDrivenMarket#doClearing()
	 */
	@Override
	public void doClearing() {
		orderBookPairs.values().stream().forEach(t->t.doClearing());
	}
	
		
	/**
	 * @see uk.ac.gla.jagora.orderdrivenmarket.OrderDrivenMarket#createTraderMarketView()
	 */
	@Override
	public TraderOrderDrivenMarketView createTraderMarketView() {
		return new TraderOrderDrivenMarketViewImpl ();
	}
	
	class TraderOrderDrivenMarketViewImpl implements TraderOrderDrivenMarketView {

		@Override
		public Double getCurrentBestSellPrice(Stock stock) {			
			List<SellOrder> sellOrders =
				getOrderBookPair(stock).getSellOrders();
			
			try {
				SellOrder bestSellOrder = sellOrders.get(0);
				return bestSellOrder.price;
			} catch (IndexOutOfBoundsException e){
				return null;
			}
		}

		@Override
		public Double getCurrentBestBuyPrice(Stock stock) {
			List<BuyOrder> buyOrders =
				getOrderBookPair(stock).getBuyOrders();

			try {
				BuyOrder bestBuyOrder = buyOrders.get(0);
				return bestBuyOrder.price;
			} catch (IndexOutOfBoundsException e){
				return null;
			}
		}

		@Override
		public void registerBuyOrder(BuyOrder buyOrder) {
			OrderBookPair orderBookPair = getOrderBookPair(buyOrder.stock);
			orderBookPair.recordBuyOrder(buyOrder);		
		}

		@Override
		public void registerSellOrder(SellOrder sellOrder) {
			OrderBookPair orderBookPair = getOrderBookPair(sellOrder.stock);
			orderBookPair.recordSellOrder(sellOrder);			
		}

		@Override
		public void cancelBuyOrder(BuyOrder buyOrder) {
			OrderBookPair orderBookPair = getOrderBookPair(buyOrder.stock);
			orderBookPair.cancelBuyOrder(buyOrder);			
		}

		@Override
		public void cancelSellOrder(SellOrder sellOrder) {
			OrderBookPair orderBookPair = getOrderBookPair(sellOrder.stock);
			orderBookPair.cancelSellOrder(sellOrder);
		}
		
	}
}
