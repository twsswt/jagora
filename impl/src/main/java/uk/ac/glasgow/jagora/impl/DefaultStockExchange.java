package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.StockExchangeObservable;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultStockExchange implements StockExchange{

	private final World world;	
	private MarketFactory marketFactory;
	
	private final Map<Stock,Market> markets;
	
	private final StockExchangeObservable stockExchangeObservable;
		
	public DefaultStockExchange (
		World world,
		StockExchangeObservable stockExchangeObservable,
		MarketFactory marketFactory){
		
		this.world = world;
		this.marketFactory = marketFactory;
		this.stockExchangeObservable = stockExchangeObservable;
		this.markets = new HashMap<Stock,Market>();
	}

	private Market getMarket(Stock stock) {
		Market market = markets.get(stock);
		
		if (market == null){
			market = marketFactory.createOrderDrivenMarket(stock, world);
			markets.put(stock, market);
		}
		return market;
	}

	@Override
	public void doClearing() {
		for (Market market: markets.values())
			stockExchangeObservable.notifyTradeListeners(
				market.doClearing());
	}
	
	public List<LimitBuyOrder> getBuyOrders(Stock stock){
		return getMarket(stock).getBuyLimitOrders();
	}
	
	public List<LimitSellOrder> getSellOrders(Stock stock){
		return getMarket(stock).getSellLimitOrders();
	}
		
	@Override
	public StockExchangeLevel1View createLevel1View() {
		return new DefaultLevel1View ();
	}

	private class DefaultLevel1View implements StockExchangeLevel1View {

		@Override
		public Long getBestOfferPrice(Stock stock) {			
			return getMarket(stock).getBestOfferPrice();
		}

		@Override
		public Long getBestBidPrice(Stock stock) {
			return getMarket(stock).getBestBidPrice();
		}
		
		@Override
		public Long getLastKnownBestOfferPrice(Stock stock) {
			return getMarket(stock).getLastKnownBestOfferPrice();
		}

		@Override
		public Long getLastKnownBestBidPrice(Stock stock) {
			return getMarket(stock).getLastKnownBestBidPrice();
		}	

		@Override
		public void placeLimitBuyOrder(LimitBuyOrder limitBuyOrder) {
			TickEvent<LimitBuyOrder> orderEvent =
				getMarket(limitBuyOrder.getStock()).recordLimitBuyOrder(limitBuyOrder);
			stockExchangeObservable.notifyOrderListenersOfLimitOrder(orderEvent);		
		}

		@Override
		public void placeLimitSellOrder(LimitSellOrder limitSellOrder) {
			TickEvent<LimitSellOrder> orderEvent = 
				getMarket(limitSellOrder.getStock()).recordLimitSellOrder(limitSellOrder);	
			stockExchangeObservable.notifyOrderListenersOfLimitOrder(orderEvent);
		}

		@Override
		public void cancelLimitBuyOrder(LimitBuyOrder limitBuyOrder) {
			TickEvent<LimitBuyOrder> orderEvent =
					getMarket(limitBuyOrder.getStock()).cancelLimitBuyOrder(limitBuyOrder);
			stockExchangeObservable.notifyOrderListenersOfLimitOrderCancellation(orderEvent);
		}

		@Override
		public void cancelLimitSellOrder(LimitSellOrder limitSellOrder){
			TickEvent<LimitSellOrder> orderEvent =
				getMarket(limitSellOrder.getStock()).cancelLimitSellOrder(limitSellOrder);
			stockExchangeObservable.notifyOrderListenersOfLimitOrderCancellation(orderEvent);
		}
		
		@Override
		public void placeMarketBuyOrder(MarketBuyOrder marketBuyOrder) {
			TickEvent<MarketBuyOrder> orderEvent =
				getMarket(marketBuyOrder.getStock()).recordMarketBuyOrder(marketBuyOrder);
			stockExchangeObservable.notifyOrderListenersOfMarketOrder(orderEvent);
		}

		@Override
		public void placeMarketSellOrder(MarketSellOrder marketSellOrder) {
			TickEvent<MarketSellOrder> orderEvent =
				getMarket(marketSellOrder.getStock()).recordMarketSellOrder(marketSellOrder);
			stockExchangeObservable.notifyOrderListenersOfMarketOrder(orderEvent);
		}

		
		@Override
		public void registerTradeListener(TradeListener tradeListener) {
			stockExchangeObservable.registerTradeListener(tradeListener);
		}
		
		@Override
		public String toString (){
			return DefaultStockExchange.this.toString();
		}
		
	}

	@Override
	public StockExchangeLevel2View createLevel2View() {
		return new DefaultLevel2View ();
	}
	
	public class DefaultLevel2View extends DefaultLevel1View implements StockExchangeLevel2View {

		@Override
		public void registerOrderListener(OrderListener orderListener) {
			stockExchangeObservable.registerOrderListener(orderListener);
		}

		@Override
		public List<? extends LimitOrder> getBuyLimitOrders(Stock stock) {
			return getMarket(stock).getBuyLimitOrders();
		}

		@Override
		public List<? extends LimitOrder> getSellLimitOrders(Stock stock) {
			return getMarket(stock).getBuyLimitOrders();
		}

	}
	
	@Override
	public String toString (){
		return markets.toString();
	}

}
