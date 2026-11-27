package io.bmsride.monitor;
import android.bluetooth.*;import android.bluetooth.le.*;import android.content.*;import android.os.*;import java.util.*;
@SuppressWarnings("MissingPermission") final class Ble {
 interface Listener {void status(String s);void data(Protocol.Battery b);}
 final Context ctx;final Handler h=new Handler(Looper.getMainLooper());final BluetoothAdapter adapter;final Listener l;BluetoothGatt g;BluetoothGattCharacteristic tx;Lk86Decoder parser;boolean ready,busy,scanning,connected;int reg=3,lkPoll=0;long lastWrite;final Map<String,BluetoothDevice> devices=new LinkedHashMap<>();final Map<String,Integer> rssi=new HashMap<>();final Map<String,String> names=new HashMap<>();long dataFrames=0;final Runnable scanTimeout=()->stopScan();
 final SharedPreferences prefs; final LastDevice last; String autoTarget=""; boolean automaticConnection; ScanCallback activeScan;
 final Runnable connectTimeout=()->{if(g!=null&&dataFrames==0)fail("连接或读取超时");};
 boolean autoSearching(){return scanning&&!autoTarget.isEmpty();}
 String name(BluetoothDevice d){String n=names.get(d.getAddress());if(n==null||n.trim().isEmpty())n=d.getName();return n==null||n.trim().isEmpty()?"未知设备":n;}
 boolean supported(BluetoothDevice d){return name(d).startsWith("LK8625-");}
 static UUID id(int v){return UUID.fromString(String.format(Locale.US,"0000%04x-0000-1000-8000-00805f9b34fb",v));}
 Ble(Context c,Listener l){ctx=c;this.l=l;adapter=c.getSystemService(BluetoothManager.class).getAdapter();prefs=c.getSharedPreferences("bms",Context.MODE_PRIVATE);last=new LastDevice(prefs.getString("last_bms_address",""),prefs.getString("last_bms_name",""));}
 // Scan all devices, but only auto-connect the exact previously validated address.
 void autoConnectLast(){if(g!=null||scanning||!last.hasTarget())return;startScan(last.address);}
 void scan(){if(automaticConnection&&dataFrames==0)close();startScan("");}
 void startScan(String target){
  stopScan(); BluetoothDevice active=g==null?null:g.getDevice();String activeName=active==null?null:name(active);
  devices.clear();rssi.clear();names.clear();if(active!=null){devices.put(active.getAddress(),active);names.put(active.getAddress(),activeName);}
  if(adapter==null||!adapter.isEnabled()||adapter.getBluetoothLeScanner()==null){l.status("请打开蓝牙 · 可手动扫描设备");return;}
  autoTarget=target;scanning=true;
  final ScanCallback callback=new ScanCallback(){
   public void onScanResult(int type,ScanResult r){h.post(()->{if(!scanning||activeScan!=this)return;BluetoothDevice d=r.getDevice();String a=d.getAddress();devices.put(a,d);rssi.put(a,r.getRssi());String n=r.getScanRecord()==null?null:r.getScanRecord().getDeviceName();if(n!=null&&!n.trim().isEmpty())names.put(a,n);
    if(g==null&&!autoTarget.isEmpty()&&last.matches(a,name(d))){if(!supported(d))names.put(a,last.name);connectInternal(d,true);}
   });}
   public void onScanFailed(int e){h.post(()->{if(activeScan!=this)return;stopScan();l.status("扫描失败 "+e+" · 可手动重试");});}
  };activeScan=callback;
  try{adapter.getBluetoothLeScanner().startScan(null,new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(0).build(),callback);if(g==null)l.status(autoSearching()?"正在查找上次连接的设备…":"扫描中");h.postDelayed(scanTimeout,8000);}catch(RuntimeException ex){stopScan();l.status("扫描失败 · 可手动重试");}
 }
 void stopScan(){h.removeCallbacks(scanTimeout);boolean was=scanning,auto=autoSearching();ScanCallback callback=activeScan;scanning=false;activeScan=null;autoTarget="";if(was&&callback!=null&&adapter!=null){try{BluetoothLeScanner scanner=adapter.getBluetoothLeScanner();if(scanner!=null)scanner.stopScan(callback);}catch(RuntimeException ignored){}}if(was&&g==null)l.status(auto?"未找到上次设备 · 请选择设备手动连接":"扫描结束 · 请选择 LK8625");}
 void connect(BluetoothDevice d){connectInternal(d,false);}
 void connectInternal(BluetoothDevice d,boolean automatic){if(!supported(d)){l.status("仅支持保锂安 LK8625 设备");return;}close();automaticConnection=automatic;parser=new Lk86Decoder();l.status(automatic?"正在自动连接上次设备…":"连接中");try{g=d.connectGatt(ctx,false,cb,BluetoothDevice.TRANSPORT_LE);if(g==null){fail("无法建立蓝牙连接");return;}h.postDelayed(connectTimeout,20000);}catch(RuntimeException ex){fail("无法建立蓝牙连接");}}
 void close(){stopScan();h.removeCallbacks(connectTimeout);automaticConnection=false;ready=false;busy=false;connected=false;tx=null;reg=3;lkPoll=0;dataFrames=0;if(g!=null){BluetoothGatt old=g;g=null;try{old.disconnect();}catch(RuntimeException ignored){}try{old.close();}catch(RuntimeException ignored){}}l.data(null);}
 void fail(String msg){boolean auto=automaticConnection;close();l.status(auto?"自动连接失败 · "+msg+" · 可手动连接":msg);}
 void rememberValidated(BluetoothDevice d){String address=d.getAddress(),n=name(d);if(!last.remember(address,n))return;prefs.edit().putString("last_bms_address",last.address).putString("last_bms_name",last.name).apply();}
 void tick(){if(!ready||g==null||tx==null)return;if(busy){if(SystemClock.elapsedRealtime()-lastWrite>3000)fail("BLE 写入超时");return;}if(!parser.confirmed){if(!parser.model.isEmpty()){fail("保锂安型号不匹配");return;}reg=2;}else{int[] cycle={0x1e,0x20,0x09,0x1e,0x20,0x1f,0x1e,0x20,0x1e,0x20};reg=cycle[lkPoll++%cycle.length];}byte[] b=Lk86Decoder.request(reg);tx.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);tx.setValue(b);busy=g.writeCharacteristic(tx);lastWrite=SystemClock.elapsedRealtime();if(!busy)l.status("读取请求失败");}
 final BluetoothGattCallback cb=new BluetoothGattCallback(){
  public void onConnectionStateChange(BluetoothGatt x,int status,int state){h.post(()->{if(x!=g)return;if(status!=0||state==BluetoothProfile.STATE_DISCONNECTED){fail("蓝牙已断开 "+status);return;}if(state==BluetoothProfile.STATE_CONNECTED){connected=true;l.status("蓝牙已连接 · 正在发现服务");if(!x.discoverServices())fail("服务发现失败");}});}
  public void onServicesDiscovered(BluetoothGatt x,int status){h.post(()->{if(x!=g)return;if(status!=0){fail("服务发现失败");return;}UUID rxid=UUID.fromString("00010203-0405-0607-0809-0a0b0c0dffe1"),txid=UUID.fromString("00010203-0405-0607-0809-0a0b0c0dffe2");BluetoothGattCharacteristic rx=null;for(BluetoothGattService svc:x.getServices()){for(BluetoothGattCharacteristic c:svc.getCharacteristics()){int p=c.getProperties();if(c.getUuid().equals(rxid)&&(p&BluetoothGattCharacteristic.PROPERTY_NOTIFY)!=0)rx=c;if(c.getUuid().equals(txid)&&(p&BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)!=0)tx=c;}}if(rx==null||tx==null||!x.setCharacteristicNotification(rx,true)){fail("找不到读写特征");return;}BluetoothGattDescriptor d=rx.getDescriptor(id(0x2902));if(d==null){fail("缺少通知描述符");return;}d.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);if(!x.writeDescriptor(d))fail("订阅失败");});}
  public void onDescriptorWrite(BluetoothGatt x,BluetoothGattDescriptor d,int status){h.post(()->{if(x!=g)return;if(status!=0){fail("订阅失败");return;}ready=true;l.status("已订阅，等待有效20串数据");});}
  public void onCharacteristicWrite(BluetoothGatt x,BluetoothGattCharacteristic c,int s){h.post(()->{if(x!=g)return;busy=false;if(s!=0)l.status("读取请求失败 "+s);});}
  public void onCharacteristicChanged(BluetoothGatt x,BluetoothGattCharacteristic c){byte[] b=c.getValue().clone();h.post(()->{if(x!=g)return;Protocol.Battery d=parser.feed(b,System.currentTimeMillis());if(d!=null){if(dataFrames==0){h.removeCallbacks(connectTimeout);rememberValidated(x.getDevice());automaticConnection=false;}dataFrames++;l.data(d);l.status("保锂安 LK8625 · 实时数据");}});}
 };
}
