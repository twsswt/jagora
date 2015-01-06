package uk.ac.gla.jagora.test.stub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.gla.jagora.ExecutedTrade;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.orderdrivenmarket.BuyOrder;
import uk.ac.gla.jagora.orderdrivenmarket.OrderDrivenMarket;
import uk.ac.gla.jagora.orderdrivenmarket.SellOrder;
import uk.ac.gla.jagora.orderdrivenmarket.TraderOrderDrivenMarketView;

public class StubOrderDrivenMarket implements OrderDrivenMarket {

	private final Map<Stock,List<BuyOrder>> allBuyOrders;
	private final Map<Stock,List<SellOrder>> allSellOrders;

	/**
	 * Does nothing.
	 */
	@Override
	public void doClearing() {	}
	
	public StubOrderDrivenMarket (){
		allBuyOrders = new HashMap<Stock,List<BuyOrder>> ();
		allSellOrders = new HashMap<Stock,List<SellOrder>>();
	}
	
	@Override
	public TraderOrderDrivenMarketView createTraderMarketView() {

		return new TraderOrderDrivenMarketView (){

			@Override
			public Double getCurrentBestSellPrice(Stock stock) {
				return getSellOrders(stock)
					.stream()
					.mapToDouble(sellOrder -> sellOrder.price)
					.min()
					.getAsDouble();
			}

			@Override
			public Double getCurrentBestBuyPrice(Stock stock) {
				return getBuyOrders(stock)
					.stream()
					.mapToDouble(buyOrder -> buyOrder.price)
					.max()
					.getAsDouble();
			}

			@Override
			public void registerBuyOrder(BuyOrder buyOrder) {
				getBuyOrders(buyOrder.stock).add(buyOrder);
			}

			@Override
			public void registerSellOrder(SellOrder sellOrder) {
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

	public List<ExecutedTrade> getTradeHistory(Stock stock) {
		// Does nothing as no trades are ever executed.
		return null;
	}
}
