package com.foolchen.lib.tracker

import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import com.foolchen.lib.tracker.data.*
import com.foolchen.lib.tracker.lifecycle.ITrackerContext
import com.foolchen.lib.tracker.lifecycle.TrackerActivityLifeCycle
import com.foolchen.lib.tracker.utils.getTrackProperties
import com.foolchen.lib.tracker.utils.initBuildInProperties
import com.foolchen.lib.tracker.utils.trackEvent
import java.util.*
import kotlin.collections.HashMap
import com.foolchen.lib.tracker.utils.login as buildInLogin
import com.foolchen.lib.tracker.utils.logout as buildInLogout

/**
 * 统计工具
 *
 * 该工具用于统计的初始化、登录、注册等操作
 * @author chenchong
 * 2017/11/4
 * 上午11:17
 */
object Tracker {


  /**当前正在浏览的页面的名称*/
  internal var screenName: String = ""
  internal var screenClass: String = ""
  internal var screenTitle: String = ""

  /**当前正在浏览的页面所依附的页面*/
  internal var parent: String = ""
  internal var parentClass: String = ""

  /** 上一个浏览页面的名称 */
  internal var referer: String = ""
  internal var refererClass: String = ""

  /**
   * 开发者在初始化时附加的属性
   * 这些属性在所有的事件中都会存在
   */
  internal val additionalProperties = HashMap<String, Any?>()

  /**
   * 用于保存各个元素需要的附加属性
   */
  internal val elementsProperties = WeakHashMap<View, Map<String, Any?>?>()

  internal var channelId: String? = null

  internal var mode = TrackerMode.RELEASE
  internal var isBackground = false
  internal var clearOnBackground = true

  internal var appStartTime = 0L

  internal var trackContext: ITrackerContext? = null

  fun initialize(app: ITrackerContext) {
    trackContext = app
    initBuildInProperties(app.getApplicationContext())
    app.registerActivityLifecycleCallbacks(TrackerActivityLifeCycle())
  }

  /**
   * 设置统计的模式
   * @see [TrackerMode]]
   * @see [TrackerMode.DEBUG_ONLY]
   * @see [TrackerMode.DEBUG_TRACK]
   * @see [TrackerMode.RELEASE]
   */
  fun setMode(mode: TrackerMode) {
    this@Tracker.mode = mode
  }

  /**
   * 设置是否在App切换到后台时，将前向地址等信息清空
   * 该功能默认为开启
   * @param clear 设置为true，则之前所有的前向地址、前向类名等都会在App被切换到后台时被清空，从后台切换回App时，访问的页面没有前向地址等信息
   */
  fun clearOnBackground(clear: Boolean) {
    clearOnBackground = clear
  }

  /**
   * 增加自定义属性
   */
  fun addProperty(key: String, value: Any?) {
    if (value != null) {
      additionalProperties.put(key, value)
    }
  }

  /**
   * 增加自定义属性
   */
  fun addProperties(properties: Map<String, Any?>?) {
    properties?.forEach({
      if (it.value != null) {
        additionalProperties.put(it.key, it.value!!)
      }
    })
  }

  /**
   * 用户登录
   */
  fun login(userId: String) {
    buildInLogin(userId)
  }

  /**
   * 用户登出
   */
  fun logout() {
    trackContext?.let { buildInLogout() }

  }

  fun setChannelId(channelId: String?) {
    this.channelId = channelId
  }

  /**
   * 为要统计的元素增加需要附加的属性
   */
  fun trackView(view: View, properties: Map<String, Any?>?) {
    properties?.let {
      elementsProperties.put(view, properties)
    }
  }

  internal fun trackScreen(properties: Map<String, Any?>?) {
    val event = TrackerEvent(VIEW_SCREEN)
    event.addProperties(properties)
    trackEvent(event)

    // TODO: 2017/11/4 chenchong 用于暂存或者请求接口
  }

  /**
   * 对View的点击进行统计
   */
  internal fun trackView(view: View, ev: MotionEvent) {
    val event = TrackerEvent(CLICK)
    val trackProperties = view.getTrackProperties(ev)
    event.addProperties(trackProperties)
    trackEvent(event)
  }

  /**
   * 对AdapterView的点击进行统计
   */
  internal fun trackAdapterView(adapterView: AdapterView<*>, view: View, position: Int, id: Long,
      ev: MotionEvent) {
    val event = TrackerEvent(CLICK)
    val trackProperties = view.getTrackProperties(ev)
    event.addProperties(trackProperties)
    trackEvent(event)
  }

  /**
   * 清空已保存的前向地址等状态
   */
  internal fun onBackground() {
    isBackground = true
    val event = TrackerEvent(APP_END)
    val properties = HashMap<String, Any>()
    properties.put(EVENT_DURATION, System.currentTimeMillis() - appStartTime)
    event.addProperties(properties)
    trackEvent(event)

    clearOnBackground.let {
      screenName = ""
      screenClass = ""
      parent = ""
      parentClass = ""
      referer = ""
      refererClass = ""
    }
  }

  internal fun onForeground() {
    appStartTime = System.currentTimeMillis()

    val event = TrackerEvent(APP_START)
    val properties = HashMap<String, Any>()
    properties.put(RESUME_FROM_BACKGROUND, isBackground)
    event.addProperties(properties)
    trackEvent(event)
    isBackground = false
  }
}