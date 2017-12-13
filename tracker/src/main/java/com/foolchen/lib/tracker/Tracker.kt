package com.foolchen.lib.tracker

import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import com.foolchen.lib.tracker.data.*
import com.foolchen.lib.tracker.lifecycle.ITrackerContext
import com.foolchen.lib.tracker.lifecycle.TrackerActivityLifeCycle
import com.foolchen.lib.tracker.utils.*
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

  internal var serviceHost: String? = null
  internal var servicePath: String? = null
  internal var projectName: String? = null
  /**
   * 上报接口的默认超时时间为3000ms
   */
  internal var timeoutDuration = 3000L

  private var isInitialized = false

  /**
   * 对AndroidTracker进行初始化
   *
   * 如果未调用该方法进行初始化，使用过程中可能会出现无法统计、Crash等情况
   */
  fun initialize(app: ITrackerContext) {
    trackContext = app
    initBuildInProperties(app.getApplicationContext())
    app.registerActivityLifecycleCallbacks(TrackerActivityLifeCycle())
    isInitialized = true
  }

  /**
   * 设置接口地址
   *
   * AndroidTracker中的数据上报使用了Retrofit，此处需要对host和path进行分别设置
   */
  fun setService(host: String, path: String) {
    this.serviceHost = host
    this.servicePath = path
  }

  /**
   * 设置项目名称
   */
  fun setProjectName(projectName: String) {
    this.projectName = projectName
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
   *
   * 注意：该方法不要与[ignoreView]同时调用，否则可能会失效
   *
   * @param view 要统计的View
   * @param properties 要额外添加的属性
   */
  fun trackView(view: View, properties: Map<String, Any?>?) {
    properties?.let {
      elementsProperties.put(view, properties)
    }
  }

  /**
   * 忽略View本次的点击事件（该方法仅生效一次，如需每次都忽略，应每次都调用该方法）
   *
   * 注意：该方法不要与[trackView]方法同时调用，否则可能会失效
   *
   * @param view 要忽略统计的View
   */
  fun ignoreView(view: View) {
    val properties = HashMap<String, Any>()
    properties.put(IGNORE_CLICK, true)
    elementsProperties.put(view, properties)
  }

  /**
   * 手动对自定义事件进行统计
   *
   * @param name 自定义事件的名称，对应内置属性中的event字段
   * @param properties 要统计的自定义事件的属性
   */
  fun trackEvent(name: String, properties: Map<String, Any?>?) {
    val event = TrackerEvent(name)
    event.addProperties(properties)
    trackEvent(event)
  }

  internal fun trackScreen(properties: Map<String, Any?>?) {
    val event = TrackerEvent(VIEW_SCREEN)
    event.addProperties(properties)
    trackEvent(event)

  }

  /**
   * 对View的点击进行统计
   *
   * 注意：在已经调用了[ignoreView]方法时，该方法被会直接返回，并不会执行View的点击事件统计
   *
   * @param view 要统计的点击事件View
   * @param ev 要统计的View点击事件触发时的[MotionEvent]
   * @param time 点击事件触发时的时间
   */
  internal fun trackView(view: View, ev: MotionEvent, time: Long) {
    val trackProperties = view.getTrackProperties(ev)
    if (trackProperties[IGNORE_CLICK] != null && trackProperties[IGNORE_CLICK] == true) {
      // 如果事件被忽略了，则直接返回
      return
    }
    val event = TrackerEvent(CLICK)
    event.time = time
    event.addProperties(trackProperties)
    trackEvent(event)
  }

  /**
   * 对AdapterView的点击进行统计
   */
  internal fun trackAdapterView(adapterView: AdapterView<*>, view: View, position: Int, id: Long,
      ev: MotionEvent, time: Long) {
    val event = TrackerEvent(CLICK)
    event.time = time
    val trackProperties = view.getTrackProperties(ev)
    event.addProperties(trackProperties)
    trackEvent(event)
  }

  /**
   * 清空已保存的前向地址等状态
   */
  internal fun onBackground() {
    isBackground = true
    if (isInitialized) {
      val event = TrackerEvent(APP_END)
      val properties = HashMap<String, Any>()
      properties.put(EVENT_DURATION, System.currentTimeMillis() - appStartTime)
      event.addProperties(properties)
      trackEvent(event, background = true)
    }
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
    if (isInitialized) {
      val event = TrackerEvent(APP_START)
      val properties = HashMap<String, Any>()
      properties.put(RESUME_FROM_BACKGROUND, isBackground)
      event.addProperties(properties)
      trackEvent(event, foreground = true)
    }
    isBackground = false
  }
}