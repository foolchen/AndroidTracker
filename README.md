# AndroidTracker

`AndroidTracker`是一个Android端的无埋点统计的实现方法。其对`Activity`、`Fragment`的生命周期进行监听，实现了页面浏览以及点击事件的采集。

针对点击事件的处理，目前兼容`ActionBar`、`ToolBar`的点击，以及[ButterKnife](https://github.com/JakeWharton/butterknife)的点击注解。

## 初始化

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
    implementation 'com.github.foolchen:AndroidTracker:0.3.3'
}
```

该库中引用了[okhttp3](https://github.com/square/okhttp)以及[retrofit2](https://github.com/square/retrofit)，如果需要独立引用其他版本的`okhttp`和`retrofit`，则可以引用如下：

```groovy
dependencies {
    implementation ("com.github.foolchen:AndroidTracker:0.3.3"){
        exclude module: 'okhttp'
        exclude module: 'retrofit'
    }
}
```

并在项目的`Application`中初始化如下：

```kotlin
class App : Application(), ITrackerContext {
  override fun onCreate() {
    super.onCreate()
    // 设定一些通用的属性，这些属性在每次统计事件中都会附带
    // 注意：如果此处的属性名与内置属性的名称相同，则内置属性会被覆盖
    Tracker.addProperty("附加的属性1", "附加的属性1")
    Tracker.addProperty("附加的属性2", "附加的属性2")
    // 设定上报数据的主机和接口
    // 注意：该方法一定要在Tracker.initialize()方法前调用
    // 否则会由于上报地址未初始化，在触发启动事件时导致崩溃
    Tracker.setService("https://www.demo.com", "report.php")
    // 设定上报数据的项目名称
    Tracker.setProjectName("android_tracker")
    // 设定上报数据的模式
    Tracker.setMode(TrackerMode.RELEASE)
    // 初始化AndroidTracker
    Tracker.initialize(this)
  }
}
```

## Activity中的集成方式

在`Activity`中集成时，如果项目仅存在`Activity`，不需要对`Fragment`进行特殊处理，则仅需要实现`ITrackerHelper`接口，用于获取页面的名称和页面附加属性。
如果项目中有`Fragment`，需要在特定情况下忽略`Activity`的统计，则还应该实现`ITrackerIgnore`接口，手动处理`isIgnore`方法的返回值。示例如下：

```kotlin
open class BaseActivity : AppCompatActivity(), ITrackerHelper, ITrackerIgnore {
  ///////////////////////////////////////////////////////////////////////////
  // 该类实现ITrackerHelper接口，此处两个方法全部返回null
  // 则页面名称（别名）会直接取使用canonicalName来当做标题
  // 并且不会有附加的属性
  ///////////////////////////////////////////////////////////////////////////
  override fun getTrackName(): String? = null

  override fun getTrackProperties(): Map<String, Any?>? = null

  ///////////////////////////////////////////////////////////////////////////
  // ITrackerIgnore接口用于确定当前Activity中是否包含Fragment
  // 如果返回值为true，则表明当前Activity中有包含Fragment，则此时不会对Activity进行统计
  // 如果返回值为false，则表明当前Activity中不包含Fragment，则此时会对Activity进行统计
  // 此处默认不包含Fragment，如有需要应该在子类中覆写该方法并修改返回值
  ///////////////////////////////////////////////////////////////////////////
  override fun isIgnored(): Boolean = false
}
```

## Fragment中的集成方式

与`Activity`相同，如果`Fragment`中不再有子`Fragment`，则仅需要实现`ITrackerHelper`、`IFragmentVisibleHelper`接口。
如果有子`Fragment`，或者需要手动忽略部分`Fragment`的统计，则需要实现`ITrackerIgnore`接口。示例如下：

```kotlin
open class BaseFragment : Fragment(), ITrackerHelper, ITrackerIgnore, IFragmentVisibleHelper {
  private var mIFragmentVisible: ITrackerFragmentVisible? = null

  override fun setUserVisibleHint(isVisibleToUser: Boolean) {
    super.setUserVisibleHint(isVisibleToUser)
    mIFragmentVisible?.onFragmentVisibilityChanged(isVisibleToUser, this)
  }

  override fun onHiddenChanged(hidden: Boolean) {
    super.onHiddenChanged(hidden)
    mIFragmentVisible?.onFragmentVisibilityChanged(!hidden, this)
  }

  ///////////////////////////////////////////////////////////////////////////
  // 该类实现ITrackerHelper接口，此处两个方法全部返回null
  // 则页面名称（别名）会直接取使用canonicalName来当做标题
  // 并且不会有附加的属性
  ///////////////////////////////////////////////////////////////////////////
  override fun getTrackName(): String? = null

  override fun getTrackProperties(): Map<String, Any?>? = null

  ///////////////////////////////////////////////////////////////////////////
  // ITrackerIgnore接口用于确定当前Fragment中是否包含子Fragment
  // 如果返回值为true，则表明当前Fragment中有包含子Fragment，则此时不会对当前Fragment进行统计
  // 如果返回值为false，则表明当前Fragment中不包含子Fragment，则此时会对当前Fragment进行统计
  // 此处默认不包含子Fragment，如有需要应该在子类中覆写该方法并修改返回值
  ///////////////////////////////////////////////////////////////////////////
  override fun isIgnored(): Boolean = false

  ///////////////////////////////////////////////////////////////////////////
  // IFragmentVisibleHelper接口需要被Fragment实现，该接口用于想Fragment中传递一个
  // IFragmentVisible接口而IFragmentVisible需要在当前Fragment的setUserVisibleHint
  // 和onHiddenChanged()方法被调用时同步调用以便于正确处理内部的子Fragment
  ///////////////////////////////////////////////////////////////////////////
  override fun registerIFragmentVisible(it: ITrackerFragmentVisible) {
    mIFragmentVisible = it
  }

  override fun unregisterIFragmentVisible(it: ITrackerFragmentVisible) {
    mIFragmentVisible = null
  }

  override fun getIFragmentVisible(): ITrackerFragmentVisible? = mIFragmentVisible
}
```

## 对点击事件增加属性

在某些场景下，仅对`View`进行默认属性的统计可能无法满足需求，故可以针对点击事件增加自定义属性。示例如下（ButterKnife）：

```java
@OnClick(R.id.tv_clickable) public void click(View view) {
      Toast.makeText(view.getContext(), ((TextView) view).getText().toString(), Toast.LENGTH_SHORT)
          .show();
      // 此处针对点击事件增加属性
      Map<String, Object> map = new HashMap<>();
      map.put("附加的View属性", view.toString());
      Tracker.INSTANCE.trackView(view, map);
    }
```

## 登入/登出处理

每个用户会使用`distinct_id`来标识，用户未登录时，该值由设备`Android Id`、设备制造商、设备型号等字段计算得到唯一值。
而在用户登录后，则需要使用确切的用户id来替代该值。示例如下：
kotlin:

```kotlin
Tracker.login("此处设置用户id")
```

java:

```java
Tracker.INSTANCE.login("此处设置用户id");
```

在用户退出登录时，需要主动调用登出方法。此时，`distinct_id`的值会替换为SDK生成的值。示例如下：

kotlin：

```kotlin
Tracker.logout()
```

java：

```java
Tracker.INSTANCE.logout();
```

## 设置渠道号

渠道号在统计事件中的属性为`channel`。设置如下：

kotlin：

```kotlin
Tracker.setChannelId("此处设置渠道号")
```

java：

```java
Tracker.INSTANCE.setChannelId("此处设置渠道号");
```


## 针对所有统计附加属性

如果想要针对所有的统计事件增加一些固定的属性，则应该在初始化时设置如下：

```kotlin
Tracker.addProperty("附加的属性1", "附加的属性1")
Tracker.addProperty("附加的属性2", "附加的属性2")
```

## 自定义事件

该库提供了追踪自定义事件的方法，并且可以自定义属性。调用示例如下：

kotlin:

```kotlin
Tracker.trackEvent("MainActivity的自定义追踪事件", null)
```

java:

```java
Tracker.INSTANCE.trackEvent("MainActivity的自定义追踪事件", null)
```

## 上报模式

**DEBUG_ONLY**：仅在`Logcat`中打印日志，不上传数据。建议仅在调试阶段使用该模式。

**DEBUG_TRACK**：在`Logcat`中打印日志，并且**即时**上传数据。建议在开发及测试阶段使用该模式。

**RELEASE**：不在`Logcat`中打印日志，每10条记录尝试上传数据。在发行版本中使用该模式。

**DISABLE**：禁用上报。设置该模式时，所有的上报相关方法都失效。如果想要重新生效，需要重新初始化。

## 调试

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

## 忽略处理

### 对Activity/Fragment进行忽略

如果需要对`Activity`/`Fragment`进行忽略，则需要实现`ITrackerIgnore`接口，并手动将`isIgnore()`方法的返回值置为`true`。
如果要解除对`Activity`/`Fragment`的忽略，则根据情况将返回值置为`false`即可。

### 对点击事件进行忽略

如果要对点击事件进行忽略，则需要在点击事件触发时手动调用`Tracker.ignoreView(view)`方法即可。该方式针对普通的点击监听设置方式以及`ButterKnife`的注解方式都生效。

## 注意事项
由于对点击事件的统计使用到了反射，故集成了该库之后会对点击时的效率有所影响。

## License

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

