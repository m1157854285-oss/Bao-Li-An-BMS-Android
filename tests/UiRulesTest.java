package io.bmsride.monitor;
import java.util.*;
public class UiRulesTest {
 static void check(boolean b){if(!b)throw new AssertionError();}
 public static void main(String[] args){
  check(UiRules.dragTarget(0,-5)==1);check(UiRules.dragTarget(0,5)==-1);check(UiRules.dragTarget(2,-5)==-1);check(UiRules.dragTarget(2,5)==1);check(UiRules.dragTarget(1,0)==-1);
  check(!UiRules.commitDrag(80,400,1500));check(UiRules.commitDrag(120,400,1500));check(UiRules.commitDrag(65,400,60));check(!UiRules.commitDrag(15,400,50));
  List<String> a=new ArrayList<>(Arrays.asList("unknown1","LK8625-A","unknown2","LK8625-B","active"));a.sort(Comparator.comparingInt(s->UiRules.devicePriority(s.equals("active"),s.startsWith("LK8625-"))));check(a.equals(Arrays.asList("active","LK8625-A","LK8625-B","unknown1","unknown2")));
  Map<String,String> scanned=new LinkedHashMap<>();scanned.put("other","other");Map<String,String> visible=UiRules.withActive(scanned,"active","active");check(new ArrayList<>(visible.keySet()).equals(Arrays.asList("active","other")));check(scanned.size()==1);scanned.put("active","advertisement");check(UiRules.withActive(scanned,"active","current").size()==2&&UiRules.withActive(scanned,"active","current").get("active").equals("current"));check(UiRules.withActive(scanned,"",null).size()==2);
  check(UiRules.formatNumber(-5.4,2).equals("-5.40"));check(UiRules.formatNumber(66.7,2).equals("66.70"));check(UiRules.formatNumber(.037,3).equals("0.037"));check(UiRules.formatNumber(3.33,3).equals("3.330"));check(UiRules.formatNumber(Double.NaN,3).equals("—"));
  System.out.println("PASS drag target / snap thresholds / tab bounds / stable device priority / active device dedup / numeric formatting");
 }
}
