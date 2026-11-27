package io.bmsride.monitor;
public class RangeEstimateTest {
 static void check(boolean b){if(!b)throw new AssertionError();}
 static void close(double a,double b){check(Math.abs(a-b)<1e-8);}
 public static void main(String[] args){
  TripEnergy e=new TripEnergy(); RangeEstimate r=new RangeEstimate();
  check(Double.isNaN(r.update(60,30,e.efficiency()))&&!r.current());
  for(int i=0;i<=60;i++)e.add(i*1000L,i*.01,360);
  close(e.efficiency(),10);close(r.update(60,30,e.efficiency()),180);check(r.current());
  e.breakInterval();close(e.efficiency(),10);close(r.update(60,30,e.efficiency()),180);check(r.current());
  close(r.update(Double.NaN,Double.NaN,e.efficiency()),180);check(!r.current());
  e.clear();check(Double.isNaN(e.efficiency()));close(r.update(60,30,e.efficiency()),180);check(!r.current());
  for(int i=0;i<=60;i++)e.add(i*1000L,i*.01,720);
  close(r.update(60,30,e.efficiency()),90);check(r.current());
  r.clear();check(Double.isNaN(r.update(60,30,Double.NaN))&&!r.current());
  System.out.println("PASS pause / disconnect / reset retains last range / next trip replaces estimate / battery change clears cache");
 }
}
