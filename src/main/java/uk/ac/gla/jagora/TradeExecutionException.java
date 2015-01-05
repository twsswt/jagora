package uk.ac.gla.jagora;

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
