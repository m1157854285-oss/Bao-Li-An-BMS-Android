package io.bmsride.monitor;
import java.util.*;
public final class Protocol {
 public enum Kind { JBD, JK24, JK32, BAOLIAN }
 public static final class Battery {
  public final double voltage,current,ah,soc,temp,mos; public final double[] cells; public final long time; public final double[] temps; public final int mosFlags; public final String alarm,protection,fault;
  Battery(double v,double a,double ah,double soc,double t,double m,double[] c,long ts){this(v,a,ah,soc,t,m,c,ts,new double[0],-1,null,null,null);}
  Battery(double v,double a,double ah,double soc,double t,double m,double[] c,long ts,double[] temps,int flags,String alarm,String protection,String fault){voltage=v;current=a;this.ah=ah;this.soc=soc;temp=t;mos=m;cells=c.clone();time=ts;this.temps=temps.clone();mosFlags=flags;this.alarm=alarm;this.protection=protection;this.fault=fault;}
  public double delta(){return cells.length==20?Arrays.stream(cells).max().getAsDouble()-Arrays.stream(cells).min().getAsDouble():Double.NaN;}
 }
 static int u(byte[] b,int i){return b[i]&255;} static int be(byte[] b,int i){return u(b,i)*256+u(b,i+1);} static int le(byte[] b,int i){return u(b,i)+256*u(b,i+1);} static int le32(byte[] b,int i){return le(b,i)|(le(b,i+2)<<16);}
 public static byte[] request(Kind k,int reg){if(k==Kind.JBD)return new byte[]{(byte)0xdd,(byte)0xa5,(byte)reg,0,(byte)0xff,(byte)(256-reg),0x77}; if(k==Kind.JK24||k==Kind.JK32){byte[] b=new byte[20];b[0]=(byte)0xaa;b[1]=0x55;b[2]=(byte)0x90;b[3]=(byte)0xeb;b[4]=(byte)0x96;int s=0;for(int i=0;i<19;i++)s+=u(b,i);b[19]=(byte)s;return b;}if(k==Kind.BAOLIAN)return Lk86Decoder.request(reg);throw new IllegalArgumentException();}
 static boolean validCells(double[] c){if(c.length!=20)return false;for(double v:c)if(v<0.5||v>5.0)return false;return true;}
 public static final class Decoder {
  final Kind kind; final Lk86Decoder lk; byte[] buf=new byte[0]; byte[] basic; long basicAt;
  public Decoder(Kind k){kind=k;lk=k==Kind.BAOLIAN?new Lk86Decoder():null;}
  public Battery feed(byte[] part,long now){if(kind==Kind.BAOLIAN)return lk.feed(part,now);byte[] a=Arrays.copyOf(buf,buf.length+part.length);System.arraycopy(part,0,a,buf.length,part.length);buf=a;Battery out=null;
   while(buf.length>0){boolean j=kind==Kind.JBD;int start=0;while(start<buf.length&&u(buf,start)!=(j?0xdd:0x55))start++;buf=Arrays.copyOfRange(buf,start,buf.length);if(buf.length<4)break;
    if(!j&&(u(buf,1)!=0xaa||u(buf,2)!=0xeb||u(buf,3)!=0x90)){buf=Arrays.copyOfRange(buf,1,buf.length);continue;}
    int n=j?u(buf,3)+7:300;if(buf.length<n)break;byte[] f=Arrays.copyOf(buf,n);boolean ok;if(j){int s=0;for(int i=2;i<n-3;i++)s+=u(f,i);ok=u(f,n-1)==0x77&&be(f,n-3)==((-s)&65535);}else{int s=0;for(int i=0;i<299;i++)s+=u(f,i);ok=(s&255)==u(f,299);}
    buf=Arrays.copyOfRange(buf,ok?n:1,buf.length);if(!ok)continue;Battery x=j?jbd(f,now):jk(f,now);if(x!=null)out=x;
   } if(buf.length>1024)buf=new byte[0];return out;}
  Battery jbd(byte[] f,long now){if(u(f,2)!=0)return null;int len=u(f,3),reg=u(f,1);byte[] p=Arrays.copyOfRange(f,4,4+len);if(reg==3){basic=null;if(p.length>=23&&u(p,21)==20&&u(p,19)<=100){basic=p;basicAt=now;}return null;}if(reg!=4||p.length!=40||basic==null||now-basicAt>3000)return null;double[] c=new double[20];for(int i=0;i<20;i++)c[i]=be(p,i*2)/1000.0;if(!validCells(c))return null;byte[] q=basic;double v=be(q,0)/100.0, a=(short)be(q,2)/100.0, ah=be(q,4)/100.0;int t=u(q,22),tail=23+2*t;if(q.length<tail)return null;if(q.length>tail&&u(q,tail)==0x88){a*=10;ah*=10;}double temp=t>0?(be(q,23)-2731)/10.0:Double.NaN;if(Math.abs(Arrays.stream(c).sum()-v)>3)return null;return new Battery(v,a,ah,u(q,19),temp,Double.NaN,c,now);}
  Battery jk(byte[] f,long now){if(u(f,4)!=2)return null;int o=kind==Kind.JK32?16:0,p=o*2;int mask=le32(f,54+o);if(mask!=0xfffff)return null;double[] c=new double[20];for(int i=0;i<20;i++)c[i]=le(f,6+2*i)/1000.0;double v=Integer.toUnsignedLong(le32(f,118+p))/1000.0,a=le32(f,126+p)/1000.0,ah=Integer.toUnsignedLong(le32(f,142+p))/1000.0;int soc=u(f,141+p);if(!validCells(c)||soc>100||Math.abs(Arrays.stream(c).sum()-v)>3)return null;return new Battery(v,a,ah,soc,(short)le(f,130+p)/10.0,(short)le(f,kind==Kind.JK32?128:134)/10.0,c,now);}
 }
}
