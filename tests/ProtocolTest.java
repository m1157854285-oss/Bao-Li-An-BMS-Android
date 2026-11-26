package io.bmsride.monitor;
public class ProtocolTest {
 static void check(boolean b){if(!b)throw new AssertionError();}
 public static void main(String[] args){
  double[] c=new double[20];java.util.Arrays.fill(c,3.3);c[0]=3.35;
  check(Protocol.validCells(c));Protocol.Battery b=new Protocol.Battery(66.05,-5.42,32.6,77.58,Double.NaN,c,1,new double[]{26.5,26.8,27.1},192,null,null,null);
  check(Math.abs(b.delta()-.05)<1e-9);c[0]=Double.NaN;check(!Protocol.validCells(c));check(b.cells[0]==3.35);check(!Protocol.validCells(new double[19]));
  System.out.println("PASS LK8625 shared battery model / valid cells / delta / defensive copy");
 }
}
