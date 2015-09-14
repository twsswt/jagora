package uk.ac.glasgow.jagora.trader.impl.random;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.AbstractTraderBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomTraderBuilder extends AbstractTraderBuilder {
	
	protected Random random;
	protected Map<Stock, RelativeRangeData> sellRangeData;
	protected Map<Stock, RelativeRangeData> buyRangeData;
	
	public RandomTraderBuilder(){
		sellRangeData = new HashMap<Stock,RelativeRangeData>();
		buyRangeData = new HashMap<Stock,RelativeRangeData>();

	}
	@Override
	public RandomTraderBuilder addStock(Stock stock, Integer quantity){
		super.addStock(stock, quantity);
		return this;
	}

	public RandomTraderBuilder setSellOrderRange(
		Stock stock, Integer minQuantity, Integer maxQuantity,
		Long sellLow, Long sellHigh) {
				
		sellRangeData.put(stock, new RelativeRangeData(stock, minQuantity, maxQuantity, sellLow, sellHigh));
		return this;
	}
	
	public RandomTraderBuilder setBuyOrderRange(
		Stock stock, Integer minQuantity, Integer maxQuantity, Long buyLow, Long buyHigh){
		
		buyRangeData.put(stock, new RelativeRangeData(stock, minQuantity, maxQuantity,  buyLow, buyHigh));
		return this;
	}

	
	@Override
	public RandomTraderBuilder setName(String name) {
		super.setName(name);
		return this;
	}
	@Override
	public RandomTraderBuilder setCash(Long cash){
		super.setCash(cash);
		return this;
	}
	
	public RandomTrader build(){
		return new RandomTrader(
			getName(), getCash(),
			getInventory(),
			random,
			sellRangeData,
			buyRangeData);
	}

	public RandomTraderBuilder setRandom(Random random) {
		this.random = random;
		return this;
	}


}
