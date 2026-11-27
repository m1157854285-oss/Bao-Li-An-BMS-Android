package io.bmsride.monitor;
/** Only a device that supplied valid BMS data may replace the remembered target. */
final class LastDevice {
 String address,name;
 LastDevice(String address,String name){this.address=address==null?"":address;this.name=name==null?"":name;}
 static boolean supportedName(String n){return n!=null&&n.startsWith("LK8625-");}
 boolean hasTarget(){return address.matches("(?i)[0-9a-f]{2}(:[0-9a-f]{2}){5}")&&supportedName(name);}
 boolean matches(String a,String n){return hasTarget()&&address.equalsIgnoreCase(a)&&(supportedName(n)||n==null||n.trim().isEmpty()||n.equals("未知设备"));}
 boolean remember(String a,String n){if(a==null||!a.matches("(?i)[0-9a-f]{2}(:[0-9a-f]{2}){5}")||!supportedName(n))return false;if(a.equalsIgnoreCase(address)&&n.equals(name))return false;address=a;name=n;return true;}
}
