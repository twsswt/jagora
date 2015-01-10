package uk.ac.glasgow.jagora.orderdriven.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.ExecutedTrade;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.TickerTapeListener;
import uk.ac.glasgow.jagora.World;
import uk.ac.glasgow.jagora.orderdriven.ContinuousOrderDrivenStockExchange;
import uk.ac.glasgow.jagora.orderdriven.ContinuousOrderDrivenStockExchangeTraderView;

public class OrderDrivenStockExchangeImpl implements ContinuousOrderDrivenStockExchange{

	private final World world;
	
	private final Map<Stock,OrderDrivenMarket> orderDrivenMarkets;
	
	public OrderDrivenStockExchangeImpl (World world){	
		this.world = world;
		orderDrivenMarkets = new HashMap<Stock,OrderDrivenMarket>();
	}
	
	@Override
	public List<ExecutedTrade> getTradeHistory(Stock stock) {
		OrderDrivenMarket orderDrivenMarket = this.getOrderDrivenMarket(stock);
		if (orderDrivenMarket != null)
			return orderDrivenMarket.getTradeHistory();
		else return null;
	}

	private OrderDrivenMarket getOrderDrivenMarket(Stock stock) {
		OrderDrivenMarket orderDrivenMarket = orderDrivenMarkets.get(stock);
		
		if (orderDrivenMarket == null){
			orderDrivenMarket = new OrderDrivenMarket(stock, world);
			orderDrivenMarkets.put(stock, orderDrivenMarket);
		}
		return orderDrivenMarket;
	}
	
	/**
	 * @see uk.ac.glasgow.jagora.orderdriven.ContinuousOrderDrivenStockExchange#doClearing()
	 */
	@Override
	public void doClearing() {
		orderDrivenMarkets.values().stream()
			.forEach(t->t.doClearing());
	}
	
	/**
	 * @see uk.ac.glasgow.jagora.orderdriven.ContinuousOrderDrivenStockExchange#createTraderStockExchangeView()
	 */
	@Override
	public ContinuousOrderDrivenStockExchangeTraderView createTraderStockExchangeView() {
		return new TraderOrderDrivenMarketViewImpl ();
	}
	
	class TraderOrderDrivenMarketViewImpl implements ContinuousOrderDrivenStockExchangeTraderView {
		
		@Override
		public List<SellOrder> getOpenSellOrders(Stock stock) {
			OrderDrivenMarket orderDrivenMarket = getOrderDrivenMarket(stock);
			if (orderDrivenMarket != null)
				return orderDrivenMarket.getSellOrders();
			else return null;
		}
		
		@Override
		public List<BuyOrder> getOpenBuyOrders(Stock stock) {
			OrderDrivenMarket orderDrivenMarket = getOrderDrivenMarket(stock);
			if (orderDrivenMarket != null)
				return orderDrivenMarket.getBuyOrders();
			else return null;
		}

		@Override
		public Double getBestOfferPrice(Stock stock) {			
			List<SellOrder> sellOrders =
				getOrderDrivenMarket(stock).getSellOrders();
			
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
				getOrderDrivenMarket(stock).getBuyOrders();

			try {
				BuyOrder bestBuyOrder = buyOrders.get(0);
				return bestBuyOrder.price;
			} catch (IndexOutOfBoundsException e){
				return null;
			}
		}

		@Override
		public void placeBuyOrder(BuyOrder buyOrder) {
			OrderDrivenMarket orderDrivenMarket = getOrderDrivenMarket(buyOrder.stock);
			orderDrivenMarket.recordBuyOrder(buyOrder);		
		}

		@Override
		public void placeSellOrder(SellOrder sellOrder) {
			OrderDrivenMarket orderDrivenMarket = getOrderDrivenMarket(sellOrder.stock);
			orderDrivenMarket.recordSellOrder(sellOrder);			
		}

		@Override
		public void cancelBuyOrder(BuyOrder buyOrder) {
			OrderDrivenMarket orderDrivenMarket = getOrderDrivenMarket(buyOrder.stock);
			orderDrivenMarket.cancelBuyOrder(buyOrder);			
		}

		@Override
		public void cancelSellOrder(SellOrder sellOrder) {
			OrderDrivenMarket orderDrivenMarket = getOrderDrivenMarket(sellOrder.stock);
			orderDrivenMarket.cancelSellOrder(sellOrder);
		}

		@Override
		public void addTicketTapeListener(
				TickerTapeListener tickerTapeListener, Stock stock) {
			OrderDrivenMarket orderDrivenMarket = 
				getOrderDrivenMarket(stock);
			orderDrivenMarket.addTickerTapeListener(tickerTapeListener);
		}
		
	}
}
