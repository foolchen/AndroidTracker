package com.foolchen.lib.tracker

import android.app.Application
import android.content.Context

/**
 * 辅助获取[Context]的接口
 *
 * 需要在APP的[Application]类中实现该接口
 *
 * @author chenchong
 * 2017/11/28
 * 下午8:43
 */
interface TrackContext {
  fun getApplicationContext(): Context
  fun registerActivityLifecycleCallbacks(callbacks: Application.ActivityLifecycleCallbacks)
}