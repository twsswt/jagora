package uk.ac.glasgow.jagora.test.stub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeTraderView;
import uk.ac.glasgow.jagora.ticker.TickerTapeListener;

public class StubStockExchange implements StockExchange {

	private final Map<Stock,List<BuyOrder>> allBuyOrders;
	private final Map<Stock,List<SellOrder>> allSellOrders;

	/**
	 * Does nothing.
	 */
	@Override
	public void doClearing() {	}
	
	public StubStockExchange (){
		allBuyOrders = new HashMap<Stock,List<BuyOrder>> ();
		allSellOrders = new HashMap<Stock,List<SellOrder>>();
	}
	
	@Override
	public StockExchangeTraderView createTraderStockExchangeView() {

		return new StockExchangeTraderView (){

			@Override
			public Double getBestOfferPrice(Stock stock) {
				return getSellOrders(stock)
					.stream()
					.mapToDouble(sellOrder -> sellOrder.price)
					.min()
					.getAsDouble();
			}

			@Override
			public Double getBestBidPrice(Stock stock) {
				return getBuyOrders(stock)
					.stream()
					.mapToDouble(buyOrder -> buyOrder.price)
					.max()
					.getAsDouble();
			}

			@Override
			public void placeBuyOrder(BuyOrder buyOrder) {
				getBuyOrders(buyOrder.stock).add(buyOrder);
			}

			@Override
			public void placeSellOrder(SellOrder sellOrder) {
				getSellOrders(sellOrder.stock).add(sellOrder);
			}

			@Override
			public void cancelBuyOrder(BuyOrder buyOrder) {
				getBuyOrders(buyOrder.stock).remove(buyOrder);
				
			}

			@Override
			public void cancelSellOrder(SellOrder sellOrder) {
				getSellOrders(sellOrder.stock).remove(sellOrder);
			}			
		};
		
	}

	public List<BuyOrder> getBuyOrders(Stock stock) {
		List<BuyOrder> buyOrders = allBuyOrders.get(stock);
		if (buyOrders == null){
			buyOrders = new ArrayList<BuyOrder>();
			allBuyOrders.put(stock, buyOrders);
		}
		return buyOrders;
	}

	public List<SellOrder> getSellOrders(Stock stock) {
		List<SellOrder> sellOrders = allSellOrders.get(stock);
		if (sellOrders == null){
			sellOrders = new ArrayList<SellOrder>();
			allSellOrders.put(stock, sellOrders);
		}
		return sellOrders;
	}

	@Override
	public void addTicketTapeListener(TickerTapeListener tickerTapeListener, Stock stock) {
		// Does nothing as no trades are executed.
		
	}
}
