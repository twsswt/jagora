package uk.ac.glasgow.jagora.trader.ivo.impl;

import java.util.Random;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.AbstractTraderBuilder;

public class HighFrequencyRandomTraderBuilder extends AbstractTraderBuilder {

	private Integer seed;

	private PercentageRangeData buyRangeDatum;
	private PercentageRangeData sellRangeDatum;

	public HighFrequencyRandomTraderBuilder() {}

	@Override
	public HighFrequencyRandomTraderBuilder setName(String name) {
		super.setName(name);
		return this;
	}

	@Override
	public HighFrequencyRandomTraderBuilder setCash(Long cash) {
		super.setCash(cash);
		return this;
	}

	public HighFrequencyRandomTraderBuilder addStock(Stock stock,Integer quantity) {
		super.addStock(stock,quantity);
		return this;
	}

	public HighFrequencyRandomTraderBuilder setSeed(Integer seed) {
		this.seed = seed;
		return this;
	}

	public HighFrequencyRandomTraderBuilder setBuyRangeDatum(
		Stock stock, Integer minQuantity, Integer maxQuantity, Double low, Double high) {

		this.buyRangeDatum = new PercentageRangeData(stock, minQuantity, maxQuantity, low, high);
		return this;
	}

	public HighFrequencyRandomTraderBuilder setSellRangeDatum(
		Stock stock, Integer minQuantity, Integer maxQuantity, Double low, Double high) {
		
		this.sellRangeDatum = new PercentageRangeData(stock, minQuantity, maxQuantity, low, high);
		return this;
	}

	public HighFrequencyRandomTrader build () {
		return  new HighFrequencyRandomTrader(
			getName(), getCash(), getInventory(),
			buyRangeDatum, sellRangeDatum, new Random(seed));
	}
}
