set terminal png size 1200,800
set datafile separator ","
set output "prices.png"
#set yrange [0:20]
plot 'prices.dat'  using 1:4 with lines\
     , 'zip-targets.dat'  every 1000 using 1:2 with lines\
     , 'zip-targets.dat'  every 1000 using 1:3  with lines
