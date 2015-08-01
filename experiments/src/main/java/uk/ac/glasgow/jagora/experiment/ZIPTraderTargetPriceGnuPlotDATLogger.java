package uk.ac.glasgow.jagora.experiment;

import java.io.PrintStream;
import java.util.Set;

import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.trader.impl.zip.ZIPOrderJob.ZIPBuyOrderJob;
import uk.ac.glasgow.jagora.trader.impl.zip.ZIPOrderJob.ZIPSellOrderJob;
import uk.ac.glasgow.jagora.trader.impl.zip.ZIPTrader;

/**
 * 
 * @author Tim
 *
 */
public class ZIPTraderTargetPriceGnuPlotDATLogger implements OrderListener {
		
	private PrintStream printStream;
	
	private Set<ZIPTrader> zipTraders;
		
	public ZIPTraderTargetPriceGnuPlotDATLogger (PrintStream printStream, Set<ZIPTrader> zipTraders){
		this.printStream = printStream;
		this.zipTraders = zipTraders;
	}

	public void orderEntered(OrderEntryEvent orderEntryEvent) {
				
		Long averageBid = 
			(long) zipTraders
				.stream()
				.map(zipTrader -> zipTrader.getCurrentOrderJob())
				.filter(orderJob -> orderJob instanceof ZIPBuyOrderJob)
				.mapToLong(orderJob -> orderJob.getTargetPrice())
				.average()
				.getAsDouble();
		
		Long averageOffer = 
			(long) zipTraders
				.stream()
				.map(zipTrader -> zipTrader.getCurrentOrderJob())
				.filter(orderJob -> orderJob instanceof ZIPSellOrderJob)
				.mapToLong(orderJob -> orderJob.getTargetPrice())
				.average()
				.getAsDouble();
		
		String template = "%d, %d, %d";
			
		printStream.println(String.format(template, orderEntryEvent.tick, averageBid, averageOffer));
	}

	@Override
	public void orderCancelled(OrderEntryEvent orderEntryEvent) {

	}
}
