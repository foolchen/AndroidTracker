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
    Tracker.addProperty("附加的属性1", "附加的属性1")
    Tracker.addProperty("附加的属性2", "附加的属性2")
    Tracker.setService(BuildConfig.SERVICE_HOST, BuildConfig.SERVICE_PATH)
    Tracker.setProjectName(BuildConfig.PROJECT_NAME)
    Tracker.setMode(TrackerMode.DEBUG_ONLY)
    Tracker.initialize(this)

    DebugDB.getAddressLog()
  }
}