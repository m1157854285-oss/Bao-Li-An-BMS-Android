# 保锂安 Android V6.6.6

本版基于 V0.3.8，仅统一发布版本号：versionName 6.6.6、versionCode 12。应用包名 io.bmsride.monitor 不变。保留 V0.3.8 的应用图标、联系作者、动态电芯串数与其他功能。

## GitHub 更新
将 BMS_666_update.zip 解压，上传内部文件及目录到现有仓库根目录；覆盖同路径文件。将 .github/workflows/build.yml 内容替换为 build_666.yml（网页上传可能跳过隐藏目录）。在 Actions 中运行 Build Android APK v6.6.6，下载 BMS-Ride20-Android-v6.6.6 artifact 中的 BMS_Ride20_Android_v6.6.6.apk。签名仍为原构建机制，签名不一致时不能直接覆盖安装。

本次仅核对版本配置、脚本与压缩包；未执行 Android SDK 编译或真机测试。其他历史功能及限制见下方 V0.3.7 记录，V0.3.8 的界面调整以实际源码为准。

## 历史记录（V0.3.7）
# BMS 随行20 Android V0.3.7
纯手机版，保锂安 LK8625 / LR365V10_N031x / 20串磷酸铁锂，包名 io.bmsride.monitor。

## V0.3.7
- 总览：电流、总电压两位小数；电芯及压差三位小数。未读取的值显示 —，保留原有卡片及颜色。
- GPS：撤掉 V0.3.6 的连续3个运动样本及高起步门槛；使用手机 GPS_PROVIDER 的速度，定位精度上限35米、样本有效期5秒，速度小于0.25m/s显示零。速度、最高速度显示1位小数；有效速度积分里程，间隔超过5秒不补算；保留60分钟曲线及真实BLE能耗配对。低速灵敏度提高后可能再次出现较大的静止漂移，需真机调整。
- BLE：重新扫描保留当前GATT设备、名称，按MAC去重并置顶。已连接状态取自实际GATT连接；数据有效性单独显示。未收到新广播时RSSI显示空值。仍然扫描全部可见BLE设备，最多显示4行、内部滚动，LK8625优先。
- 清理旧JK/JBD请求、解码分支及测试、旧手表消息版本/序号和未使用的汇总温度字段、旧翻页规则。保留LK8625只读协议、20串电芯、3路温度、MOS和保护信息、手动容量设置。未解码的MOS温度仍显示空值。
- 保留滑动跟手动画，GPS曲线拖动和设备列表滚动。

## GitHub 更新
将完整覆盖包解压，在仓库根目录上传 README.md、android、tests、build-packages.sh；覆盖同路径文件。不需要删除仓库。另将 .github/workflows/build.yml 的内容替换为 build-v0.3.7.yml（网页上传可能跳过隐藏目录）。Actions → Build Android APK v0.3.7 → Run workflow → main。下载 artifact 中的 APK。构建使用 JDK17 / Gradle8.9 / Android SDK35。临时签名可能导致覆盖安装失败，备份设置后再决定是否卸载旧版。

## 测试与限制
核心纯Java测试及Java语法解析见 tests/result.txt；未执行完整Android SDK编译和真机测试。真实数据需连接正确BMS并授权附近设备和精确定位，在户外点击开始GPS、重置里程后测试步行及静止。估算续航不得作为安全保证。历史保存在运行服务内存，重启后不永久保存。
