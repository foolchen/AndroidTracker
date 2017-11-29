package com.foolchen.lib.tracker.utils

import android.app.Activity
import android.support.v4.app.Fragment
import android.util.Log
import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.data.Event
import com.foolchen.lib.tracker.data.Mode
import com.foolchen.lib.tracker.lifecycle.ITrack
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
  if (this is ITrack) {
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
  if (this is ITrack) {
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
 * 获取Activity中需要的附加属性
 */
internal fun Activity.getTrackProperties(): Map<String, Unit> {
  val properties = HashMap<String, Unit>()
  if (this is ITrack) {
    val trackProperties = this.getTrackProperties()
    if (trackProperties != null) {
      properties.putAll(trackProperties)
    }
  }
  return properties
}

/**
 * 获取Fragment中需要的附加属性
 */
internal fun Fragment.getTrackProperties(): Map<String, Unit> {
  val properties = HashMap<String, Unit>()
  if (this is ITrack) {
    val trackProperties = this.getTrackProperties()
    if (trackProperties != null) {
      properties.putAll(trackProperties)
    }
  }
  return properties
}

internal fun trackEvent(event: Event) {
  //TODO 此处进行数据的处理

  // 打印日志
  log(event)
}


internal fun log(event: Event) {
  if (Tracker.mode == Mode.DEBUG_ONLY) {
    log(event.toJson())
  } else if (Tracker.mode == Mode.DEBUG_TRACK) {
    log(event.toJson())
  }
}

private fun log(s: String) {
  Log.d(TAG, s)
}
