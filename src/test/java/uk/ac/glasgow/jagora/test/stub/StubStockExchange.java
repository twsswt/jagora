package uk.ac.glasgow.jagora.test.stub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeListener;

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
	public StockExchangeLevel1View createLevel1View() {
		return new StubLevel1View ();
	}
		
		
		
	protected class StubLevel1View implements StockExchangeLevel1View {
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
		public void placeBuyOrder(BuyOrder buyOrder) {
			getBuyOrders(buyOrder.getStock()).add(buyOrder);
		}

		@Override
		public void placeSellOrder(SellOrder SellOrder) {
			getSellOrders(SellOrder.getStock()).add(SellOrder);
		}

		@Override
		public void cancelBuyOrder(BuyOrder BuyOrder) {
			getBuyOrders(BuyOrder.getStock()).remove(BuyOrder);
			
		}

		@Override
		public void cancelSellOrder(SellOrder SellOrder) {
			getSellOrders(SellOrder.getStock()).remove(SellOrder);
		}

		@Override
		public Double getLastKnownBestOfferPrice(Stock stock) {
			return getBestOfferPrice(stock);
		}

		@Override
		public Double getLastKnownBestBidPrice(Stock stock) {
			return getBestBidPrice(stock);
		}

		@Override
		public void registerTradeListener(TradeListener tradeListener) {
			// Does nothing as no trades are executed.
			
		}
			
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
	public StockExchangeLevel2View createLevel2View() {
		return new StubLevel2View();
	}
	
	protected class StubLevel2View extends StubLevel1View implements StockExchangeLevel2View {

		@Override
		public void registerOrderListener(
			OrderListener orderListener) {
			// TODO Auto-generated method stub

		}

	}

}
