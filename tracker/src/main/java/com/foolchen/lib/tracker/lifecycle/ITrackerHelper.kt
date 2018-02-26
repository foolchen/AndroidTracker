package com.foolchen.lib.tracker.lifecycle

import android.content.Context

/**
 * 用于获取页面信息的接口
 * @author chenchong
 * 2017/11/4
 * 下午1:45
 */
interface ITrackerHelper {
  /**
   * 获取当前页面的名称
   *
   * **注意：在Fragment中使用时，不要使用Fragment.getString()等方法。
   * 由于setUserVisibleHint()方法的调用可能在所有的生命周期之前，如果调用Fragment.getString()等方法可能会导致错误。
   * 此处应该使用方法回传的[Context]**
   */
  fun getTrackName(context: Context): String?

  /**
   * 获取当前页面需要附加的参数
   *
   * **注意：在Fragment中使用时，不要使用Fragment.getString()等方法。
   * 由于setUserVisibleHint()方法的调用可能在所有的生命周期之前，如果调用Fragment.getString()等方法可能会导致错误
   * 此处应该使用方法回传的[Context]**
   */
  fun getTrackProperties(context: Context): Map<String, *>?
}