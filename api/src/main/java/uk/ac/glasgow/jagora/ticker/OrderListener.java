package uk.ac.glasgow.jagora.ticker;

public interface OrderListener {

	void orderEntered(OrderEntryEvent orderEntryEvent);

	void orderCancelled(OrderEntryEvent orderEntryEvent);


}
