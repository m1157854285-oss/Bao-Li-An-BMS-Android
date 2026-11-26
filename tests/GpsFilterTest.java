package io.bmsride.monitor;
public class GpsFilterTest {
 static void check(boolean b){if(!b)throw new AssertionError();}
 static void close(double a,double b){check(Math.abs(a-b)<1e-8);}
 public static void main(String[] args){
  GpsFilter f=new GpsFilter();long t=1000;
  for(int i=0;i<60;i++,t+=1000){GpsFilter.Result r=f.add(t,8,(i%7)*.15,.4);close(r.kmh,0);close(r.meters,0);}
  close(f.add(t,8,4,.4).kmh,0);t+=1000;close(f.add(t,8,.2,.4).kmh,0);t+=1000;
  for(int i=0;i<2;i++,t+=1000){GpsFilter.Result r=f.add(t,6,6,.2);close(r.kmh,0);close(r.meters,0);}
  GpsFilter.Result r=f.add(t,6,6,.2);close(r.kmh,21.6);close(r.meters,0);t+=1000;
  r=f.add(t,6,6,.2);close(r.kmh,21.6);close(r.meters,6);t+=1000;
  r=f.add(t,6,.4,.2);close(r.kmh,0);close(r.meters,0);t+=1000;
  check(Double.isNaN(f.add(t,45,6,.2).kmh));t+=1000;
  check(Double.isNaN(f.add(t,8,6,3).kmh));t+=1000;
  check(Double.isNaN(f.add(t,8,Double.NaN,.2).kmh));t+=1000;
  for(int i=0;i<3;i++,t+=1000)r=f.add(t,8,5,Double.NaN);close(r.kmh,18);
  r=f.add(t+5000,8,5,.3);close(r.kmh,0);close(r.meters,0);
  f.reset();close(f.add(t+6000,8,.1,.3).kmh,0);
  System.out.println("PASS GPS stationary jitter / spike rejection / start / stop / distance / uncertainty / gaps / reset");
 }
}
