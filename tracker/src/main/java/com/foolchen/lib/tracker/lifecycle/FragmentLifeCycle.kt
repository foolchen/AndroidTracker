package com.foolchen.lib.tracker.lifecycle

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

/**
 * 该类用于监听所有Fragment的生命周期<p/>
 * 注意：该类会对Activity中所有的Fragment及子Fragment进行监听，故不需要在Fragment中添加监听，否则会出现同一个Fragment被监听多次的问题
 *
 * @author chenchong
 * 2017/11/4
 * 上午11:27
 */
class FragmentLifeCycle : FragmentManager.FragmentLifecycleCallbacks() {

  override fun onFragmentCreated(fm: FragmentManager?, f: Fragment?, savedInstanceState: Bundle?) {
  }

  override fun onFragmentAttached(fm: FragmentManager?, f: Fragment?, context: Context?) {
  }

  override fun onFragmentResumed(fm: FragmentManager?, f: Fragment?) {
  }

  override fun onFragmentPaused(fm: FragmentManager?, f: Fragment?) {
  }


  override fun onFragmentDetached(fm: FragmentManager?, f: Fragment?) {
  }

  override fun onFragmentDestroyed(fm: FragmentManager?, f: Fragment?) {
  }
}