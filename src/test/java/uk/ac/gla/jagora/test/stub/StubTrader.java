package uk.ac.gla.jagora.test.stub;

import java.util.Map;

import uk.ac.gla.jagora.Market;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.Trader;

public class StubTrader extends Trader{

	public StubTrader(String name, Double cash, Map<Stock, Integer> inventory) {
		super(name, cash, inventory);
	}

	@Override
	public void speak(Market market) {
		// TODO Auto-generated method stub		
	}
}
