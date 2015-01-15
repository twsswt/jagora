package uk.ac.glasgow.jagora.test.stub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.glasgow.jagora.BuyOrder;
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
			public void placeBuyOrder(BuyOrder BuyOrder) {
				getBuyOrders(BuyOrder.stock).add(BuyOrder);
			}

			@Override
			public void placeSellOrder(SellOrder SellOrder) {
				getSellOrders(SellOrder.stock).add(SellOrder);
			}

			@Override
			public void cancelBuyOrder(BuyOrder BuyOrder) {
				getBuyOrders(BuyOrder.stock).remove(BuyOrder);
				
			}

			@Override
			public void cancelSellOrder(SellOrder SellOrder) {
				getSellOrders(SellOrder.stock).remove(SellOrder);
			}			
		};
		
	}

	public List<BuyOrder> getBuyOrders(Stock stock) {
		List<BuyOrder> BuyOrders = allBuyOrders.get(stock);
		if (BuyOrders == null){
			BuyOrders = new ArrayList<BuyOrder>();
			allBuyOrders.put(stock, BuyOrders);
		}
		return BuyOrders;
	}

	public List<SellOrder> getSellOrders(Stock stock) {
		List<SellOrder> SellOrders = allSellOrders.get(stock);
		if (SellOrders == null){
			SellOrders = new ArrayList<SellOrder>();
			allSellOrders.put(stock, SellOrders);
		}
		return SellOrders;
	}

	@Override
	public void addTicketTapeListener(TickerTapeListener tickerTapeListener, Stock stock) {
		// Does nothing as no trades are executed.
		
	}
}
