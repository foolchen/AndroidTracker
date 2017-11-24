package com.foolchen.lib.tracker.demo.fragments

import android.support.v4.app.Fragment
import com.foolchen.lib.tracker.lifecycle.IFragmentVisible
import com.foolchen.lib.tracker.lifecycle.IFragmentVisibleHelper
import com.foolchen.lib.tracker.lifecycle.IFragments
import com.foolchen.lib.tracker.lifecycle.ITrack

/**
 * Fragment的基类
 * @author chenchong
 * 2017/11/23
 * 下午3:28
 */
open class BaseFragment : Fragment(), ITrack, IFragments, IFragmentVisibleHelper {
  private var mIFragmentVisible: IFragmentVisible? = null

  override fun setUserVisibleHint(isVisibleToUser: Boolean) {
    super.setUserVisibleHint(isVisibleToUser)
    mIFragmentVisible?.onFragmentVisibilityChanged(isVisibleToUser, this)
  }

  override fun onHiddenChanged(hidden: Boolean) {
    super.onHiddenChanged(hidden)
    mIFragmentVisible?.onFragmentVisibilityChanged(!hidden, this)
  }

  override fun onResume() {
    super.onResume()
    mIFragmentVisible?.onFragmentVisibilityChanged(true, this)
  }

  ///////////////////////////////////////////////////////////////////////////
  // 该类实现ITrack接口，此处两个方法全部返回null
  // 则页面名称（别名）会直接取使用canonicalName来当做标题
  // 并且不会有附加的属性
  ///////////////////////////////////////////////////////////////////////////
  override fun getTrackName(): String? = null

  override fun getTrackProperties(): Map<String, Unit>? = null

  ///////////////////////////////////////////////////////////////////////////
  // IFragments接口用于确定当前Fragment中是否包含子Fragment
  // 如果返回值为true，则表明当前Fragment中有包含子Fragment，则此时不会对当前Fragment进行统计
  // 如果返回值为false，则表明当前Fragment中不包含子Fragment，则此时会对当前Fragment进行统计
  // 此处默认不包含子Fragment，如有需要应该在子类中覆写该方法并修改返回值
  ///////////////////////////////////////////////////////////////////////////
  override fun hasChildFragments(): Boolean = false

  ///////////////////////////////////////////////////////////////////////////
  // IFragmentVisibleHelper接口需要被Fragment实现，该接口用于想Fragment中传递一个IFragmentVisible接口
  // 而IFragmentVisible需要在当前Fragment的setUserVisibleHint和onHiddenChanged()方法被调用时同步调用
  // 以便于正确处理内部的子Fragment
  ///////////////////////////////////////////////////////////////////////////
  override fun registerIFragmentVisible(it: IFragmentVisible) {
    mIFragmentVisible = it
  }

  override fun unregisterIFragmentVisible(it: IFragmentVisible) {
    mIFragmentVisible = null
  }

  override fun getIFragmentVisible(): IFragmentVisible? = mIFragmentVisible
}