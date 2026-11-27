package io.bmsride.monitor;
/** Retain the last calculated estimate when a trip is paused/reset or data is absent.
 * The UI distinguishes a retained estimate from one based on current trip data. */
final class RangeEstimate {
 private double last=Double.NaN;
 private boolean current;
 double update(double voltage,double ah,double efficiency){
  current=false;
  if(Double.isFinite(voltage)&&voltage>0&&Double.isFinite(ah)&&ah>=0&&Double.isFinite(efficiency)&&efficiency>0){
   double r=voltage*ah/efficiency;
   if(Double.isFinite(r)&&r>=0){last=r;current=true;}
  }
  return last;
 }
 boolean current(){return current;}
 void clear(){last=Double.NaN;current=false;}
}
