package uk.ac.glasgow.jagora.trader.impl.marketmaker;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;

/**
 * Used to hold information about the current position taken with a particular stock
 */
public class StockPositionDatum {

	public final Stock stock;
	public final Integer targetQuantity;

	protected BuyOrder currentBuyOrder = null;
	protected SellOrder currentSellOrder = null;

	protected Long newBuyPrice = 0l;
	protected Long newSellPrice = 0l;

	protected Double inventoryAdjustment = 0.0;

	protected Long spread = 0l;

	StockPositionDatum (Stock stock, Integer targetQuantity) {
		this.stock = stock;
		this.targetQuantity = targetQuantity;
	}

	public void setNewBuyPrice(Long price){
		this.newBuyPrice = price;
	}

	public void setNewSellPrice(Long price){
		 this.newSellPrice = price;
	}

	public void setInventoryAdjustment(Double inventoryAdjustment) {
		this.inventoryAdjustment = inventoryAdjustment;
	}
}
