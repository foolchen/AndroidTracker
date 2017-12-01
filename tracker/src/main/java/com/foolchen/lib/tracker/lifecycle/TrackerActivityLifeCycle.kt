package com.foolchen.lib.tracker.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.layout.wrap
import com.foolchen.lib.tracker.utils.getTrackName
import com.foolchen.lib.tracker.utils.getTrackProperties
import com.foolchen.lib.tracker.utils.getTrackTitle
import java.lang.ref.WeakReference

/**
 * 该类用于监听项目中所有Activity的生命周期<p/>
 * 需要在[Application]中初始化，以便于能够及时监听所有的[Activity]
 * @author chenchong
 * 2017/11/4
 * 上午11:26
 */
class TrackerActivityLifeCycle : Application.ActivityLifecycleCallbacks {
  private val fragmentLifeCycle = TrackerFragmentLifeCycle()
  private val refs = ArrayList<WeakReference<Activity>>()

  override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    if (activity != null) {
      wrap(activity)
    }
    if (activity is FragmentActivity) {
      activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifeCycle, true)
    }
  }

  override fun onActivityStarted(activity: Activity?) {
    if (refs.isEmpty()) {
      Tracker.onForeground()
    }
    activity?.let {
      refs.add(WeakReference(activity))
    }
  }

  override fun onActivityResumed(activity: Activity?) {
    if (activity != null) {
      if (activity is ITrackerIgnore) {
        if (!activity.isIgnored()) {
          // 内部没有Fragment，直接进行统计
          track(activity)
        }
      } else {
        // Activity内部没有Fragment，则直接进行统计
        track(activity)
      }
    }
  }

  override fun onActivityPaused(activity: Activity?) {
  }

  override fun onActivityStopped(activity: Activity?) {
    activity?.let {
      for (ref in refs) {
        if (ref.get() == activity) {
          refs.remove(ref)
          break
        }
      }
    }
    if (refs.isEmpty()) {
      Tracker.onBackground()
    }
  }

  override fun onActivityDestroyed(activity: Activity?) {
    if (activity is FragmentActivity) {
      activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifeCycle)
    }
  }

  override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
  }

  private fun track(activity: Activity) {
    Tracker.refererAlias = Tracker.screenNameAlias
    Tracker.referer = Tracker.screenName
    Tracker.screenNameAlias = activity.getTrackName()
    Tracker.screenName = activity.javaClass.canonicalName
    Tracker.screenTitle = activity.getTrackTitle()
    Tracker.parentAlias = ""
    Tracker.parent = ""
    Tracker.trackScreen(activity.getTrackProperties())
  }
}