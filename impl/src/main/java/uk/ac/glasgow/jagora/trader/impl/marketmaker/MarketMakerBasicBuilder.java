package uk.ac.glasgow.jagora.trader.impl.marketmaker;


import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.util.Random;

import java.util.HashMap;
import java.util.Map;

public class MarketMakerBasicBuilder {

	private String name;
	private Long cash = 0l;
	private Integer seed;

	private Map<Stock,Integer> inventory;

	private Double spread;

	private Double liquidityAdjustmentInfluence = 1.0;
	private Double inventoryAdjustmentInfluence = 1.0;
	private Stock stock;
	private Integer quantity;

	public MarketMakerBasicBuilder () {
		inventory = new HashMap<Stock,Integer>();
	}

	public MarketMakerBasicBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public MarketMakerBasicBuilder setCash(Long cash) {
		this.cash = cash;
		return this;
	}

	public MarketMakerBasicBuilder setSeed(Integer seed) {
		this.seed = seed;
		return this;
	}

	public MarketMakerBasicBuilder addStock(Stock stock, Integer quantity) {
		inventory.put(stock, quantity);
		return this;
	}

	public MarketMakerBasicBuilder setTargetStockQuantity(Stock stock, Integer totalQuantity){
		this.stock = stock;
		return this;
	}

	public MarketMakerBasicBuilder setSpread(Double spread) {
		this.spread = spread;
		return this;
	}

	public MarketMakerBasicBuilder setLiquidityAdjustmentInfluence(Double liquidityAdjustmentInfluence) {
		this.liquidityAdjustmentInfluence = liquidityAdjustmentInfluence;
		return this;
	}

	public MarketMakerBasicBuilder setInventoryAdjustmentInfluence(Double inventoryAdjustmnetInfluence) {
		this.inventoryAdjustmentInfluence = inventoryAdjustmnetInfluence;
		return this;
	}

	public MarketMaker build() {
		return new MarketMaker(
				name, cash, inventory, stock, quantity,
				new Random(seed),spread,
				inventoryAdjustmentInfluence, liquidityAdjustmentInfluence);
	}
}
