package io.bmsride.monitor;
public class LastDeviceTest {
 static void check(boolean b){if(!b)throw new AssertionError();}
 public static void main(String[] args){String a="AA:BB:CC:DD:EE:01",b="AA:BB:CC:DD:EE:02";LastDevice last=new LastDevice(null,null);check(!last.hasTarget());check(!last.matches(a,"LK8625-A"));check(last.remember(a,"LK8625-A"));check(last.hasTarget());check(last.matches(a,"LK8625-A"));check(last.matches(a.toLowerCase(),"未知设备"));check(!last.matches(b,"LK8625-A"));check(!last.matches(a,"Other"));check(!last.remember(b,"Other"));check(!last.remember(a,"LK8625-A"));check(last.remember(b,"LK8625-B"));check(!last.matches(a,"LK8625-A"));check(last.matches(b,"LK8625-B"));check(!new LastDevice("invalid","LK8625-X").hasTarget());System.out.println("PASS last validated MAC matching / unnamed advertisement / wrong device rejection / new target replacement");}
}
