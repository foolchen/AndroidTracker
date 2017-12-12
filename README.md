# AndroidTracker

`AndroidTracker`是一个Android端的无埋点统计的实现方法。其对`Activity`、`Fragment`的生命周期进行监听，实现了页面浏览以及点击事件的采集。

### 初始化

在工程根目录的`build.gradle`文件的最后添加：

```groovy
allprojects {
    repositories {
    	// ...
    	maven { url 'https://jitpack.io' }
    }
}
```
    
并在项目中增加如下依赖：

```groovy
dependencies {
    compile 'com.github.foolchen:AndroidTracker:0.3.0'
}
```

并在项目的`Application`中初始化如下：

```kotlin
class App : Application(), ITrackerContext {
  override fun onCreate() {
    super.onCreate()
    // 初始化AndroidTracker
    Tracker.initialize(this)
    // 设定一些通用的属性，这些属性在每次统计事件中都会附带
    // 注意：如果此处的属性名与内置属性的名称相同，则内置属性会被覆盖
    Tracker.addProperty("附加的属性1", "附加的属性1")
    Tracker.addProperty("附加的属性2", "附加的属性2")
    // 设定上报数据的主机和接口
    Tracker.setService("https://www.demo.com", "report.php")
    // 设定上报数据的项目名称
    Tracker.setProjectName("android_tracker")
    // 设定上报数据的模式
    Tracker.setMode(TrackerMode.RELEASE)
  }
}
```

### 上报模式

**DEBUG_ONLY**：仅在`Logcat`中打印日志，不上传数据。建议仅在调试阶段使用该模式。

**DEBUG_TRACK**：在`Logcat`中打印日志，并且**即时**上传数据。建议在开发及测试阶段使用该模式。

**RELEASE**：不在`Logcat`中打印日志，每10条记录尝试上传数据。在发行版本中使用该模式。

### 调试

在`DEBUG_ONLY`、`DEBUG_TRACKE`模式下，可以在`Logcat`中输出日志，格式如下：

```json
{
  "time": 1513062404277,
  "event": "AppStart",
  "lib": {
    "lib": "Android",
    "app_version": "1.0",
    "lib_version": "0.3.0"
  },
  "distinct_id": "308597799c71b44b95d1f6845",
  "properties": {
    "parent": "",
    "screen_width": 2560,
    "wifi": true,
    "lib": "Android",
    "app_version": "1.0",
    "os": "Android",
    "device_id": "c71b44b95d1f6845",
    "resume_from_background": false,
    "os_version": "8.0.0",
    "imeicode": "359906070806943",
    "parent_class": "",
    "lib_version": "0.3.0",
    "title": "",
    "manufacturer": "google",
    "referrer": "",
    "carrier": "中国联通",
    "附加的属性1": "附加的属性1",
    "screen_height": 1440,
    "附加的属性2": "附加的属性2",
    "screen_name": "",
    "model": "Pixel XL",
    "referrer_class": "",
    "network_type": "WIFI",
    "screen_class": ""
  }
}
```


### License

```
Copyright 2017 Chen Chong

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

