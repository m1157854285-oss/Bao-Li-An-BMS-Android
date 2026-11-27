package io.bmsride.monitor;
public class GpsFilterTest {
 static void check(boolean b){if(!b)throw new AssertionError();}
 static void close(double a,double b){check(Math.abs(a-b)<1e-8);}
 public static void main(String[] args){
  GpsFilter f=new GpsFilter();long t=1000;
  for(int i=0;i<60;i++,t+=1000){GpsFilter.Result r=f.add(t,8,(i%5)*.05,.4);close(r.kmh,0);close(r.meters,0);}
  close(f.add(t,8,.8,2).kmh,2.88);t+=1000;
  GpsFilter.Result r=f.add(t,30,1.0,2.5);close(r.kmh,3.6);close(r.meters,.9);t+=1000;
  r=f.add(t,8,1.2,Double.NaN);close(r.kmh,4.32);close(r.meters,1.1);t+=1000;
  r=f.add(t,8,6,.2);close(r.kmh,21.6);t+=1000;
  r=f.add(t,8,0,.2);close(r.kmh,0);t+=1000;
  check(Double.isNaN(f.add(t,45,6,.2).kmh));t+=1000;
  check(Double.isNaN(f.add(t,8,Double.NaN,.2).kmh));t+=1000;
  r=f.add(t,8,.9,.5);close(r.kmh,3.24);close(r.meters,0);
  r=f.add(t+6000,8,.9,.5);close(r.kmh,3.24);close(r.meters,0);
  f.reset();close(f.add(t+7000,8,.1,.3).kmh,0);
  f.reset();TripHistory hist=new TripHistory();
  // A short peak between one-second display refreshes is retained at full precision.
  hist.observeSpeed(f.add(1000,8,36.6/3.6,.3).kmh);
  hist.observeSpeed(f.add(1250,8,39.1/3.6,.3).kmh);
  hist.observeSpeed(f.add(1500,8,37/3.6,.3).kmh);
  close(hist.maxSpeed(),39.1);check(UiRules.formatNumber(hist.maxSpeed(),0).equals("39"));
  System.out.println("PASS sub-second accepted GPS peak retained / integer maximum display");
  System.out.println("PASS GPS walking / immediate start / near-zero / riding / gaps / stale-invalid / reset");
 }
}
