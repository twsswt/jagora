package uk.ac.glasgow.jagora.engine.impl.delay;

import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.StockExchangeViewProvider;

public class DelayViewProvider implements StockExchangeViewProvider {

	private final Long delay;
	private final StockExchangeViewProvider underlying;
	
	public DelayViewProvider(StockExchangeViewProvider underlying, Long delay){
		this.delay = delay;
		this.underlying = underlying;
	}
	
	@Override
	public StockExchangeLevel1View createLevel1View() {
		return new DelayedExchangeLevel1View(underlying.createLevel1View(), delay);
	}

	@Override
	public StockExchangeLevel2View createLevel2View() {
		// TODO Auto-generated method stub
		return null;
	}

}
