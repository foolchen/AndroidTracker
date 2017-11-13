package com.foolchen.lib.tracker.layout

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * 统计用的Layout
 * @author chenchong
 * 2017/11/9
 * 上午10:07
 */
class TrackLayout : FrameLayout {
  private val TAG = TrackLayout::class.java.simpleName

  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr)

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
      context, attrs, defStyleAttr, defStyleRes)

  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    if (ev != null) {
      val action = ev.action and MotionEvent.ACTION_MASK
      if (action == MotionEvent.ACTION_DOWN) {
        log("dispatchTouchEvent", "ACTION_DOWN")
      } else if (action == MotionEvent.ACTION_UP) {
        log("dispatchTouchEvent", "ACTION_UP")
      }
    }
    return super.dispatchTouchEvent(ev)
  }

  override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {

    if (ev != null) {
      val action = ev.action and MotionEvent.ACTION_MASK
      if (action == MotionEvent.ACTION_DOWN) {
        log("onInterceptTouchEvent", "ACTION_DOWN")
      } else if (action == MotionEvent.ACTION_UP) {
        log("onInterceptTouchEvent", "ACTION_UP")
      }
    }

    return super.onInterceptTouchEvent(ev)
  }

  override fun onTouchEvent(ev: MotionEvent?): Boolean {
    if (ev != null) {
      val action = ev.action and MotionEvent.ACTION_MASK
      if (action == MotionEvent.ACTION_DOWN) {
        log("onTouchEvent", "ACTION_DOWN")
      } else if (action == MotionEvent.ACTION_UP) {
        log("onTouchEvent", "ACTION_UP")
      }
    }
    return super.onTouchEvent(ev)
  }

  override fun performClick(): Boolean {
    log("performClick", "null")
    return super.performClick()
  }

  private fun log(method: String, action: String) {
    Log.d(TAG, "method : $method , action : $action")
  }
}