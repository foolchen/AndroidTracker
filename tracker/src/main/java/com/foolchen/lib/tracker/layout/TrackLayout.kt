package com.foolchen.lib.tracker.layout

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.foolchen.lib.tracker.R

/**
 * 统计用的Layout
 * @author chenchong
 * 2017/11/9
 * 上午10:07
 */
class TrackLayout : FrameLayout {
  private val TAG = TrackLayout::class.java.simpleName
  private val rect = Rect()
  private var clickFunc: ((View, MotionEvent) -> Unit)? = null

  private val listenerInfoField by lazy {
    // 通过反射拿到mListenerInfo，并且设置为可访问（用于后续替换点击事件）
    val declaredField = View::class.java.getDeclaredField("mListenerInfo")
    declaredField.isAccessible = true
    return@lazy declaredField
  }


  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr)


  override fun onTouchEvent(ev: MotionEvent?): Boolean {
    if (ev != null) {
      val ac = ev.action and MotionEvent.ACTION_MASK
      if (ac == MotionEvent.ACTION_DOWN) {
        val hitView = findHitView(rootView, ev.x.toInt(), ev.y.toInt())
        if (hitView != null) {
          // 查找到了点击的View
          wrapClick(hitView, ev)
        }
      }
    }
    return super.onTouchEvent(ev)
  }

  internal fun registerClickFunc(func: ((View, MotionEvent) -> Unit)) {
    this@TrackLayout.clickFunc = func
  }

  private fun log(method: String, action: String) {
    Log.d(TAG, "method : $method , action : $action")
  }

  /**
   * 根据当前坐标值查找对应位置的View
   *
   * 该方法为递归实现，如果传入的布局为[ViewGroup]，则执行递归；否则，则对[View]进行判断。
   * 在递归过程中，如果发现[ViewGroup]中没有合适的[View]，则会对[ViewGroup]本身进行判断，
   * 如果[ViewGroup]本身可点击，则会将[ViewGroup]当做点击的[View]
   */
  private fun findHitView(parent: View, x: Int, y: Int): View? {
    var hitView: View? = null
    if (parent is ViewGroup && parent.childCount > 0) {
      val childCount = parent.childCount
      for (i in 0 until childCount) {
        val child = parent.getChildAt(i)
        hitView = findHitView(child, x, y)
        // 如果hitView不为空，则直接返回该View
        if (hitView == null && parent.isClickable) {
          // 如果没有查找到对应的可点击的View
          // 并且如果parent可点击，则认为点击的就是parent
          hitView = parent
        } else if (hitView != null) {
          break
        }
      }
    } else if (parent.isClickable && isHitPoint(parent, x, y)) {
      // 如果已经没有子View/或者本身为View，并且View可点击，则认为点击的就是该View
      hitView = parent
    }
    return hitView
  }


  /**
   * 判断一个View是否包含了对应的坐标
   */
  private fun isHitPoint(view: View, x: Int, y: Int): Boolean {
    view.getGlobalVisibleRect(rect)
    return rect.contains(x, y)
  }

  /**
   * 对View的点击事件进行包装，便于增加统计代码
   */
  private fun wrapClick(view: View, ev: MotionEvent) {
    if (view.hasOnClickListeners()) {
      val tag = view.getTag(R.id.android_tracker_click_listener)
      if (tag !is OnClickListener) {
        // 如果不存在，则重新设置
        val listenerInfo = listenerInfoField.get(view)
        listenerInfo?.javaClass?.getDeclaredField("mOnClickListener")?.let {
          it.isAccessible = true
          val clickWrapper = ClickWrapper(it.get(listenerInfo) as? OnClickListener, ev)
          it.set(listenerInfo, clickWrapper)
          view.setTag(R.id.android_tracker_click_listener, clickWrapper)
        }
      }
    }
  }

  /**
   * [View.OnClickListener]的包装类，内部包装了View的原[View.OnClickListener]，并且增加了点击统计
   *
   * @param listener View的原[View.OnClickListener]
   * @param ev 触发点击时的坐标位置
   */
  private inner class ClickWrapper(val listener: OnClickListener?,
      val ev: MotionEvent) : OnClickListener {
    override fun onClick(view: View?) {
      listener?.let {
        listener.onClick(view)
        view?.let { clickFunc?.invoke(view, ev) }
      }

    }
  }
}