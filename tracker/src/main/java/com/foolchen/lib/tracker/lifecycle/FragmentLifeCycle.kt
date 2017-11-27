package com.foolchen.lib.tracker.lifecycle

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

  private val refs = ArrayList<WeakReference<Fragment>>()

  override fun onFragmentResumed(fm: FragmentManager?, f: Fragment?) {
    if (f is IFragmentVisibleHelper) {
      f.registerIFragmentVisible(this)
    }
    if (f != null) {
      refs.add(WeakReference(f))
    }
    f?.let {
      if (isParentVisible(f) && !isParentFragment(f) && isVisible(f)) {
        // 如果父Fragment可见
        // 并且本身不是父Fragment
        // 并且本身可见，则进行统计
        track(f)
      }
    }
  }

  override fun onFragmentVisibilityChanged(visible: Boolean, f: Fragment?) {
    if (visible) {
      f?.let {
        // 由于内嵌的Fragment不会触发onHiddenChange()和setUserVisibleHint()方法，故此处只能根据其父Fragment来判断
        findVisibleChildren(f).forEach {
          track(it)
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

  /**
   * 根据一个Fragment，从[refs]中查找其所有的子Fragment/子孙Fragment
   * @param parent 要查找的父/祖先Fragment
   * @return 查找到的Fragment，如果不存在Fragment，则返回的列表元素数量为0
   */
  private fun findVisibleChildren(parent: Fragment): List<Fragment> {
    val children = ArrayList<Fragment>()
    refs.filter {
      // 此处用于过滤掉父Fragment不符的Fragment
      val child = it.get()
      child != null && checkParent(child, parent)
    }.filter {
      // 此处用于过滤掉不可见的Fragment
      val child = it.get()
      child != null && !child.isHidden && child.userVisibleHint
    }.forEach {
      val child = it.get()
      child?.let { children.add(child) }
    }

    // 如果没有符合需要的children，则其自身就为符合需要的Fragment
    if (children.isEmpty()) {
      children.add(parent)
    }

    return children
  }

  /**
   * 判断一个Fragment是否可见
   * @param f 要判断的Fragment
   * @return 在Fragment的[Fragment.isHidden]为false，并且[Fragment.getUserVisibleHint]为true时，才返回true；否则false
   */
  private fun isVisible(f: Fragment): Boolean = !f.isHidden && f.userVisibleHint

  /**
   * 判断父Fragment是否可见
   * @return 父Fragment不存在时，直接返回true；父Fragment可见时返回true；其他情况时返回false
   */
  private fun isParentVisible(f: Fragment): Boolean {
    val parent = f.parentFragment
    return if (parent == null) {
      true
    } else {
      !parent.isHidden && parent.userVisibleHint
    }
  }

  /**
   * 判断是否为其他Fragment的父级
   * @param f 需要检查的Fragment
   * @return 在[f]实现了[IFragments]接口，并且[IFragments.hasChildFragments]值为true时，返回true；其他情况下返回false
   */
  private fun isParentFragment(f: Fragment): Boolean = f is IFragments && f.hasChildFragments()

  /**
   * 检查一个[parent]是否是[child]的父Fragment/祖先Fragment
   */
  private fun checkParent(child: Fragment, parent: Fragment): Boolean {
    val parentFragment = child.parentFragment
    return if (parentFragment != null) {
      if (parentFragment == parent) {// 如果是父Fragment，则直接返回true
        true
      } else {// 如果不是父Fragment，并且还存在祖先Fragment，则进入递归
        checkParent(parentFragment, parent)
      }
    } else {// 如果不存在父Fragment，则直接返回false
      false
    }
  }
}