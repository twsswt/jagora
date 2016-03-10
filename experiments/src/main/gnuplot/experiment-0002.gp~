#!/usr/bin/gnuplot

set terminal png size 1200,800

EXPERIMENTAL_REPORTS_DIR='reports/jagora/experiments'
EXPERIMENT_REPORT_DIR=EXPERIMENTAL_REPORTS_DIR . '/Experiment0002'

set yrange [0:500]

set datafile separator ","

set output EXPERIMENT_REPORT_DIR.'/prices.png'

set ylabel 'price'
set xlabel 'ticks'

plot	EXPERIMENT_REPORT_DIR.'/prices.dat' using 1:2 with lines title "sell orders", \
		EXPERIMENT_REPORT_DIR.'/prices.dat' using 1:3 with lines title "buy orders", \
		EXPERIMENT_REPORT_DIR.'/prices.dat' using 1:4 with lines title "trades"