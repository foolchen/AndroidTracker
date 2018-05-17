package com.foolchen.lib.tracker.data

import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.utils.*
import com.google.gson.annotations.SerializedName

/**
 * 统计事件
 * @author chenchong
 * 2017/11/4
 * 下午2:48
 */
data class TrackerEvent(
    @SerializedName("event")
    @EventType private var event: String) {

  @SerializedName("properties")
  private var properties = HashMap<String, Any>()
  @SerializedName("time")
  internal var time = System.currentTimeMillis()

  @SerializedName("screenName")
  private var screenName = Tracker.screenName
  @SerializedName("screenClass")
  private var screenClass = Tracker.screenClass
  @SerializedName("screenTitle")
  private var screenTitle = Tracker.screenTitle
  @SerializedName("referer")
  private var referer = Tracker.referer
  @SerializedName("refererClass")
  private var refererClass = Tracker.refererClass
  @SerializedName("parent")
  private var parent = Tracker.parent
  @SerializedName("parentClass")
  private var parentClass = Tracker.parentClass

  init {
    Tracker.additionalProperties.filter { it.value != null }.forEach {
      this@TrackerEvent.properties[it.key] = it.value!!
    }
  }

  fun addProperties(properties: Map<String, Any?>?) {
    if (properties == null) {
      return
    }
    properties.filter { it.value != null }.forEach {
      this@TrackerEvent.properties[it.key] = it.value!!
    }
  }

  fun build(): Map<String, Any> {
    val o = HashMap<String, Any>()
    o.putAll(buildInObject)
    o[EVENT] = event
    o[TIME] = time

    o[LIB] = buildInLib
    val properties = HashMap<String, Any>()
    properties.putAll(buildInProperties)
    properties[SCREEN_NAME] = screenName
    properties[SCREEN_CLASS] = screenClass
    properties[TITLE] = screenTitle
    properties[REFERER] = referer
    properties[REFERER_CLASS] = refererClass
    properties[PARENT] = parent
    properties[PARENT_CLASS] = parentClass
    Tracker.trackContext.let {
      properties[NETWORK_TYPE] = it.getApplicationContext().getNetworkType().desc()
      properties.put(WIFI, it.getApplicationContext().isWiFi())
    }
    Tracker.channelId?.let {
      properties.put(CHANNEL, it)
    }
    this@TrackerEvent.properties.let {
      properties.putAll(it)
    }

    o[PROPERTIES] = properties
    return o
  }

  fun toPrettyJson(): String {
    return PRETTY_GSON.toJson(build())
  }
}