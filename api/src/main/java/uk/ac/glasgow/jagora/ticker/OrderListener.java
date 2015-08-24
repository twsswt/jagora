package uk.ac.glasgow.jagora.ticker;

public interface OrderListener {

	public void limitOrderEvent(LimitOrderEvent limitOrderEvent);

	public void marketOrderEntered(MarketOrderEvent marketOrderEvent);
}
