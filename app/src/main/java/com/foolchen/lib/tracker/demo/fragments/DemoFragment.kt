package com.foolchen.lib.tracker.demo.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foolchen.lib.tracker.demo.R
import com.foolchen.lib.tracker.demo.listeners

/**
 * 演示用的Fragment
 * @author chenchong
 * 2017/11/4
 * 下午4:57
 */

class DemoFragment : Fragment() {
  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?) = inflater?.inflate(R.layout.activity_main, container, false)

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    listeners()
  }
}
