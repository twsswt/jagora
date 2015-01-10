package uk.ac.glasgow.jagora.orderdriven;

import java.util.List;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeTraderView;

public interface ContinuousOrderDrivenStockExchangeTraderView extends StockExchangeTraderView {

	public List<SellOrder> getOpenSellOrders(Stock stock);

	public List<BuyOrder> getOpenBuyOrders(Stock stock);

}
