package com.foolchen.lib.tracker.lifecycle

import android.app.Activity
import android.support.v4.app.Fragment
import java.lang.ref.WeakReference


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
  fun getFragments(): List<WeakReference<Fragment>>
}