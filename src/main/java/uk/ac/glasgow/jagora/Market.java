package uk.ac.glasgow.jagora;

import java.util.List;

import uk.ac.glasgow.jagora.impl.AbstractBuyOrder;
import uk.ac.glasgow.jagora.impl.AbstractSellOrder;
import uk.ac.glasgow.jagora.world.TickEvent;

/**
 * Defines features of a market for a single stock.
 * @author tws
 *
 */
public interface Market {

	public abstract void recordBuyOrder(AbstractBuyOrder order);

	public abstract void recordSellOrder(AbstractSellOrder order);

	public abstract void cancelBuyOrder(AbstractBuyOrder order);

	public abstract void cancelSellOrder(AbstractSellOrder order);

	public abstract List<TickEvent<Trade>> doClearing();

	public abstract List<AbstractBuyOrder> getBuyOrders();

	public abstract List<AbstractSellOrder> getSellOrders();
}