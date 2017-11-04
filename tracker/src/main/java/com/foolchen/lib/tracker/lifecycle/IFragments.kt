package com.foolchen.lib.tracker.lifecycle

import android.app.Activity
import android.support.v4.app.Fragment


/**
 * 获取当前[Activity]/[Fragment]中所有[Fragment]的接口<p/>
 * 注意：获取到的[Fragment]都为弱引用
 * @author chenchong
 * 2017/11/4
 * 下午12:04
 */
interface IFragments {
  /**
   * 获取当前[Activity]/[Fragment]中的所有的[Fragment]
   */
  //fun getFragments(): List<WeakReference<Fragment>>

  /**
   * 强制规定当前页面中是否含有子Fragment
   * 如果该方法返回false，则会被强制当做需要被统计的Fragment进行统计,以免由于Fragment没有及时初始化，导致监听的Fragment错误
   */
  fun hasChildFragments(): Boolean
}