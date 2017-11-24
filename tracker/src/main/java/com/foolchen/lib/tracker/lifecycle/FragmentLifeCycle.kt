package com.foolchen.lib.tracker.lifecycle

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.utils.getTrackName
import com.foolchen.lib.tracker.utils.getTrackProperties
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
  private val refs = ArrayList<WeakReference<Fragment>>()

  override fun onFragmentAttached(fm: FragmentManager?, f: Fragment?, context: Context?) {

  }

  override fun onFragmentStarted(fm: FragmentManager?, f: Fragment?) {
    f?.childFragmentManager?.registerFragmentLifecycleCallbacks(fragmentLifeCycle, false)
  }

  override fun onFragmentResumed(fm: FragmentManager?, f: Fragment?) {
    if (f is IFragmentVisibleHelper) {
      f.registerIFragmentVisible(this)
    }
    if (f != null) {
      refs.add(WeakReference(f))
    }
    //TODO 在执行了onPause之后，内嵌的Fragment没有监听到onResume()，可能是由于注册callback的时机问题
    if (f != null) {
      if (isVisible(f)) {
        track(f)
      }
    }
  }

  private fun isVisible(f: Fragment): Boolean {
    return if (f is IFragments) {
      isParentVisible(f) && !f.hasChildFragments() && !f.isHidden && f.userVisibleHint
    } else {
      isParentVisible(f) && !f.isHidden && f.userVisibleHint
    }
  }

  private fun isParentVisible(f: Fragment): Boolean {
    val parent = f.parentFragment
    return if (parent == null) {
      true
    } else {
      !parent.isHidden && parent.userVisibleHint
    }
  }

  override fun onFragmentVisibilityChanged(visible: Boolean, f: Fragment?) {
    if (visible) {
      if (f != null) {
        if (f is IFragments) {
          if (!f.hasChildFragments() && !f.isHidden && f.userVisibleHint) {
            // 在当前Fragment内部没有嵌套其他Fragment进行统计
            track(f)
          } else if (f.hasChildFragments()) {
            // 如果内部嵌套了其他Fragment，则内部的Fragment的setUserVisibleHint和onHidden方法不会被调用
            // 故此处需要对内部的Fragment进行处理
            // 首先，尝试找出内嵌的子Fragment中对用户可见的Fragment
            val visibleFragments = findVisibleFragmentsFromRefs()
            visibleFragments?.
                //asSequence()?.
                forEach {
                  if (it is IFragmentVisibleHelper && isVisible(it)) {
                    //it.onFragmentVisibilityChanged(visible, it)
                    it.getIFragmentVisible()?.onFragmentVisibilityChanged(visible, it)
                  }
                }
          }
        } else if (isParentVisible(f) && !f.isHidden && f.userVisibleHint) {
          // Fragment没有被隐藏，且在当前显示的UI中，则进行统计
          track(f)
        }
      }
    }
  }


  override fun onFragmentPaused(fm: FragmentManager?, f: Fragment?) {
    // 在Fragment不可见时对应的移除该Fragment
    for (ref in refs) {
      if (ref.get() == f) {
        refs.remove(ref)
        break
      }
    }
    if (f is IFragmentVisibleHelper) {
      f.unregisterIFragmentVisible(this)
    }
  }

  override fun onFragmentStopped(fm: FragmentManager?, f: Fragment?) {
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

  private fun findVisibleFragmentsFromRefs(): List<Fragment>? {
    var fragments: ArrayList<Fragment>? = null
    for (ref in fragmentLifeCycle.refs) {
      val f = ref.get()
      if (f != null && !f.isHidden && f.userVisibleHint) {
        if (fragments == null) {
          fragments = ArrayList()
        }
        fragments.add(f)
      }
    }
    return fragments
  }
}