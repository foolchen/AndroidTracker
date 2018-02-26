package com.foolchen.lib.tracker.demo.fragments

import android.content.Context
import android.support.v4.app.Fragment
import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.lifecycle.ITrackerHelper
import com.foolchen.lib.tracker.lifecycle.ITrackerIgnore

/**
 * Fragment的基类
 * @author chenchong
 * 2017/11/23
 * 下午3:28
 */
open class BaseFragment : Fragment(), ITrackerHelper, ITrackerIgnore {

  ///////////////////////////////////////////////////////////////////////////
  // Tracker.setUserVisibleHint()和Tracker.onHiddenChanged()方法用于同步Fragment
  // 的可见性，解决在Fragment显隐/与ViewPager结合使用时无法触发生命周期的问题
  ///////////////////////////////////////////////////////////////////////////
  override fun setUserVisibleHint(isVisibleToUser: Boolean) {
    super.setUserVisibleHint(isVisibleToUser)
    Tracker.setUserVisibleHint(this, isVisibleToUser)
  }

  override fun onHiddenChanged(hidden: Boolean) {
    super.onHiddenChanged(hidden)
    Tracker.onHiddenChanged(this, hidden)
  }

  ///////////////////////////////////////////////////////////////////////////
  // 该类实现ITrackerHelper接口，此处两个方法全部返回null
  // 则页面名称（别名）会直接取使用canonicalName来当做标题
  // 并且不会有附加的属性
  ///////////////////////////////////////////////////////////////////////////
  override fun getTrackName(context: Context): String? = null

  override fun getTrackProperties(context: Context): Map<String, Any?>? = null

  ///////////////////////////////////////////////////////////////////////////
  // ITrackerIgnore接口用于确定当前Fragment中是否包含子Fragment
  // 如果返回值为true，则表明当前Fragment中有包含子Fragment，则此时不会对当前Fragment进行统计
  // 如果返回值为false，则表明当前Fragment中不包含子Fragment，则此时会对当前Fragment进行统计
  // 此处默认不包含子Fragment，如有需要应该在子类中覆写该方法并修改返回值
  ///////////////////////////////////////////////////////////////////////////
  override fun isIgnored(): Boolean = false
}