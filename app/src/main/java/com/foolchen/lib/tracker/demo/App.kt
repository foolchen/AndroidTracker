package com.foolchen.lib.tracker.demo

import android.app.Application
import com.foolchen.lib.tracker.TrackContext
import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.data.Mode

/**
 * 演示用的Application
 * @author chenchong
 * 2017/11/4
 * 下午12:08
 */
class App : Application(), TrackContext {
  override fun onCreate() {
    super.onCreate()
    Tracker.initialize(this)
    Tracker.addProperty("附加的属性1", "附加的属性1")
    Tracker.addProperty("附加的属性2", "附加的属性2")
    Tracker.setMode(Mode.DEBUG_ONLY)
  }
}