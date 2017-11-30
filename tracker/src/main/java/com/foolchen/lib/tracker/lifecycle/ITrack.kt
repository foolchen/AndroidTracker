package com.foolchen.lib.tracker.lifecycle

/**
 * 用于获取页面信息的接口
 * @author chenchong
 * 2017/11/4
 * 下午1:45
 */
interface ITrack {
  /**
   * 获取当前页面的名称
   */
  fun getTrackName(): String?

  /**
   * 获取当前页面需要附加的参数
   */
  fun getTrackProperties(): Map<String, *>?
}