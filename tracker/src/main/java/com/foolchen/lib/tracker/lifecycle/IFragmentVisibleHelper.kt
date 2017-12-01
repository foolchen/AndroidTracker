package com.foolchen.lib.tracker.lifecycle

/**
 * 该接口需要在业务代码中实现，用于注入[ITrackerFragmentVisible]接口
 * @author chenchong
 * 2017/11/4
 * 下午4:01
 */
interface IFragmentVisibleHelper {
  fun registerIFragmentVisible(it: ITrackerFragmentVisible)
  fun unregisterIFragmentVisible(it: ITrackerFragmentVisible)
  fun getIFragmentVisible(): ITrackerFragmentVisible?
}