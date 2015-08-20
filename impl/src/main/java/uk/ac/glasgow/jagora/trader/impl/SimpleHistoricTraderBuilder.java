package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.util.Random;

import java.util.HashSet;
import java.util.Set;

public class SimpleHistoricTraderBuilder extends AbstractTraderBuilder {

	private Integer seed;

	private Set<StockExchange> stockExchanges;
		
	public SimpleHistoricTraderBuilder(){
		this.inventory = new HashMap<Stock,Integer>();
		this.stockExchanges = new HashSet<StockExchange>();
	}
	@Override
	public SimpleHistoricTraderBuilder addStock(Stock stock, Integer quantity){
		super.addStock(stock, quantity);
		return this;
	}
	@Override
	public SimpleHistoricTraderBuilder setName(String name) {
		super.setName(name);
		return this;
	}
	@Override
	public SimpleHistoricTraderBuilder setCash(Long cash){
		super.setCash(cash);
		return this;
	}
	
	public SimpleHistoricTraderBuilder setSeed(Integer seed){
		this.seed = seed;
		return this;
	}
	
	public SimpleHistoricTraderBuilder monitorStockExchange(StockExchange stockExchange) {
		stockExchanges.add(stockExchange);
		return this;
	}

	public SimpleHistoricTrader build(){
		SimpleHistoricTrader trader =  
			new SimpleHistoricTrader(
				getName(), getCash(), getInventory(), new Random(seed));
		for (StockExchange stockExchange: stockExchanges)
			stockExchange.createLevel1View().registerTradeListener(trader);
		return trader;
	}
	
}
