package uk.ac.gla.jagora.orderdrivenmarket;

import java.util.List;

import uk.ac.gla.jagora.ExecutedTrade;
import uk.ac.gla.jagora.Market;
import uk.ac.gla.jagora.Stock;

public interface OrderDrivenMarket extends Market {

	@Override
	public abstract TraderOrderDrivenMarketView createTraderMarketView();

	public abstract List<BuyOrder> getBuyOrders(Stock stock);

	public abstract List<SellOrder> getSellOrders(Stock stock);

	public abstract List<ExecutedTrade> getTradeHistory(Stock stock);

}