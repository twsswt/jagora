package uk.ac.glasgow.jagora.ticker;

public interface OrderListener {

	void orderEntered(OrderEvent orderEvent);

	void orderCancelled(OrderEvent orderEvent);


}
