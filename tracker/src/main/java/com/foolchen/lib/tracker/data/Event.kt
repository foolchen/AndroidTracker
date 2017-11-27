package com.foolchen.lib.tracker.data

import com.foolchen.lib.tracker.*
import com.foolchen.lib.tracker.utils.generateId
import com.google.gson.annotations.SerializedName

/**
 * 统计事件
 * @author chenchong
 * 2017/11/4
 * 下午2:48
 */
open class Event(
    @SerializedName(TYPE)
    @EventType private val type: String,
    @SerializedName(NAME)
    private val name: String,
    @SerializedName(CLASS) val clazz: String,
    @SerializedName(REFER)
    private val refer: String,
    @SerializedName(REFER_CLASS) val referClazz: String,
    @SerializedName(PARENT) private val parent: String,
    @SerializedName(PARENT_CLASS) private val parentClazz: String) {
  @SerializedName(DISTINCT_ID)
  private val distinctId: String = Tracker.distinctId
  @SerializedName(USER_ID)
  private val userId: String? = Tracker.userId
  @SerializedName(ID)
  val id: Long
  @SerializedName(PROPERTIES)
  var properties: HashMap<String, Any?>? = null
  @SerializedName(TIME)
  val time = System.currentTimeMillis()

  init {
    id = generateId(distinctId + userId)
    properties = HashMap()
    properties!!.putAll(Tracker.regularProperties)
    properties!!.putAll(Tracker.additionalProperties)
  }

  fun addProperties(properties: Map<String, Any?>?) {
    if (properties == null) {
      return
    }
    if (this.properties == null) {
      this.properties = HashMap()
    }
    this.properties?.putAll(properties)
  }
}