package com.foolchen.lib.tracker.layout

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import com.foolchen.lib.tracker.R

/**
 * 统计用的Layout
 * @author chenchong
 * 2017/11/9
 * 上午10:07
 */
class TrackLayout : FrameLayout {
  private val rect = Rect()
  private var clickFunc: ((View, MotionEvent) -> Unit)? = null
  private var itemClickFunc: ((AdapterView<*>, View, Int, Long, MotionEvent) -> Unit)? = null

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
        hitView?.let {
          if (it is AdapterView<*>) {
            // 包装OnItemClickListener
            wrapItemClick(it, ev)
          } else {
            // 包装OnClickListener
            wrapClick(hitView, ev)
          }
        }
      }
    }
    return super.onTouchEvent(ev)
  }

  internal fun registerClickFunc(func: ((View, MotionEvent) -> Unit)) {
    this@TrackLayout.clickFunc = func
  }

  internal fun registerItemClickFunc(
      func: ((AdapterView<*>, View, Int, Long, MotionEvent) -> Unit)) {
    this@TrackLayout.itemClickFunc = func
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
    if (hitAdapterView(parent, x, y)) {
      // 如果是AdapterView（ListView、GridView等），则直接返回
      // AdapterView需要设置OnItemClickListener，而不是OnClickListener
      hitView = parent
    } else if (parent !is AdapterView<*>) {
      if (parent is ViewGroup && parent.childCount > 0) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
          val child = parent.getChildAt(i)
          hitView = findHitView(child, x, y)
          // 如果hitView不为空，则直接返回该View
          if (hitView != null) {
            break
          }
        }

        // 如果查找了所有的子View，都没有找到可点击的View
        if (hitView == null && parent.isClickable) {
          // 此时如果parent可点击，则认为点击的就是parent
          hitView = parent
        }
      } else if (parent.isClickable && hitPoint(parent, x, y)) {
        // 如果已经没有子View/或者本身为View，并且View可点击，则认为点击的就是该View
        hitView = parent
      }
    }
    return hitView
  }


  private fun hitAdapterView(view: View, x: Int, y: Int): Boolean =
      view is AdapterView<*> && hitPoint(view, x, y)

  /**
   * 判断一个View是否包含了对应的坐标
   */
  private fun hitPoint(view: View, x: Int, y: Int): Boolean {
    view.getGlobalVisibleRect(rect)
    return rect.contains(x, y)
  }

  /**
   * 对View的点击事件进行包装，便于增加统计代码
   */
  private fun wrapClick(view: View, ev: MotionEvent) {
    if (view.hasOnClickListeners()) {
      val viewInfo = listenerInfoField.get(view)
      val clickInfo = viewInfo?.javaClass?.getDeclaredField("mOnClickListener")
      val source = clickInfo?.get(viewInfo) as? OnClickListener
      source?.let {
        // 如果source已经是ClickWrapper则不需继续处理
        if (it !is ClickWrapper) {
          // 如果source不是ClickWrapper，则首先尝试复用原先已有的ClickWrapper（可能在RecyclerView中对View重新设置了OnClickListener，但是其ClickWrapper对象还在）
          var wrapper = view.getTag(R.id.android_tracker_click_listener)
          if (wrapper is ClickWrapper) {
            // 如果原先已存在ClickWrapper
            // 则对比原先ClickWrapper中的OnClickListener是否与source为同一个实例
            if (wrapper.source != source) {
              wrapper.source = source
            }
          } else {
            // 如果原先不存在ClickWrapper，则创建ClickWrapper
            wrapper = ClickWrapper(source, ev)
            view.setTag(R.id.android_tracker_click_listener, wrapper)
          }
          clickInfo?.let {
            it.isAccessible = true
            it.set(viewInfo, wrapper)
          }
        }
      }
    }
  }

  /**
   * 对AdapterView条目的点击监听进行包装,便于增加统计代码
   */
  private fun wrapItemClick(view: AdapterView<*>, ev: MotionEvent) {
    val source = view.onItemClickListener
    source?.let {
      if (source !is ItemClickWrapper) {
        // 如果原先设置的监听不为ItemClickWrapper类型，则对source进行包装
        // 如果已经为ItemClickWrapper，则直接复用原先监听即可，不需要再次包装
        view.onItemClickListener = ItemClickWrapper(source, ev)
      }
    }
  }

  /**
   * [View.OnClickListener]的包装类，内部包装了View的原[View.OnClickListener]，并且增加了点击统计
   *
   * @param source View的原[View.OnClickListener]
   * @param ev 触发点击时的坐标位置
   */
  private inner class ClickWrapper(var source: OnClickListener?,
      val ev: MotionEvent) : OnClickListener {
    override fun onClick(view: View) {
      source?.let {
        source?.onClick(view)
        clickFunc?.invoke(view, ev)
      }

    }
  }

  /**
   * [AdapterView.OnItemClickListener]的包装类，内部包装了原监听器，并且增加了点击统计
   */
  private inner class ItemClickWrapper(val source: AdapterView.OnItemClickListener,
      val ev: MotionEvent) : AdapterView.OnItemClickListener {
    override fun onItemClick(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
      source.onItemClick(adapterView, view, position, id)
      itemClickFunc?.invoke(adapterView, view, position, id, ev)
    }
  }
}