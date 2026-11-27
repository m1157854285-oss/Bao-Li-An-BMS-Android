package io.bmsride.monitor;
final class UiRules {
 // Keep brand stable; append the complete advertised name without stripping its model prefix.
 static String connectedTitle(String name){String n=name==null?"":name.replace('\n',' ').replace('\r',' ').trim();if(n.isEmpty()||n.equals("未知设备")||n.equals("保锂安"))return "保锂安";if(n.startsWith("保锂安")){n=n.substring("保锂安".length()).trim();if(n.isEmpty())return "保锂安";}return "保锂安 "+n;}
 static int devicePriority(boolean active,boolean supported){return active?0:supported?1:2;}
 static <T> java.util.Map<String,T> withActive(java.util.Map<String,T> scanned,String address,T active){java.util.Map<String,T> out=new java.util.LinkedHashMap<>();if(active!=null)out.put(address,active);for(java.util.Map.Entry<String,T> e:scanned.entrySet())out.putIfAbsent(e.getKey(),e.getValue());return out;}
 static String formatNumber(double n,int decimals){return Double.isFinite(n)?String.format(java.util.Locale.US,"%."+decimals+"f",n):"—";}
 // BMS current uses negative for discharge and positive for charging.
 static double powerMagnitude(double signedWatts){return Math.abs(signedWatts);}
 static String powerTitle(double signedWatts){return !Double.isFinite(signedWatts)||signedWatts==0?"实时功率":signedWatts<0?"放电功率":"充电功率";}
 static boolean deltaWarning(double volts){return Double.isFinite(volts)&&Math.round(volts*1000)>=100;}
 // Equal temperatures (or a single valid probe) remain neutral.
 static int temperatureRank(double[] temperatures,int index){double v=temperatures[index],lo=Double.POSITIVE_INFINITY,hi=Double.NEGATIVE_INFINITY;if(!Double.isFinite(v))return 0;for(double t:temperatures)if(Double.isFinite(t)){lo=Math.min(lo,t);hi=Math.max(hi,t);}return hi>lo?(v==hi?1:v==lo?-1:0):0;}
 static int dragTarget(int tab,float dx){int target=tab+(dx<0?1:dx>0?-1:0);return target==tab||target<0||target>2?-1:target;}
 static boolean commitDrag(float dx,float width,long durationMs){float d=Math.abs(dx);return d>=width*.27f||(durationMs>0&&durationMs<300&&d>=width*.10f&&d/durationMs>=width/450f);}
}
