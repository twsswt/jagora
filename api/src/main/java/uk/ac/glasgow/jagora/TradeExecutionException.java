package uk.ac.glasgow.jagora;

import uk.ac.glasgow.jagora.trader.Trader;

public class TradeExecutionException extends Exception {

	/****/
	private static final long serialVersionUID = -5811973945246674438L;
	
	private Trade trade;
	private Trader culprit;

	public TradeExecutionException(String message, Trade trade, Trader culprit) {
		super(message);
		this.trade = trade;
		this.culprit = culprit;
	}

	public Trader getCulprit() {
		return culprit;
	}

	public Trade getTrade() {
		return trade;
	}

}
