package uk.ac.glasgow.jagora.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.Market;
import uk.ac.glasgow.jagora.MarketFactory;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.ticker.StockExchangeObservable;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

public class DefaultStockExchange implements StockExchange{

	private final World world;	
	private MarketFactory marketFactory;
	private final Map<Stock,Market> markets;
	
	private final StockExchangeObservable stockExchangeObservable;
		
	public DefaultStockExchange (World world, StockExchangeObservable stockExchangeObservable, MarketFactory marketFactory){	
		this.world = world;
		this.marketFactory = marketFactory;
		this.stockExchangeObservable = stockExchangeObservable;
		markets = new HashMap<Stock,Market>();
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
	
	public List<BuyOrder> getBuyOrders(Stock stock){
		return getMarket(stock).getBuyOrders();
	}
	
	public List<SellOrder> getSellOrders(Stock stock){
		return getMarket(stock).getSellOrders();
	}
		
	@Override
	public StockExchangeLevel1View createLevel1View() {
		return new DefaultLevel1View ();
	}
	
	private class DefaultLevel1View implements StockExchangeLevel1View {

		@Override
		public Double getBestOfferPrice(Stock stock) {			
			return getMarket(stock).getBestOfferPrice();
		}

		@Override
		public Double getBestBidPrice(Stock stock) {
			return getMarket(stock).getBestBidPrice();
		}
		
		@Override
		public Double getLastKnownBestOfferPrice(Stock stock) {
			return getMarket(stock).getLastKnownBestOfferPrice();
		}

		@Override
		public Double getLastKnownBestBidPrice(Stock stock) {
			return getMarket(stock).getLastKnownBestBidPrice();
		}	

		@Override
		public void placeBuyOrder(BuyOrder buyOrder) {
			TickEvent<BuyOrder> orderEvent =
				getMarket(buyOrder.getStock()).recordBuyOrder(buyOrder);
			stockExchangeObservable.notifyOrderListeners(orderEvent);		
		}

		@Override
		public void placeSellOrder(SellOrder sellOrder) {
			TickEvent<SellOrder> orderEvent = 
				getMarket(sellOrder.getStock()).recordSellOrder(sellOrder);	
			stockExchangeObservable.notifyOrderListeners(orderEvent);
		}

		@Override
		public void cancelBuyOrder(BuyOrder buyOrder) {
			getMarket(buyOrder.getStock()).cancelBuyOrder(buyOrder);			
		}

		@Override
		public void cancelSellOrder(SellOrder sellOrder) {
			getMarket(sellOrder.getStock()).cancelSellOrder(sellOrder);
		}
		
		@Override
		public void registerTradeListener(TradeListener tradeListener) {
			stockExchangeObservable.registerTradeListener(tradeListener);
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

	}

}
