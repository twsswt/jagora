package uk.ac.glasgow.jagora.experiment;

public class ExperimentalReportsPathsUtil {

	public static final String experimentalReportsDirectory = "target/jagora-reports/";

	public static final String experimentalReportDirectory(Class<?> experiment){
		String experimentId = experiment.getSimpleName();
		return experimentalReportsDirectory + "/" + experimentId;
	}

	public static String experimentalPricesDatFilePath(Class<?> experiment) {
		return experimentalReportDirectory(experiment) + "/prices.dat";
	}
}
