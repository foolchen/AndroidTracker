package com.foolchen.lib.tracker.demo.fragments

import android.support.v4.app.Fragment
import com.foolchen.lib.tracker.lifecycle.ITrackerFragmentVisible
import com.foolchen.lib.tracker.lifecycle.IFragmentVisibleHelper
import com.foolchen.lib.tracker.lifecycle.ITrackerIgnore
import com.foolchen.lib.tracker.lifecycle.ITrackerHelper

/**
 * Fragment的基类
 * @author chenchong
 * 2017/11/23
 * 下午3:28
 */
open class BaseFragment : Fragment(), ITrackerHelper, ITrackerIgnore, IFragmentVisibleHelper {
  private var mIFragmentVisible: ITrackerFragmentVisible? = null

  override fun setUserVisibleHint(isVisibleToUser: Boolean) {
    super.setUserVisibleHint(isVisibleToUser)
    mIFragmentVisible?.onFragmentVisibilityChanged(isVisibleToUser, this)
  }

  override fun onHiddenChanged(hidden: Boolean) {
    super.onHiddenChanged(hidden)
    mIFragmentVisible?.onFragmentVisibilityChanged(!hidden, this)
  }

  ///////////////////////////////////////////////////////////////////////////
  // 该类实现ITrackerHelper接口，此处两个方法全部返回null
  // 则页面名称（别名）会直接取使用canonicalName来当做标题
  // 并且不会有附加的属性
  ///////////////////////////////////////////////////////////////////////////
  override fun getTrackName(): String? = null

  override fun getTrackProperties(): Map<String, Any?>? = null

  ///////////////////////////////////////////////////////////////////////////
  // ITrackerIgnore接口用于确定当前Fragment中是否包含子Fragment
  // 如果返回值为true，则表明当前Fragment中有包含子Fragment，则此时不会对当前Fragment进行统计
  // 如果返回值为false，则表明当前Fragment中不包含子Fragment，则此时会对当前Fragment进行统计
  // 此处默认不包含子Fragment，如有需要应该在子类中覆写该方法并修改返回值
  ///////////////////////////////////////////////////////////////////////////
  override fun isIgnored(): Boolean = false

  ///////////////////////////////////////////////////////////////////////////
  // IFragmentVisibleHelper接口需要被Fragment实现，该接口用于想Fragment中传递一个IFragmentVisible接口
  // 而IFragmentVisible需要在当前Fragment的setUserVisibleHint和onHiddenChanged()方法被调用时同步调用
  // 以便于正确处理内部的子Fragment
  ///////////////////////////////////////////////////////////////////////////
  override fun registerIFragmentVisible(it: ITrackerFragmentVisible) {
    mIFragmentVisible = it
  }

  override fun unregisterIFragmentVisible(it: ITrackerFragmentVisible) {
    mIFragmentVisible = null
  }

  override fun getIFragmentVisible(): ITrackerFragmentVisible? = mIFragmentVisible
}