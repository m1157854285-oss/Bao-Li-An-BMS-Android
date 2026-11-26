package io.bmsride.monitor;
import java.util.*;
import java.nio.charset.StandardCharsets;
public class Lk86Test {
 static byte[] frame(int cmd,byte[] d){byte[] f=new byte[d.length+7];f[0]=0x3b;f[1]=0x7e;f[2]=1;f[3]=1;f[4]=(byte)cmd;f[5]=(byte)d.length;System.arraycopy(d,0,f,6,d.length);int s=0;for(int i=0;i<f.length-1;i++)s+=f[i]&255;f[f.length-1]=(byte)s;return f;}
 static void put(byte[] a,int i,int v){a[i]=(byte)v;a[i+1]=(byte)(v>>8);}
 static void check(boolean x){if(!x)throw new AssertionError();}
 public static void main(String[] args){
  Lk86Decoder d=new Lk86Decoder();byte[] live=new byte[38];put(live,0,6877);put(live,2,0);put(live,4,5000);byte[] c=new byte[45];c[0]=20;
  int[] mv={3409,3408,3447,3400,3418,3440,3461,3463,3455,3440,3512,3472,3453,3449,3461,3414,3429,3434,3415,3390};for(int i=0;i<20;i++)put(c,5+i*2,mv[i]);
  check(d.feed(frame(0x1e,live),0)==null);d.feed(frame(2,"LR365V10_N031x".getBytes(StandardCharsets.US_ASCII)),10);check(d.confirmed);
  check(d.feed(frame(0x1e,live),20)==null);byte[] cf=frame(0x20,c);check(d.feed(Arrays.copyOfRange(cf,0,19),30)==null);Protocol.Battery b=d.feed(Arrays.copyOfRange(cf,19,cf.length),40);check(b!=null&&b.soc==100&&b.voltage==68.77&&Math.abs(b.delta()-0.122)<1e-8&&Double.isNaN(b.ah)&&Double.isNaN(b.mos));
  byte[] bad=cf.clone();bad[bad.length-1]^=1;check(d.feed(bad,50)==null);check(d.feed(cf,4000)==null);check(d.feed(frame(0x1e,live),4100)!=null);
  Lk86Decoder wrong=new Lk86Decoder();wrong.feed(frame(2,"LR359V13_N031x".getBytes(StandardCharsets.US_ASCII)),1);check(!wrong.confirmed);check(wrong.feed(cf,2)==null);
  boolean rejected=false;try{Lk86Decoder.request(0x3c);}catch(IllegalArgumentException e){rejected=true;}check(rejected);check(Arrays.equals(Lk86Decoder.request(2),new byte[]{0x3a,0x7e,1,1,2,0,(byte)0xbc}));
  // Latest screenshot: 20 cells, three temperature probes, status bits from related-model protocol.
  Lk86Decoder s=new Lk86Decoder();s.feed(frame(2,"LR365V10_N031x".getBytes(StandardCharsets.US_ASCII)),0);
  int[] mv2={3287,3287,3287,3288,3288,3288,3287,3288,3288,3288,3285,3285,3285,3285,3285,3285,3285,3285,3285,3286};
  byte[] c2=new byte[45];put(c2,0,20);for(int i=0;i<20;i++)put(c2,5+i*2,mv2[i]);
  byte[] l2=new byte[38];put(l2,0,6573);put(l2,4,3900);l2[36]=(byte)0xc0;
  byte[] t2=new byte[8];put(t2,0,3);put(t2,2,2827);put(t2,4,2828);put(t2,6,2813);
  s.feed(frame(0x1e,l2),100);b=s.feed(frame(0x20,c2),200);check(b!=null&&Math.abs(b.delta()-0.003)<1e-8&&b.mosFlags==192);
  b=s.feed(frame(0x1f,t2),300);check(b!=null&&Arrays.equals(b.temps,new double[]{9.6,9.7,8.2})&&Double.isNaN(b.mos));
  check(s.feed(frame(0x1f,new byte[]{3,0,0,0}),350)==null || s.temps.length==0);check(s.temps.length==0);
  check(s.feed(frame(0x20,new byte[44]),400)==null);check(s.feed(frame(0x20,c2),17000)==null);b=s.feed(frame(0x1e,l2),17100);check(b!=null&&b.temps.length==0);
  System.out.println("PASS 20S 3mV / three probes / flags / malformed and stale data");
  System.out.println("PASS LK86 synthetic 20S screenshot values, fragmented frame, checksum, stale data, model gate, read whitelist, missing capacity/MOS");
 }
}
