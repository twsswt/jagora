#! /usr/bin/gnuplot

EXPERIMENTAL_REPORTS_DIR='target/jagora-reports'
EXPERIMENT_REPORT_DIR=EXPERIMENTAL_REPORTS_DIR . '/Experiment0003'

set terminal png size 1200,800
set datafile separator ","

set output EXPERIMENT_REPORT_DIR.'/prices.png'

plot	EXPERIMENT_REPORT_DIR.'/zip-targets.dat' every 20  using 1:2 with lines title "buy targets", \
		EXPERIMENT_REPORT_DIR.'/zip-targets.dat' every 20  using 1:3 with lines title "sell targets", \
		EXPERIMENT_REPORT_DIR.'/prices.dat'  using 1:4 with lines title "trades"
