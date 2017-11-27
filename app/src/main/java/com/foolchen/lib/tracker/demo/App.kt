package com.foolchen.lib.tracker.demo

import android.app.Application
import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.data.Mode

/**
 * 演示用的Application
 * @author chenchong
 * 2017/11/4
 * 下午12:08
 */
class App : Application() {
  override fun onCreate() {
    super.onCreate()
    Tracker.initialize(this)
    Tracker.setMode(Mode.DEBUG_ONLY)
  }
}