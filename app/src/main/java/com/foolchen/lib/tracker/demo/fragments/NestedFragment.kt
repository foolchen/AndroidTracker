package com.foolchen.lib.tracker.demo.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.foolchen.lib.tracker.demo.R

/**
 * 嵌套在内部的Fragment
 * @author chenchong
 * 2017/11/4
 * 下午5:34
 */
class NestedFragment : DemoFragment() {
  override fun getTrackName(): String = getString(R.string.text_nested_fragment)

  override fun onResume() {
    super.onResume()
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    val text = TextView(context)
    text.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT)
    text.setText(R.string.text_nested_fragment)
    text.gravity = Gravity.CENTER
    text.setBackgroundColor(Color.RED)
    return text
  }
}