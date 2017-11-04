package com.foolchen.lib.tracker.event

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
    @SerializedName(REFER)
    private val refer: String, @SerializedName(PARENT) private val parent: String) {
  @SerializedName(DISTINCT_ID)
  private val distinctId: String = Tracker.distinctId
  @SerializedName(USER_ID)
  private val userId: String? = Tracker.userId
  @SerializedName(ID)
  val id: Long
  @SerializedName(PROPERTIES)
  var properties: HashMap<String, Any?>? = null

  init {
    id = generateId(distinctId + userId)
    properties = HashMap()
    properties!!.putAll(Tracker.regularProperties)
    properties!!.putAll(Tracker.additionalProperties)
  }

}