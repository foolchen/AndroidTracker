package com.foolchen.lib.tracker.layout

import android.app.Activity
import android.view.ViewGroup


fun wrap(activity: Activity) {
  val decorView = activity.window.decorView
  if (decorView != null && decorView is ViewGroup) {
    decorView.addView(TrackLayout(activity),
        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT))
  }
}