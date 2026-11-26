package io.bmsride.monitor;
final class UiRules {
 static int devicePriority(boolean active,boolean supported){return active?0:supported?1:2;}
 static int nextTab(int tab,float dx,float dy,float threshold){if(Math.abs(dx)<threshold||Math.abs(dx)<=Math.abs(dy)*1.5f)return tab;return Math.max(0,Math.min(2,tab+(dx<0?1:-1)));}
}
