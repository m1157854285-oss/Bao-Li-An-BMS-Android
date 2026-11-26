package io.bmsride.monitor;
import java.util.Arrays;
/** Shared battery data model for the read-only LK8625 decoder. */
public final class Protocol {
 public static final class Battery {
  public final double voltage,current,ah,soc,mos; public final double[] cells,temps; public final long time; public final int mosFlags; public final String alarm,protection,fault;
  Battery(double v,double a,double ah,double soc,double m,double[] c,long ts,double[] temps,int flags,String alarm,String protection,String fault){voltage=v;current=a;this.ah=ah;this.soc=soc;mos=m;cells=c.clone();time=ts;this.temps=temps.clone();mosFlags=flags;this.alarm=alarm;this.protection=protection;this.fault=fault;}
  public double delta(){return cells.length==20?Arrays.stream(cells).max().getAsDouble()-Arrays.stream(cells).min().getAsDouble():Double.NaN;}
 }
 static boolean validCells(double[] c){if(c.length!=20)return false;for(double v:c)if(!Double.isFinite(v)||v<0.5||v>5.0)return false;return true;}
}
