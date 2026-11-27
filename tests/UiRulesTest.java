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
  check(UiRules.formatNumber(77.58,1).equals("77.6"));check(UiRules.formatNumber(36.6,0).equals("37"));
  check(UiRules.deltaWarning(3.4-3.3));check(!UiRules.deltaWarning(.099));check(!UiRules.deltaWarning(Double.NaN));
  check(UiRules.temperatureRank(new double[]{26.5,27.1,26.8},0)==-1);check(UiRules.temperatureRank(new double[]{26.5,27.1,26.8},1)==1);check(UiRules.temperatureRank(new double[]{26.5,27.1,26.8},2)==0);check(UiRules.temperatureRank(new double[]{26.5,26.5,26.5},1)==0);check(UiRules.temperatureRank(new double[]{Double.NaN,26.5,Double.NaN},1)==0);
  check(UiRules.powerMagnitude(-1200)==1200&&UiRules.powerTitle(-1200).equals("放电功率"));check(UiRules.powerMagnitude(300)==300&&UiRules.powerTitle(300).equals("充电功率"));check(Double.isNaN(UiRules.powerMagnitude(Double.NaN)));
  check(UiRules.connectedTitle(null).equals("保锂安"));check(UiRules.connectedTitle("未知设备").equals("保锂安"));check(UiRules.connectedTitle("LK8625-A66542").equals("保锂安 LK8625-A66542"));check(UiRules.connectedTitle("保锂安 LK8625-A66542").equals("保锂安 LK8625-A66542"));check(UiRules.connectedTitle("保锂安").equals("保锂安"));
  System.out.println("PASS stable brand title / complete connected device name / no duplicate brand");
  System.out.println("PASS display precision / 100mV threshold / temperature extrema / BMS power direction");
  System.out.println("PASS drag target / snap thresholds / tab bounds / stable device priority / active device dedup / numeric formatting");
 }
}
