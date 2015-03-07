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
import uk.ac.glasgow.jagora.StockExchangeTraderView;
import uk.ac.glasgow.jagora.ticker.TickerTapeListener;
import uk.ac.glasgow.jagora.ticker.TickerTapeObservable;
import uk.ac.glasgow.jagora.world.World;

public class DefaultStockExchange implements StockExchange{

	private final World world;	
	private MarketFactory marketFactory;
	private final Map<Stock,Market> markets;
	
	private final TickerTapeObservable tickerTapeObservable;
		
	public DefaultStockExchange (World world, TickerTapeObservable tickerTapeObservable, MarketFactory marketFactory){	
		this.world = world;
		this.marketFactory = marketFactory;
		this.tickerTapeObservable = tickerTapeObservable;
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
			tickerTapeObservable.notifyTickerTapeListeners(
				market.doClearing());
	}
	
	public List<BuyOrder> getBuyOrders(Stock stock){
		return getMarket(stock).getBuyOrders();
	}
	
	public List<SellOrder> getSellOrders(Stock stock){
		return getMarket(stock).getSellOrders();
	}
		
	@Override
	public StockExchangeTraderView createTraderStockExchangeView() {
		return new TraderOrderDrivenMarketViewImpl ();
	}
	
	private class TraderOrderDrivenMarketViewImpl implements StockExchangeTraderView {

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
			getMarket(buyOrder.getStock()).recordBuyOrder(buyOrder);		
		}

		@Override
		public void placeSellOrder(SellOrder sellOrder) {
			getMarket(sellOrder.getStock()).recordSellOrder(sellOrder);			
		}

		@Override
		public void cancelBuyOrder(BuyOrder buyOrder) {
			getMarket(buyOrder.getStock()).cancelBuyOrder(buyOrder);			
		}

		@Override
		public void cancelSellOrder(SellOrder sellOrder) {
			getMarket(sellOrder.getStock()).cancelSellOrder(sellOrder);
		}
	
	}

	@Override
	public void addTickerTapeListener(TickerTapeListener tickerTapeListener) {
		tickerTapeObservable.addTicketTapeListener(tickerTapeListener);
	}
}
