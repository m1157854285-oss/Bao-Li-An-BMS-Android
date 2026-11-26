package io.bmsride.monitor;
final class UiRules {
 static int devicePriority(boolean active,boolean supported){return active?0:supported?1:2;}
 static <T> java.util.Map<String,T> withActive(java.util.Map<String,T> scanned,String address,T active){java.util.Map<String,T> out=new java.util.LinkedHashMap<>();if(active!=null)out.put(address,active);for(java.util.Map.Entry<String,T> e:scanned.entrySet())out.putIfAbsent(e.getKey(),e.getValue());return out;}
 static String formatNumber(double n,int decimals){return Double.isFinite(n)?String.format(java.util.Locale.US,"%."+decimals+"f",n):"—";}
 static int dragTarget(int tab,float dx){int target=tab+(dx<0?1:dx>0?-1:0);return target==tab||target<0||target>2?-1:target;}
 static boolean commitDrag(float dx,float width,long durationMs){float d=Math.abs(dx);return d>=width*.27f||(durationMs>0&&durationMs<300&&d>=width*.10f&&d/durationMs>=width/450f);}
}
