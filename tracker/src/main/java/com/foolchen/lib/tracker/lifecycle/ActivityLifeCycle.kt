package com.foolchen.lib.tracker.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
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

  override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
  }

  override fun onActivityResumed(activity: Activity?) {
    if (activity is IFragments) {
      val refs = activity.getFragments()
      if (refs.isEmpty()) {
        // 该Activity中不存在Fragment，则该Activity就是需要统计的页面
        Tracker.refer = Tracker.name
        Tracker.name = activity.getTrackName()
        Tracker.trackScreen()
      }
    } else if (activity != null) {
      // 该Activity中不存在Fragment，则该Activity就是需要统计的页面
      Tracker.refer = Tracker.name
      Tracker.name = activity.getTrackName()
      Tracker.trackScreen()
    }
  }

  override fun onActivityStarted(activity: Activity?) {
  }

  override fun onActivityPaused(activity: Activity?) {
  }

  override fun onActivityStopped(activity: Activity?) {
  }

  override fun onActivityDestroyed(activity: Activity?) {
  }

  override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
  }
}