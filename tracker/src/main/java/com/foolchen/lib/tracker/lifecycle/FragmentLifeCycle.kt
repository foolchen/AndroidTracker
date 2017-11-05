package com.foolchen.lib.tracker.lifecycle

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.utils.getTrackName
import com.foolchen.lib.tracker.utils.getTrackProperties

/**
 * 该类用于监听所有Fragment的生命周期<p/>
 *
 * @author chenchong
 * 2017/11/4
 * 上午11:27
 */
class FragmentLifeCycle : FragmentManager.FragmentLifecycleCallbacks(), IFragmentVisible {

  private val fragmentLifeCycle: FragmentLifeCycle by lazy { FragmentLifeCycle() }
  //internal val refs = ArrayList<WeakReference<Fragment>>()

  override fun onFragmentAttached(fm: FragmentManager?, f: Fragment?, context: Context?) {

  }

  override fun onFragmentStarted(fm: FragmentManager?, f: Fragment?) {
    f?.childFragmentManager?.registerFragmentLifecycleCallbacks(fragmentLifeCycle, false)
    if (f is IFragmentVisibleHlper) {
      f.registerIFragmentVisible(this)
    }
  }

  override fun onFragmentResumed(fm: FragmentManager?, f: Fragment?) {
    /*if (f != null) {
      refs.add(WeakReference(f))
    }*/
    onFragmentVisible(f)
  }

  override fun onFragmentVisible(f: Fragment?) {
    if (f != null) {
      /*if (f is IFragments) {
        if (!f.hasChildFragments()) {
          // 该Fragment中不存在其他Fragment
          // 则直接对该Fragment进行统计
          track(f)
          return
        }

        val refs = fragmentLifeCycle.refs
        if (refs.isEmpty()) {
          // 当前Fragment中没有子Fragment
          if (!f.isHidden && f.userVisibleHint) {
            // 并且当前Fragment可见，则统计该Fragment
            track(f)
          }
        }
      } else if (!f.isHidden && f.userVisibleHint) {
        // 直接统计
        track(f)
      }*/
      if (f is IFragments) {
        if (!f.hasChildFragments() && !f.isHidden && f.userVisibleHint) {
          // 在当前Fragment内部没有嵌套其他Fragment进行统计
          track(f)
        }
      } else if (!f.isHidden && f.userVisibleHint) {
        // Fragment没有被隐藏，且在当前显示的UI中，则进行统计
        track(f)
      }
    }
  }

  override fun onFragmentHide(f: Fragment?) {

  }

  override fun onFragmentPaused(fm: FragmentManager?, f: Fragment?) {
    onFragmentHide(f)
    /*for (ref in refs) {
      if (f == ref.get()) {
        refs.remove(ref)
        break
      }
    }*/
  }

  override fun onFragmentStopped(fm: FragmentManager?, f: Fragment?) {
    if (f is IFragmentVisibleHlper) {
      f.unregisterIFragmentVisible(this)
    }
    f?.childFragmentManager?.unregisterFragmentLifecycleCallbacks(fragmentLifeCycle)
  }

  override fun onFragmentDetached(fm: FragmentManager?, f: Fragment?) {

  }

  override fun onFragmentDestroyed(fm: FragmentManager?, f: Fragment?) {
  }

  private fun track(f: Fragment) {
    val trackName = f.getTrackName()
    Tracker.refer = Tracker.name
    Tracker.referClazz = Tracker.clazz
    Tracker.name = trackName
    Tracker.clazz = f.javaClass.canonicalName
    var parent = ""
    var parentClazz = ""
    val parentFragment = f.parentFragment
    if (parentFragment != null) {
      parent = parentFragment.getTrackName()
      parentClazz = parentFragment.javaClass.canonicalName
    } else {
      val activity = f.activity
      if (activity != null) {
        parent = activity.getTrackName()
        parentClazz = activity.javaClass.canonicalName
      }
    }
    Tracker.parent = parent
    Tracker.parentClazz = parentClazz
    Tracker.trackScreen(f.getTrackProperties())
  }
}