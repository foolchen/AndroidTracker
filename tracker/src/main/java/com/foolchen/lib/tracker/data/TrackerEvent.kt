package com.foolchen.lib.tracker.data

import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.Tracker.parent
import com.foolchen.lib.tracker.Tracker.parentClass
import com.foolchen.lib.tracker.Tracker.referer
import com.foolchen.lib.tracker.Tracker.refererClass
import com.foolchen.lib.tracker.Tracker.screenClass
import com.foolchen.lib.tracker.Tracker.screenName
import com.foolchen.lib.tracker.Tracker.screenTitle
import com.foolchen.lib.tracker.utils.*

/**
 * 统计事件
 * @author chenchong
 * 2017/11/4
 * 下午2:48
 */
open class TrackerEvent(
    @EventType private val event: String) {

  private val properties = HashMap<String, Any>()
  private val time = System.currentTimeMillis()

  init {
    Tracker.additionalProperties.filter { it.value != null }.forEach {
      this@TrackerEvent.properties.put(it.key, it.value!!)
    }
  }

  fun addProperties(properties: Map<String, Any?>?) {
    if (properties == null) {
      return
    }
    properties.filter { it.value != null }.forEach {
      this@TrackerEvent.properties.put(it.key, it.value!!)
    }
  }

  fun toJson(): String {
    val o = HashMap<String, Any>()
    o.putAll(buildInObject)
    o.put(EVENT, event)
    o.put(TIME, time)

    o.put(LIB, buildInLib)
    val properties = HashMap<String, Any>()
    properties.putAll(buildInProperties)
    properties.put(SCREEN_NAME, screenName)
    properties.put(SCREEN_CLASS, screenClass)
    properties.put(TITLE, screenTitle)
    properties.put(REFERER, referer)
    properties.put(REFERER_CLASS, refererClass)
    properties.put(PARENT, parent)
    properties.put(PARENT_CLASS, parentClass)
    Tracker.trackContext?.let {
      properties.put(NETWORK_TYPE,
          it.getApplicationContext().getNetworkType().desc())
      properties.put(WIFI, it.getApplicationContext().isWiFi())
    }
    Tracker.channelId?.let {
      properties.put(CHANNEL, it)
    }
    this@TrackerEvent.properties.let {
      properties.putAll(it)
    }

    o.put(PROPERTIES, properties)
    return GSON.toJson(o)
  }

  fun toPrettyJson(): String {
    val o = HashMap<String, Any>()
    o.putAll(buildInObject)
    o.put(EVENT, event)
    o.put(TIME, time)

    o.put(LIB, buildInLib)
    val properties = HashMap<String, Any>()
    properties.putAll(buildInProperties)
    properties.put(SCREEN_NAME, screenName)
    properties.put(SCREEN_CLASS, screenClass)
    properties.put(TITLE, screenTitle)
    properties.put(REFERER, referer)
    properties.put(REFERER_CLASS, refererClass)
    properties.put(PARENT, parent)
    properties.put(PARENT_CLASS, parentClass)
    Tracker.trackContext?.let {
      properties.put(NETWORK_TYPE,
          it.getApplicationContext().getNetworkType().desc())
      properties.put(WIFI, it.getApplicationContext().isWiFi())
    }
    Tracker.channelId?.let {
      properties.put(CHANNEL, it)
    }
    this@TrackerEvent.properties.let {
      properties.putAll(it)
    }

    o.put(PROPERTIES, properties)
    return PRETTY_GSON.toJson(o)
  }
}