package io.bmsride.monitor;
import java.util.*;
public class UiRulesTest {
 static void check(boolean b){if(!b)throw new AssertionError();}
 public static void main(String[] args){
  check(UiRules.nextTab(0,-80,10,64)==1);check(UiRules.nextTab(1,-90,0,64)==2);check(UiRules.nextTab(2,80,10,64)==1);check(UiRules.nextTab(0,100,0,64)==0);check(UiRules.nextTab(2,-100,0,64)==2);check(UiRules.nextTab(1,30,0,64)==1);check(UiRules.nextTab(1,80,100,64)==1);
  List<String> a=new ArrayList<>(Arrays.asList("unknown1","LK8625-A","unknown2","LK8625-B","active"));a.sort(Comparator.comparingInt(s->UiRules.devicePriority(s.equals("active"),s.startsWith("LK8625-"))));check(a.equals(Arrays.asList("active","LK8625-A","LK8625-B","unknown1","unknown2")));
  System.out.println("PASS tab gestures / bounds / vertical rejection / stable device priority");
 }
}
