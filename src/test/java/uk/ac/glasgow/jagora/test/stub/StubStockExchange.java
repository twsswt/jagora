package uk.ac.glasgow.jagora.test.stub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeTraderView;
import uk.ac.glasgow.jagora.impl.AbstractBuyOrder;
import uk.ac.glasgow.jagora.impl.AbstractSellOrder;
import uk.ac.glasgow.jagora.ticker.TickerTapeListener;

public class StubStockExchange implements StockExchange {

	private final Map<Stock,List<AbstractBuyOrder>> allBuyOrders;
	private final Map<Stock,List<AbstractSellOrder>> allSellOrders;

	/**
	 * Does nothing.
	 */
	@Override
	public void doClearing() {	}
	
	public StubStockExchange (){
		allBuyOrders = new HashMap<Stock,List<AbstractBuyOrder>> ();
		allSellOrders = new HashMap<Stock,List<AbstractSellOrder>>();
	}
	
	@Override
	public StockExchangeTraderView createTraderStockExchangeView() {

		return new StockExchangeTraderView (){

			@Override
			public Double getBestOfferPrice(Stock stock) {
				return getSellOrders(stock)
					.stream()
					.mapToDouble(sellOrder -> sellOrder.getPrice())
					.min()
					.getAsDouble();
			}

			@Override
			public Double getBestBidPrice(Stock stock) {
				return getBuyOrders(stock)
					.stream()
					.mapToDouble(buyOrder -> buyOrder.getPrice())
					.max()
					.getAsDouble();
			}

			@Override
			public void placeBuyOrder(AbstractBuyOrder BuyOrder) {
				getBuyOrders(BuyOrder.stock).add(BuyOrder);
			}

			@Override
			public void placeSellOrder(AbstractSellOrder SellOrder) {
				getSellOrders(SellOrder.stock).add(SellOrder);
			}

			@Override
			public void cancelBuyOrder(AbstractBuyOrder BuyOrder) {
				getBuyOrders(BuyOrder.stock).remove(BuyOrder);
				
			}

			@Override
			public void cancelSellOrder(AbstractSellOrder SellOrder) {
				getSellOrders(SellOrder.stock).remove(SellOrder);
			}			
		};
		
	}

	public List<AbstractBuyOrder> getBuyOrders(Stock stock) {
		List<AbstractBuyOrder> BuyOrders = allBuyOrders.get(stock);
		if (BuyOrders == null){
			BuyOrders = new ArrayList<AbstractBuyOrder>();
			allBuyOrders.put(stock, BuyOrders);
		}
		return BuyOrders;
	}

	public List<AbstractSellOrder> getSellOrders(Stock stock) {
		List<AbstractSellOrder> SellOrders = allSellOrders.get(stock);
		if (SellOrders == null){
			SellOrders = new ArrayList<AbstractSellOrder>();
			allSellOrders.put(stock, SellOrders);
		}
		return SellOrders;
	}

	@Override
	public void addTicketTapeListener(TickerTapeListener tickerTapeListener, Stock stock) {
		// Does nothing as no trades are executed.
		
	}
}
