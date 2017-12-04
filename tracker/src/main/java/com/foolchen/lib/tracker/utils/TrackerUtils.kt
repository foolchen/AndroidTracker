package com.foolchen.lib.tracker.utils

import android.app.Activity
import android.support.v4.app.Fragment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.foolchen.lib.tracker.data.ELEMENT_CONTENT
import com.foolchen.lib.tracker.data.ELEMENT_TYPE
import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.data.TrackerEvent
import com.foolchen.lib.tracker.data.TrackerMode
import com.foolchen.lib.tracker.lifecycle.ITrackerHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder

val PRETTY_GSON: Gson by lazy {
  GsonBuilder().setPrettyPrinting().create()
}
val GSON: Gson by lazy {
  Gson()
}

const val TAG = "AndroidTracker"

/**
 * 用于获取Activity的名字
 */
internal fun Activity.getTrackName(): String {
  var name: String? = null
  if (this is ITrackerHelper) {
    name = this.getTrackName()
  }
  if (name.isNullOrEmpty()) {
    name = this.javaClass.canonicalName
  }
  if (name.isNullOrEmpty()) {
    name = this.toString()
  }
  return name!!
}

/**
 * 用于获取Fragment的名字
 */
internal fun Fragment.getTrackName(): String {
  var name: String? = null
  if (this is ITrackerHelper) {
    name = this.getTrackName()
  }
  if (name.isNullOrEmpty()) {
    name = this.javaClass.canonicalName
  }
  if (name.isNullOrEmpty()) {
    name = this.toString()
  }
  return name!!
}

internal fun Activity?.getTrackTitle(): String = this?.title?.toString() ?: ""

internal fun Fragment.getTrackTitle(): String = activity?.getTrackTitle() ?: ""

/**
 * 获取Activity中需要的附加属性
 */
internal fun Activity.getTrackProperties(): Map<String, Any> {
  val properties = HashMap<String, Any>()
  if (this is ITrackerHelper) {
    this.getTrackProperties()?.let {
      it.filter { it.value != null }.forEach {
        properties.put(it.key, it.value!!)
      }
    }
  }
  return properties
}

/**
 * 获取Fragment中需要的附加属性
 */
internal fun Fragment.getTrackProperties(): Map<String, Any> {
  val properties = HashMap<String, Any>()
  if (this is ITrackerHelper) {
    this.getTrackProperties()?.let {
      it.filter { it.value != null }.forEach {
        properties.put(it.key, it.value!!)
      }
    }
  }
  return properties
}

internal fun View.getTrackProperties(ev: MotionEvent?): Map<String, Any> {
  // 首先获取元素本身的属性
  val properties = HashMap<String, Any>()
  properties.put(ELEMENT_TYPE, this.javaClass.name)
  if (this is TextView) {
    properties.put(ELEMENT_CONTENT, this.text?.toString() ?: "")
  }
  /*ev?.let {
    properties.put(ELEMENT_X, ev.x)
    properties.put(ELEMENT_Y, ev.y)
  }*/

  // 然后获取开发者附加的属性
  val additionalProperties = Tracker.elementsProperties[this]
  additionalProperties?.filter { it.value != null }?.forEach {
    properties.put(it.key, it.value!!)
  }
  Tracker.elementsProperties.remove(this)
  return properties
}

internal fun trackEvent(event: TrackerEvent) {
  //TODO 此处进行数据的处理

  // 打印日志
  log(event)
}


internal fun log(event: TrackerEvent) {
  if (Tracker.mode == TrackerMode.DEBUG_ONLY) {
    log(event.toJson())
  } else if (Tracker.mode == TrackerMode.DEBUG_TRACK) {
    log(event.toJson())
  }
}

private fun log(s: String) {
  Log.d(TAG, s)
}
