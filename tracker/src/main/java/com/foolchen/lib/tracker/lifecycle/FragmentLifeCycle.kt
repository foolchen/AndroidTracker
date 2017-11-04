package com.foolchen.lib.tracker.lifecycle

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.utils.getTrackName
import java.lang.ref.WeakReference

/**
 * 该类用于监听所有Fragment的生命周期<p/>
 *
 * @author chenchong
 * 2017/11/4
 * 上午11:27
 */
class FragmentLifeCycle : FragmentManager.FragmentLifecycleCallbacks(), IFragmentVisible {

  private val fragmentLifeCycle: FragmentLifeCycle by lazy { FragmentLifeCycle() }
  internal val refs = ArrayList<WeakReference<Fragment>>()

  override fun onFragmentCreated(fm: FragmentManager?, f: Fragment?, savedInstanceState: Bundle?) {

    f?.childFragmentManager?.registerFragmentLifecycleCallbacks(fragmentLifeCycle, false)
    if (f is IFragmentVisibleHlper) {
      f.registerIFragmentVisible(this)
    }
  }

  override fun onFragmentAttached(fm: FragmentManager?, f: Fragment?, context: Context?) {
    if (f != null) {
      refs.add(WeakReference(f))
    }
  }

  override fun onFragmentResumed(fm: FragmentManager?, f: Fragment?) {
    onFragmentVisible(f)
  }

  override fun onFragmentVisible(f: Fragment?) {
    if (f != null) {
      if (f is IFragments) {
        if (!f.hasChildFragments()) {
          // 该Fragment中不存在其他Fragment
          // 则直接对该Fragment进行统计
          track(f)
          return
        }

        val refs = fragmentLifeCycle.refs
        if (refs.isEmpty()) {
          // 当前Fragment中没有子Fragment
          if (f.isVisible && f.userVisibleHint) {
            // 并且当前Fragment可见，则统计该Fragment
            track(f)
          }
        }
      }
    }
  }

  override fun onFragmentHide(f: Fragment?) {

  }

  override fun onFragmentPaused(fm: FragmentManager?, f: Fragment?) {
    onFragmentHide(f)
  }


  override fun onFragmentDetached(fm: FragmentManager?, f: Fragment?) {
    for (ref in refs) {
      if (f == ref.get()) {
        refs.remove(ref)
        break
      }
    }
  }

  override fun onFragmentDestroyed(fm: FragmentManager?, f: Fragment?) {
    if (f is IFragmentVisibleHlper) {
      f.unregisterIFragmentVisible(this)
    }
    f?.childFragmentManager?.unregisterFragmentLifecycleCallbacks(fragmentLifeCycle)
  }

  private fun track(f: Fragment) {
    val trackName = f.getTrackName()
    if (Tracker.name != trackName) {
      Tracker.refer = Tracker.name
      Tracker.name = trackName
      var parent = ""
      var parentFragment = f.parentFragment
      if (parentFragment != null) {
        parent = parentFragment.javaClass.canonicalName
      } else {
        val activity = f.activity
        if (activity != null) {
          parent = activity.javaClass.canonicalName
        }
      }
      Tracker.parent = parent
      Tracker.trackScreen()
    }
  }
}