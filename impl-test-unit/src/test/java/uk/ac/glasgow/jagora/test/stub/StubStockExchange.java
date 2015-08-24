package uk.ac.glasgow.jagora.test.stub;

import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.impl.MarketBuyOrder;
import uk.ac.glasgow.jagora.impl.MarketSellOrder;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StubStockExchange implements StockExchange {

	private final Map<Stock,List<LimitBuyOrder>> allBuyOrders;
	private final Map<Stock,List<LimitSellOrder>> allSellOrders;

	/**
	 * Does nothing.
	 */
	@Override
	public void doClearing() {	}
	
	public StubStockExchange (){
		allBuyOrders = new HashMap<Stock,List<LimitBuyOrder>> ();
		allSellOrders = new HashMap<Stock,List<LimitSellOrder>>();
	}
	
	@Override
	public StockExchangeLevel1View createLevel1View() {
		return new StubLevel1View ();
	}

	protected class StubLevel1View implements StockExchangeLevel1View {
		@Override
		public Long getBestOfferPrice(Stock stock) {
			return getSellOrders(stock)
				.stream()
				.mapToLong(sellOrder -> sellOrder.getLimitPrice())
				.min()
				.getAsLong();
		}

		@Override
		public Long getBestBidPrice(Stock stock) {
			return getBuyOrders(stock)
				.stream()
				.mapToLong(buyOrder -> buyOrder.getLimitPrice())
				.max()
				.getAsLong();
		}

		@Override
		public void placeLimitBuyOrder(LimitBuyOrder limitBuyOrder) {
			getBuyOrders(limitBuyOrder.getStock()).add(limitBuyOrder);
		}

		@Override
		public void placeLimitSellOrder(LimitSellOrder LimitSellOrder) {
			getSellOrders(LimitSellOrder.getStock()).add(LimitSellOrder);
		}

		@Override
		public void cancelLimitBuyOrder(LimitBuyOrder LimitBuyOrder) {
			getBuyOrders(LimitBuyOrder.getStock()).remove(LimitBuyOrder);
			
		}

		@Override
		public void cancelLimitSellOrder(LimitSellOrder LimitSellOrder) {
			getSellOrders(LimitSellOrder.getStock()).remove(LimitSellOrder);
		}

		@Override
		public Long getLastKnownBestOfferPrice(Stock stock) {
			return getBestOfferPrice(stock);
		}

		@Override
		public Long getLastKnownBestBidPrice(Stock stock) {
			return getBestBidPrice(stock);
		}

		@Override
		public void registerTradeListener(TradeListener tradeListener) {
			// Does nothing as no trades are executed.
			
		}

		@Override
		public void placeMarketBuyOrder(
			MarketBuyOrder marketBuyOrder) {
			// Does nothing.
			
		}

		@Override
		public void placeMarketSellOrder(
			MarketSellOrder marketSellOrder) {
			// Does nothing.
			
		}
	}


	public List<LimitBuyOrder> getBuyOrders(Stock stock) {
		List<LimitBuyOrder> LimitBuyOrders = allBuyOrders.get(stock);
		if (LimitBuyOrders == null){
			LimitBuyOrders = new ArrayList<LimitBuyOrder>();
			allBuyOrders.put(stock, LimitBuyOrders);
		}
		return LimitBuyOrders;
	}

	public List<LimitSellOrder> getSellOrders(Stock stock) {
		List<LimitSellOrder> LimitSellOrders = allSellOrders.get(stock);
		if (LimitSellOrders == null){
			LimitSellOrders = new ArrayList<LimitSellOrder>();
			allSellOrders.put(stock, LimitSellOrders);
		}
		return LimitSellOrders;
	}


	@Override
	public StockExchangeLevel2View createLevel2View() {
		return new StubLevel2View();
	}
	
	protected class StubLevel2View extends StubLevel1View implements StockExchangeLevel2View {

		@Override
		public void registerOrderListener(
			OrderListener orderListener) {


		}

	}

}
