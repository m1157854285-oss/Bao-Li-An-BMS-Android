package io.bmsride.monitor;
/** Estimate discharge Wh/km using only overlapping valid phone GPS and BLE samples. */
final class TripEnergy {
 private double wh, km;
 void add(double seconds,double distanceKm,double startW,double endW){
  if(!Double.isFinite(seconds)||seconds<=0||seconds>=5||!Double.isFinite(distanceKm)||distanceKm<0||!Double.isFinite(startW)||!Double.isFinite(endW)||startW<0||endW<0)return;
  wh+=(startW+endW)*0.5*seconds/3600;km+=distanceKm;
 }
 double efficiency(){return km>=0.5&&wh>0?wh/km:Double.NaN;}
 void clear(){wh=km=0;}
}
