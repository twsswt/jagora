package uk.ac.glasgow.jagora;

public interface LimitOrder extends Order, Comparable<LimitOrder>{
	public Long getLimitPrice ();
}
