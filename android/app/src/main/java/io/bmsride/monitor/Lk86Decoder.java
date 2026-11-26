package io.bmsride.monitor;
import java.util.Arrays;
import java.nio.charset.StandardCharsets;
/** Experimental read-only LK86xx adapter for LR365V10_N031x. Protocol reference:
 * https://github.com/patman15/BMS_BLE-HA/issues/879 (related LR359 device, not this model).
 */
final class Lk86Decoder {
 static final int MODEL=2, CAPACITY=9, LIVE=0x1e, TEMPS=0x1f, CELLS=0x20;
 static byte[] request(int cmd){
  if(cmd!=MODEL&&cmd!=CAPACITY&&cmd!=LIVE&&cmd!=TEMPS&&cmd!=CELLS)throw new IllegalArgumentException("read whitelist");
  byte[] b={0x3a,0x7e,1,1,(byte)cmd,0,0};b[6]=(byte)sum(b,6);return b;
 }
 static int u(byte[] b,int i){return b[i]&255;} static int le(byte[] b,int i){return u(b,i)|(u(b,i+1)<<8);} static int sum(byte[] b,int n){int s=0;for(int i=0;i<n;i++)s+=u(b,i);return s&255;}
 byte[] pending=new byte[0]; String model=""; boolean confirmed; long liveAt=-1,cellAt=-1,tempAt=-1; double v,a,soc,full=Double.NaN; double[] cells,temps=new double[0]; int flags=-1; String alarm,protection,fault;
 static String hex(byte[] d,int from,int to){StringBuilder b=new StringBuilder();for(int i=from;i<to;i++)b.append(String.format(java.util.Locale.US,"%02X",u(d,i)));return b.toString();}
 Protocol.Battery feed(byte[] chunk,long now){
  if(chunk.length>1024){pending=new byte[0];return null;}
  byte[] all=Arrays.copyOf(pending,pending.length+chunk.length);System.arraycopy(chunk,0,all,pending.length,chunk.length);pending=all;Protocol.Battery out=null;
  while(pending.length>=2){if(u(pending,0)!=0x3b||u(pending,1)!=0x7e){drop(1);continue;}if(pending.length<6)break;if(u(pending,2)!=1){drop(1);continue;}int n=u(pending,5)+7;if(pending.length<n)break;if(sum(pending,n-1)!=u(pending,n-1)){drop(1);continue;}int cmd=u(pending,4),flag=u(pending,3);byte[] data=Arrays.copyOfRange(pending,6,n-1);drop(n);if(flag!=1)continue;Protocol.Battery b=accept(cmd,data,now);if(b!=null)out=b;}
  return out;
 }
 void drop(int n){pending=Arrays.copyOfRange(pending,n,pending.length);}
 Protocol.Battery accept(int cmd,byte[] d,long now){
  if(cmd==MODEL){model=new String(d,StandardCharsets.US_ASCII).replace("\0","").trim();confirmed=model.startsWith("LR365V10_N031x");return null;}
  if(!confirmed)return null;
  if(cmd==CAPACITY&&d.length>=8){double x=le(d,4)/100.0;full=x>0?x:Double.NaN;}
  else if(cmd==LIVE){liveAt=-1;if(d.length<38)return null;double vv=le(d,0)/100.0,aa=(short)le(d,2)/100.0,ss=le(d,4)/50.0;if(vv<10||vv>100||ss>100)return null;v=vv;a=aa;soc=ss;flags=u(d,36);alarm=hex(d,26,30);protection=hex(d,30,34);fault=hex(d,34,36);liveAt=now;}
  else if(cmd==CELLS){cellAt=-1;if(d.length<5)return null;int count=le(d,0);if(count<1||count>32||d.length!=5+2*count)return null;double[] c=new double[count];for(int i=0;i<count;i++)c[i]=le(d,5+i*2)/1000.0;if(!Protocol.validCells(c))return null;cells=c;cellAt=now;}
  else if(cmd==TEMPS){tempAt=-1;temps=new double[0];if(d.length<2)return null;int n=le(d,0);if(n!=3||d.length!=2+2*n)return null;double[] t3=new double[n];for(int i=0;i<n;i++){double t=(le(d,2+i*2)-2731)/10.0;t3[i]=(t>=-50&&t<=120)?t:Double.NaN;}temps=t3;tempAt=now;}
  else return null;
  if(cmd!=LIVE&&cmd!=CELLS&&cmd!=TEMPS&&cmd!=CAPACITY)return null;
  if(liveAt<0||cellAt<0||Math.abs(liveAt-cellAt)>2500||now-liveAt>2500||now-cellAt>2500)return null;
  if(Math.abs(Arrays.stream(cells).sum()-v)>1.0)return null;
  return new Protocol.Battery(v,a,Double.isFinite(full)?full*soc/100:Double.NaN,soc,Double.NaN,cells,Math.min(liveAt,cellAt),tempAt>=0&&now-tempAt<15000?temps:new double[0],flags,alarm,protection,fault);
 }
}
