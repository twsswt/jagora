package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.PriceListener;
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
		
	public DefaultStockExchange (World world, StockExchangeObservable stockExchangeObservable,
								 MarketFactory marketFactory){
		this.world = world;
		this.marketFactory = marketFactory;
		this.stockExchangeObservable = stockExchangeObservable;
		markets = new HashMap<Stock,Market>();
	}

    /**
     * Find the market of a stock,
     * if it doesn't yet exist it creates one
     * and saves it to the Exchange
     * @param stock
     * @return
     */
	private Market getMarket(Stock stock) {
		Market market = markets.get(stock);
		
		if (market == null){
			//implemented this way to keep old experiments working - market should not fail
			market = marketFactory.createOrderDrivenMarket(
					new StockWarehouse(stock,1000), world);
			markets.put(stock, market);
		}
		return market;
	}

	@Override
	public void createMarket(StockWarehouse stockWarehouse) {
		Stock stock = stockWarehouse.getStock();
		Market market = markets.get(stockWarehouse.getStock());
		//just a check if there already is an exiting market
		if (market == null){
			market = marketFactory.createOrderDrivenMarket(
					stockWarehouse, world);
			markets.put(stock, market);
		}

	}

	@Override
	public StockWarehouse getStockWarehouse(Stock stock) {
		Market market = getMarket(stock);
		return market.getStockWarehouse();
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
	//can't this be static?
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

        //Important if we want to implement the hypothetical crash
		@Override
		public void cancelBuyOrder(BuyOrder buyOrder) {
			TickEvent<BuyOrder> orderEvent =
					getMarket(buyOrder.getStock()).cancelBuyOrder(buyOrder);
			stockExchangeObservable.notifyOrderListenersOfCancellation(orderEvent);
			buyOrder.getTrader().notifyOfCancellation(buyOrder);
		}

		@Override
		public void cancelSellOrder(SellOrder sellOrder){
			TickEvent<SellOrder> orderEvent =
				getMarket(sellOrder.getStock()).cancelSellOrder(sellOrder);
			stockExchangeObservable.notifyOrderListenersOfCancellation(orderEvent);
			sellOrder.getTrader().notifyOfCancellation(sellOrder);
		}
		
		@Override
		public void registerTradeListener(TradeListener tradeListener) {
			stockExchangeObservable.registerTradeListener(tradeListener);
		}


		@Override
		public void registerPriceListener (PriceListener tradePriceListener){
			stockExchangeObservable.registerPriceListener(tradePriceListener);
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

	}
	
	@Override
	public String toString (){
		return markets.toString();
	}

}
