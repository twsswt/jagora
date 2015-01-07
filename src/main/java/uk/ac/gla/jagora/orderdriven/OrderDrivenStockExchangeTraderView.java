package uk.ac.gla.jagora.orderdriven;

import java.util.List;

import uk.ac.gla.jagora.BuyOrder;
import uk.ac.gla.jagora.SellOrder;
import uk.ac.gla.jagora.StockExchangeTraderView;
import uk.ac.gla.jagora.Stock;

public interface OrderDrivenStockExchangeTraderView extends StockExchangeTraderView {

	public List<SellOrder> getOpenSellOrders(Stock stock);

	public List<BuyOrder> getOpenBuyOrders(Stock stock);

}
