package com.foolchen.lib.tracker.lifecycle

import android.support.v4.app.Fragment

/**
 * 用于监听[Fragment]的可见性
 * @author chenchong
 * 2017/11/4
 * 下午12:02
 */
interface IFragmentVisible {
  fun onFragmentVisible(f: Fragment)
  fun onFragmentHide(f: Fragment)
}