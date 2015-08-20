package uk.ac.glasgow.jagora.trader.impl.random;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.AbstractTraderBuilder;
import uk.ac.glasgow.jagora.util.Random;

import java.util.HashMap;
import java.util.Map;


public class HighFrequencyRandomTraderBuilder extends AbstractTraderBuilder {
	private String name;
	private Long cash;

	private Map <Stock, Integer> inventory;

	private Integer seed;

	private RangeData buyRangeDatum;
	private RangeData sellRangeDatum;

	public HighFrequencyRandomTraderBuilder() {
		this.inventory = new HashMap<>();
	}

	@Override
	public HighFrequencyRandomTraderBuilder setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public HighFrequencyRandomTraderBuilder setCash(Long cash) {
		this.cash = cash;
		return this;
	}

	public HighFrequencyRandomTraderBuilder addStock(Stock stock,Integer quantity) {
		this.inventory.put(stock,quantity);
		return this;
	}

	public HighFrequencyRandomTraderBuilder setSeed(Integer seed) {
		this.seed = seed;
		return this;
	}

	public HighFrequencyRandomTraderBuilder setBuyRangeDatum(
		Stock stock, Integer minQuantity, Integer maxQuantity, Long low, Long high) {

		this.buyRangeDatum = new RangeData(stock, low, high, minQuantity, maxQuantity);
		return this;
	}

	public HighFrequencyRandomTraderBuilder setSellRangeDatum(
		Stock stock, Integer minQuantity, Integer maxQuantity, Long low, Long high) {
		
		this.sellRangeDatum = new RangeData(stock, low, high, minQuantity, maxQuantity);
		return this;
	}

	public HighFrequencyRandomTrader build () {
		return  new HighFrequencyRandomTrader(
			name, cash, inventory,
			buyRangeDatum, sellRangeDatum, new Random(seed));
	}
}
