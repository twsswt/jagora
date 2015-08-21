package uk.ac.glasgow.jagora.experiment;

public class ExperimentalReportsPathsUtil {

	public static final String experimentalReportsDirectory = "reports/jagora/experiments";

	public static final String experimentalReportDirectory(Class<?> experiment){
		String experimentId = experiment.getSimpleName();
		return experimentalReportsDirectory + "/" + experimentId;
	}

	public static String experimentalPricesDatFilePath(Class<?> experiment) {
		return experimentalReportDirectory(experiment) + "/prices.dat";
	}
}
