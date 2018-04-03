package com.foolchen.lib.tracker.layout

import android.app.Activity
import android.support.v4.view.ViewCompat
import android.view.ViewGroup
import com.foolchen.lib.tracker.Tracker


fun wrap(activity: Activity) {
  val decorView = activity.window.decorView
  if (decorView != null && decorView is ViewGroup) {
    val trackLayout = TrackLayout(activity)
    trackLayout.registerClickFunc { view, ev, time ->
      Tracker.trackView(view, ev, time)
    }
    trackLayout.registerItemClickFunc { adapterView, view, position, id, ev, time ->
      Tracker.trackAdapterView(adapterView, view, position, id, ev, time)
    }
    decorView.addView(trackLayout,
        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT))
    ViewCompat.setElevation(trackLayout,
        999F)// 提升布局层次，防止fragmentation等库由于侧滑返回添加的布局导致该布局被覆盖，从而导致点击统计失效
  }
}