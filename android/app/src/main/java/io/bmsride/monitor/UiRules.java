package io.bmsride.monitor;
final class UiRules {
 static int devicePriority(boolean active,boolean supported){return active?0:supported?1:2;}
 static int nextTab(int tab,float dx,float dy,float threshold){if(Math.abs(dx)<threshold||Math.abs(dx)<=Math.abs(dy)*1.5f)return tab;return Math.max(0,Math.min(2,tab+(dx<0?1:-1)));}
 static int dragTarget(int tab,float dx){int target=tab+(dx<0?1:dx>0?-1:0);return target==tab||target<0||target>2?-1:target;}
 static boolean commitDrag(float dx,float width,long durationMs){float d=Math.abs(dx);return d>=width*.27f||(durationMs>0&&durationMs<300&&d>=width*.10f&&d/durationMs>=width/450f);}
}
