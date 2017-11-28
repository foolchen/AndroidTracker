package com.foolchen.lib.tracker

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.foolchen.lib.tracker.data.Event
import com.foolchen.lib.tracker.data.Mode
import com.foolchen.lib.tracker.data.VIEW_SCREEN
import com.foolchen.lib.tracker.lifecycle.ActivityLifeCycle
import com.foolchen.lib.tracker.utils.TAG
import com.foolchen.lib.tracker.utils.getBuildInProperties
import com.foolchen.lib.tracker.utils.log
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
  internal var name: String = ""
  internal var clazz: String = ""

  /**当前正在浏览的页面所依附的页面*/
  internal var parent: String = ""
  internal var parentClazz: String = ""

  /** 上一个浏览页面的名称 */
  internal var refer: String = ""
  internal var referClazz: String = ""

  /**
   * 固定需要获取的属性，该属性在初始化时完成
   * 这些属性在所有的事件中都会存在
   */
  internal val buildInProperties = HashMap<String, Any>()
  /**
   * 开发者在初始化时附加的属性
   * 这些属性在所有的事件中都会存在
   */
  internal val additionalProperties = HashMap<String, Any>()

  internal var distinctId = ""
  internal var userId: String? = null

  internal var mode = Mode.RELEASE
  private var isCleanWithBackground = true

  fun initialize(app: Application) {
    buildInProperties.putAll(getBuildInProperties(app))
    app.registerActivityLifecycleCallbacks(ActivityLifeCycle())
  }

  /**
   * 设置统计的模式
   * @see [Mode]]
   * @see [Mode.DEBUG_ONLY]
   * @see [Mode.DEBUG_TRACK]
   * @see [Mode.RELEASE]
   */
  fun setMode(mode: Mode) {
    this@Tracker.mode = mode
  }

  /**
   * 设置是否在App切换到后台时，将前向地址等信息清空
   * 该功能默认为开启
   * @param clean 设置为true，则之前所有的前向地址、前向类名等都会在App被切换到后台时被清空，从后台切换回App时，访问的页面没有前向地址等信息
   */
  fun cleanWithBackground(clean: Boolean) {
    isCleanWithBackground = clean
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
    buildInLogin(buildInProperties, userId)
  }

  /**
   * 用户登出
   */
  fun logout(context: Context) {
    buildInLogout(context, buildInProperties)
  }

  internal fun trackScreen(properties: Map<String, Any?>?) {
    val event = Event(VIEW_SCREEN, name,
        clazz, refer, referClazz, parent, parentClazz)
    event.addProperties(properties)
    log(event)

    // TODO: 2017/11/4 chenchong 用于暂存或者请求接口
  }

  /**
   * 对View的点击进行统计
   */
  internal fun trackView(view: View, ev: MotionEvent) {
    if (mode != Mode.RELEASE) {
      if (view is TextView) {
        Log.d(TAG, "text = ${view.text} , (x,y) = (${ev.x},${ev.y})")
      }
    }
  }

  /**
   * 对AdapterView的点击进行统计
   */
  internal fun trackAdapterView(adapterView: AdapterView<*>, view: View, position: Int, id: Long,
      ev: MotionEvent) {
    if (mode != Mode.RELEASE) {
      if (view is TextView) {
        Log.d(TAG,
            "text = ${view.text} , position = $position , id = $id , (x,y) = (${ev.x},${ev.y})")
      }
    }
  }

  /**
   * 清空已保存的前向地址等状态
   */
  internal fun clean() {
    isCleanWithBackground.let {
      name = ""
      clazz = ""
      parent = ""
      parentClazz = ""
      refer = ""
      referClazz = ""
    }
  }

}