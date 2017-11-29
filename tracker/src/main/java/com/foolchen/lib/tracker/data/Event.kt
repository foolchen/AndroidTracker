package com.foolchen.lib.tracker.data

import com.foolchen.lib.tracker.*
import com.foolchen.lib.tracker.utils.*

/**
 * 统计事件
 * @author chenchong
 * 2017/11/4
 * 下午2:48
 */
open class Event(
    @EventType private val event: String,
    private val screenAlias: String, val screenName: String,
    private val refererAlias: String, val referer: String,
    private val parentAlias: String,
    private val parent: String) {

  var properties: HashMap<String, Any>? = null
  val time = System.currentTimeMillis()

  init {
    properties = HashMap()
    properties!!.putAll(Tracker.additionalProperties)
  }

  fun addProperties(properties: Map<String, Any>?) {
    if (properties == null) {
      return
    }
    if (this.properties == null) {
      this.properties = HashMap()
    }
    this.properties?.putAll(properties)
  }

  fun toJson(): String {
    val o = HashMap<String, Any>()
    o.put(EVENT, event)
    o.put(TIME, time)
    o.put(DISTINCT_ID, Tracker.userId ?: buildInUUID)// 如果已登录，则该值是可以被替换掉的

    o.put(LIB, buildInLib)
    val properties = HashMap<String, Any>()
    properties.putAll(buildInProperties)
    properties.put(SCREEN_NAME_ALIAS, screenAlias)
    properties.put(SCREEN_NAME, screenName)
    properties.put(REFERER_ALIAS, refererAlias)
    properties.put(REFERER, referer)
    properties.put(PARENT_ALIAS, parentAlias)
    properties.put(PARENT, parent)
    Tracker.trackContext?.let {
      properties.put(NETWORK_TYPE,
          it.getApplicationContext().getNetworkType().desc())
      properties.put(WIFI, it.getApplicationContext().isWiFi())
    }
    this@Event.properties?.let {
      properties.putAll(it)
    }

    o.put(PROPERTIES, properties)
    return PRETTY_GSON.toJson(o)
  }
}