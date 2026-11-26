package io.bmsride.monitor;
public class TripEnergyTest {
 static void check(boolean ok){if(!ok)throw new AssertionError();}
 public static void main(String[] args){TripEnergy e=new TripEnergy();check(Double.isNaN(e.efficiency()));
 for(int i=0;i<100;i++)e.add(1,0.01,360,360);
 check(Math.abs(e.efficiency()-10)<1e-8);e.add(1,1,Double.NaN,360);e.add(10,1,360,360);check(Math.abs(e.efficiency()-10)<1e-8);
 e.add(1,0,360,360);check(Math.abs(e.efficiency()-10.1)<1e-8);e.clear();check(Double.isNaN(e.efficiency()));System.out.println("PASS paired GPS/BLE energy integration, gaps, reset");}
}
