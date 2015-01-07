package uk.ac.gla.jagora.orderdriven.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.gla.jagora.BuyOrder;
import uk.ac.gla.jagora.ExecutedTrade;
import uk.ac.gla.jagora.SellOrder;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.World;
import uk.ac.gla.jagora.orderdriven.OrderDrivenStockExchange;
import uk.ac.gla.jagora.orderdriven.OrderDrivenStockExchangeTraderView;

public class OrderDrivenStockExchangeImpl implements OrderDrivenStockExchange{

	private final World world;
	
	private final Map<Stock,OrderDrivenMarket> orderDrivenMarkets;
	
	public OrderDrivenStockExchangeImpl (World world){
		
		this.world = world;
		orderDrivenMarkets = new HashMap<Stock,OrderDrivenMarket>();
	}
	
	@Override
	public List<ExecutedTrade> getTradeHistory(Stock stock) {
		OrderDrivenMarket orderDrivenMarket = this.getOrderBookPair(stock);
		if (orderDrivenMarket != null)
			return orderDrivenMarket.getTradeHistory();
		else return null;
	}

	private OrderDrivenMarket getOrderBookPair(Stock stock) {
		OrderDrivenMarket orderDrivenMarket = orderDrivenMarkets.get(stock);
		
		if (orderDrivenMarket == null){
			orderDrivenMarket = new OrderDrivenMarket(stock, world);
			orderDrivenMarkets.put(stock, orderDrivenMarket);
		}
		return orderDrivenMarket;
	}
	
	/**
	 * @see uk.ac.gla.jagora.orderdriven.OrderDrivenStockExchange#doClearing()
	 */
	@Override
	public void doClearing() {
		orderDrivenMarkets.values().stream().forEach(t->t.doClearing());
	}
	
		
	/**
	 * @see uk.ac.gla.jagora.orderdriven.OrderDrivenStockExchange#createTraderMarketView()
	 */
	@Override
	public OrderDrivenStockExchangeTraderView createTraderMarketView() {
		return new TraderOrderDrivenMarketViewImpl ();
	}
	
	class TraderOrderDrivenMarketViewImpl implements OrderDrivenStockExchangeTraderView {
		
		@Override
		public List<SellOrder> getOpenSellOrders(Stock stock) {
			OrderDrivenMarket orderDrivenMarket = getOrderBookPair(stock);
			if (orderDrivenMarket != null)
				return orderDrivenMarket.getSellOrders();
			else return null;
		}
		
		@Override
		public List<BuyOrder> getOpenBuyOrders(Stock stock) {
			OrderDrivenMarket orderDrivenMarket = getOrderBookPair(stock);
			if (orderDrivenMarket != null)
				return orderDrivenMarket.getBuyOrders();
			else return null;
		}

		@Override
		public Double getBestOfferPrice(Stock stock) {			
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
		public Double getBestBidPrice(Stock stock) {
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
			OrderDrivenMarket orderDrivenMarket = getOrderBookPair(buyOrder.stock);
			orderDrivenMarket.recordBuyOrder(buyOrder);		
		}

		@Override
		public void registerSellOrder(SellOrder sellOrder) {
			OrderDrivenMarket orderDrivenMarket = getOrderBookPair(sellOrder.stock);
			orderDrivenMarket.recordSellOrder(sellOrder);			
		}

		@Override
		public void cancelBuyOrder(BuyOrder buyOrder) {
			OrderDrivenMarket orderDrivenMarket = getOrderBookPair(buyOrder.stock);
			orderDrivenMarket.cancelBuyOrder(buyOrder);			
		}

		@Override
		public void cancelSellOrder(SellOrder sellOrder) {
			OrderDrivenMarket orderDrivenMarket = getOrderBookPair(sellOrder.stock);
			orderDrivenMarket.cancelSellOrder(sellOrder);
		}
		
	}
}
