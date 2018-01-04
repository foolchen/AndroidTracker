package com.foolchen.lib.tracker.demo

import android.app.Application
import com.amitshekhar.DebugDB
import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.data.TrackerMode
import com.foolchen.lib.tracker.lifecycle.ITrackerContext

/**
 * 演示用的Application
 * @author chenchong
 * 2017/11/4
 * 下午12:08
 */
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
    Tracker.setService(BuildConfig.SERVICE_HOST, BuildConfig.SERVICE_PATH)
    // 设定上报数据的项目名称
    Tracker.setProjectName(BuildConfig.PROJECT_NAME)
    // 设定上报数据的模式
    Tracker.setMode(TrackerMode.RELEASE)
    // 初始化AndroidTracker
    Tracker.initialize(this)

    DebugDB.getAddressLog()
  }
}