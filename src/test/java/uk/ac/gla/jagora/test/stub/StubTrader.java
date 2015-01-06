package uk.ac.gla.jagora.test.stub;

import java.util.Map;

import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.TraderMarketView;
import uk.ac.gla.jagora.trader.AbstractTrader;

public class StubTrader extends AbstractTrader{

	public StubTrader(String name, Double cash, Map<Stock, Integer> inventory) {
		super(name, cash, inventory);
	}

	@Override
	public void speak(TraderMarketView market) {
		// TODO Auto-generated method stub		
	}
}
