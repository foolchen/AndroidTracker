package com.foolchen.lib.tracker.demo

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.foolchen.lib.tracker.lifecycle.ITrackerIgnore
import com.foolchen.lib.tracker.lifecycle.ITrackerHelper

/**
 *  Activity的基类
 * @author chenchong
 * 2017/11/23
 * 下午3:18
 */
open class BaseActivity : AppCompatActivity(), ITrackerHelper, ITrackerIgnore {
  ///////////////////////////////////////////////////////////////////////////
  // 该类实现ITrackerHelper接口，此处两个方法全部返回null
  // 则页面名称（别名）会直接取使用canonicalName来当做标题
  // 并且不会有附加的属性
  ///////////////////////////////////////////////////////////////////////////
  override fun getTrackName(context: Context): String? = null

  override fun getTrackProperties(context: Context): Map<String, Any?>? = null

  ///////////////////////////////////////////////////////////////////////////
  // ITrackerIgnore接口用于确定当前Activity中是否包含Fragment
  // 如果返回值为true，则表明当前Activity中有包含Fragment，则此时不会对Activity进行统计
  // 如果返回值为false，则表明当前Activity中不包含Fragment，则此时会对Activity进行统计
  // 此处默认不包含Fragment，如有需要应该在子类中覆写该方法并修改返回值
  ///////////////////////////////////////////////////////////////////////////
  override fun isIgnored(): Boolean = false
}