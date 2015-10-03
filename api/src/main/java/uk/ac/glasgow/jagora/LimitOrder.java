package uk.ac.glasgow.jagora;

/**
 * @author tws
 *
 */
public interface LimitOrder extends Order, Comparable<LimitOrder>{
	public Long getLimitPrice ();
}
