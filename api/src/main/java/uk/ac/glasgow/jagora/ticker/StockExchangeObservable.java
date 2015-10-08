package uk.ac.glasgow.jagora.ticker;

import java.util.List;

import uk.ac.glasgow.jagora.LimitOrder;
import uk.ac.glasgow.jagora.MarketOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.world.TickEvent;

/**
 * A stock exchange observable manages the distribution of
 * stock exchange event notifications on behalf of a stock
 * exchange. The types of event are- orders events and trade
 * executions.
 * 
 * Implementations of StockExchangeObservable provide for
 * variation in the way events are distributed to listeners.
 * Ordering of listeners may be randomised or threaded, for
 * example.
 * 
 * @author tws
 *
 */
public interface StockExchangeObservable {

	public void registerTradeListener(TradeListener tradeListener);

	public void notifyTradeListeners(List<TickEvent<Trade>> orderEvents);

	public void registerOrderListener(OrderListener orderListener);
	
	public void notifyOrderListenersOfLimitOrder(TickEvent<? extends LimitOrder> orderEvent);

	public void notifyOrderListenersOfLimitOrderCancellation(TickEvent<? extends LimitOrder> orderEvent);

	public void notifyOrderListenersOfMarketOrder(TickEvent<? extends MarketOrder> orderEvent);

	public List<TickEvent<Trade>> getTradeHistory(Stock stock);

	public List<LimitOrderEvent> getLimitSellOrderHistory(Stock stock);

	public List<LimitOrderEvent> getLimitBuyOrderHistory(Stock stock);

	public void deRegisterOrderListener(OrderListener orderListener);

	public void deRegisterTradeListener(TradeListener tradeListener);

}