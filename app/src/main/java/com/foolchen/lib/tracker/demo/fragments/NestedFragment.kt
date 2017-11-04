package com.foolchen.lib.tracker.demo.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.foolchen.lib.tracker.demo.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 嵌套在内部的Fragment
 * @author chenchong
 * 2017/11/4
 * 下午5:34
 */
class NestedFragment : DemoFragment() {
  override fun getTrackName(): String = getString(R.string.text_nested_fragment)

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    child_fragment_container.setBackgroundColor(Color.RED)
    val text = TextView(context)
    text.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT)
    text.setText(R.string.text_nested_fragment)
    text.gravity = Gravity.CENTER
    child_fragment_container.addView(text)
  }
}