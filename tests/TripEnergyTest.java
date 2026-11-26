package io.bmsride.monitor;
public class TripEnergyTest {
 static void check(boolean x){if(!x)throw new AssertionError();}
 public static void main(String[] args){
  TripEnergy e=new TripEnergy(); e.add(0,0,360);e.add(1000,.01,360);
  check(Double.isNaN(e.efficiency()));
  for(int i=2;i<=60;i++)e.add(i*1000L,i*.01,360);
  check(Math.abs(e.efficiency()-10)<1e-8);
  e.add(61000,.61,Double.NaN);e.add(62000,1.2,360);e.add(63000,1.21,360);
  check(Math.abs(e.efficiency()-10)<1e-8);
  e.breakInterval();e.add(64000,5,360);check(Math.abs(e.efficiency()-10)<1e-8);
  e.add(100000,6,360);check(Math.abs(e.efficiency()-10)<1e-8);
  e.clear();check(Double.isNaN(e.efficiency()));
  System.out.println("PASS paired GPS/BLE energy, missing data, disconnect, gap, reset");
 }
}
