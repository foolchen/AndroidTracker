package com.foolchen.lib.tracker.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.utils.getTrackName

/**
 * 该类用于监听项目中所有Activity的生命周期<p/>
 * 需要在[Application]中初始化，以便于能够及时监听所有的[Activity]
 * @author chenchong
 * 2017/11/4
 * 上午11:26
 */
class ActivityLifeCycle : Application.ActivityLifecycleCallbacks {
  private val fragmentLifeCycle = FragmentLifeCycle()

  override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    Log.d("ActivityLifeCycle", activity.toString())
    if (activity is FragmentActivity) {
      activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifeCycle, false)
    }
  }

  override fun onActivityResumed(activity: Activity?) {
    if (activity != null) {
      if (activity is IFragments) {
        if (!activity.hasChildFragments()) {
          // 该Activity被强制规定没有Fragment，直接统计
          track(activity)
          return
        }

        val refs = fragmentLifeCycle.refs
        if (refs.isEmpty()) {
          // 该Activity中不存在Fragment，则该Activity就是需要统计的页面
          track(activity)
        }
      } else {
        // 该Activity中不存在Fragment，则该Activity就是需要统计的页面
        track(activity)
      }
    }
  }

  override fun onActivityStarted(activity: Activity?) {
  }

  override fun onActivityPaused(activity: Activity?) {
  }

  override fun onActivityStopped(activity: Activity?) {
  }

  override fun onActivityDestroyed(activity: Activity?) {
    if (activity is FragmentActivity) {
      activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifeCycle)
    }
  }

  override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
  }

  private fun track(activity: Activity) {
    Tracker.refer = Tracker.name
    Tracker.name = activity.getTrackName()
    Tracker.parent = ""
    Tracker.trackScreen()
  }
}