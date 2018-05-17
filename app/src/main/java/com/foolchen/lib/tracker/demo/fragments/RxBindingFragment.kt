package com.foolchen.lib.tracker.demo.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.demo.R
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.fragment_rx_binding.*
import java.util.concurrent.TimeUnit

/**
 * rx-binding事件测试
 * @author chenchong
 * 2018/5/17
 * 上午10:09
 */
class RxBindingFragment : BaseFragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_rx_binding, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    rv_data.layoutManager = GridLayoutManager(context, 3)

    RxView.clicks(acb_fill_data)
        .throttleFirst(1500, TimeUnit.MILLISECONDS)
        .subscribe {
          // 触发填充数据
          val map = HashMap<String, String>()
          map["custom_action"] = "自定义行为_填充数据"
          Tracker.trackView(acb_fill_data, map)

          val data = ArrayList<String>()
          for (i in 0 until 100) {
            data.add("Item $i")
          }

          rv_data.adapter = RxBindingAdapter(data)
        }
  }

  private inner class RxBindingAdapter(
      val data: List<String>) : RecyclerView.Adapter<RxBindingHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
        viewType: Int): RxBindingHolder = RxBindingHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_common, parent, false))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RxBindingHolder, position: Int) {
      (holder.itemView as? TextView)?.text = data[position]
    }
  }

  private inner class RxBindingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    init {
      RxView.clicks(itemView)
          .throttleFirst(1500, TimeUnit.MILLISECONDS)
          .subscribe {
            val text = (itemView as? TextView)?.text?.toString() ?: ""
            val map = HashMap<String, String>()
            map["custom_action"] = "自定义行为_点击条目_$text"
            Tracker.trackView(itemView, map)

            Snackbar.make(cl, text, Snackbar.LENGTH_INDEFINITE)
                .setAction("关闭", { view ->
                  val localMap = HashMap<String, String>()
                  localMap["custom_action"] = "自定义行为_SnackBar_$text"
                  Tracker.trackView(view, localMap)
                })
                .show()
          }
    }
  }
}