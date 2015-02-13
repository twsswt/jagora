package uk.ac.glasgow.jagora.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.glasgow.jagora.Market;
import uk.ac.glasgow.jagora.MarketFactory;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeTraderView;
import uk.ac.glasgow.jagora.ticker.TickerTapeListener;
import uk.ac.glasgow.jagora.ticker.TickerTapeObserver;
import uk.ac.glasgow.jagora.world.World;

public class DefaultStockExchange implements StockExchange{

	private final World world;	
	private MarketFactory marketFactory;
	private final Map<Stock,Market> markets;
	
	private final TickerTapeObserver tickerTapeObserver;
		
	public DefaultStockExchange (World world, TickerTapeObserver tickerTapeObserver, MarketFactory marketFactory){	
		this.world = world;
		this.marketFactory = marketFactory;
		this.tickerTapeObserver = tickerTapeObserver;
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
			tickerTapeObserver.notifyTickerTapeListeners(
				market.doClearing());
	}
	
	public List<AbstractBuyOrder> getBuyOrders(Stock stock){
		return getMarket(stock).getBuyOrders();
	}
	
	public List<AbstractSellOrder> getSellOrders(Stock stock){
		return getMarket(stock).getSellOrders();
	}
		
	@Override
	public StockExchangeTraderView createTraderStockExchangeView() {
		return new TraderOrderDrivenMarketViewImpl ();
	}
	
	private class TraderOrderDrivenMarketViewImpl implements StockExchangeTraderView {

		@Override
		public Double getBestOfferPrice(Stock stock) {			
			List<AbstractSellOrder> limitSellOrders =
				getMarket(stock).getSellOrders();
			
			try {
				AbstractSellOrder bestSellOrder = limitSellOrders.get(0);
				return bestSellOrder.getPrice();
			} catch (IndexOutOfBoundsException e){
				return null;
			}
		}

		@Override
		public Double getBestBidPrice(Stock stock) {
			List<AbstractBuyOrder> limitBuyOrders =
				getMarket(stock).getBuyOrders();

			try {
				AbstractBuyOrder bestBuyOrder = limitBuyOrders.get(0);
				return bestBuyOrder.getPrice();
			} catch (IndexOutOfBoundsException e){
				return null;
			}
		}

		@Override
		public void placeBuyOrder(AbstractBuyOrder limitBuyOrder) {
			getMarket(limitBuyOrder.stock).recordBuyOrder(limitBuyOrder);		
		}

		@Override
		public void placeSellOrder(AbstractSellOrder limitSellOrder) {
			getMarket(limitSellOrder.stock).recordSellOrder(limitSellOrder);			
		}

		@Override
		public void cancelBuyOrder(AbstractBuyOrder limitBuyOrder) {
			getMarket(limitBuyOrder.stock).cancelBuyOrder(limitBuyOrder);			
		}

		@Override
		public void cancelSellOrder(AbstractSellOrder limitSellOrder) {
			getMarket(limitSellOrder.stock).cancelSellOrder(limitSellOrder);
		}		
	}

	@Override
	public void addTicketTapeListener(TickerTapeListener tickerTapeListener, Stock stock) {
		tickerTapeObserver.addTicketTapeListener(tickerTapeListener, stock);
	}
}
