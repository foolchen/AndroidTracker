package com.foolchen.lib.tracker.demo

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.demo.data.Demo
import com.foolchen.lib.tracker.demo.fragments.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    Tracker.trackEvent("MainActivity的自定义追踪事件", null)

    // 简单的Fragment
    // 可以显示/隐藏的Fragment
    // 内嵌子Fragment的Fragment
    // 内嵌子Fragment并且可显隐的Fragment
    // ViewPager+FragmentStatePagerAdapter
    // ViewPager+FragmentStatePagerAdapter+内嵌
    val adapter = FragmentsAdapter(
        listOf(
            Demo("简单的Fragment", SimpleFragment::class.java.name, buildArgs(false, false)),
            Demo("内嵌的Fragment", SimpleFragment::class.java.name, buildArgs(false, true)),
            Demo("内嵌可显隐的Fragment", SimpleFragment::class.java.name, buildArgs(true, true)),
            Demo("ViewPager+Fragment", ViewPagerFragment::class.java.name, buildArgs(false, false)),
            Demo("ViewPager+Fragment+内嵌Fragment", ViewPagerFragment::class.java.name,
                buildArgs(false, true)),
            Demo("AdapterView点击事件Fragment",
                AdapterViewFragment::class.java.name, buildArgs(false, false)),
            Demo("模拟登录及渠道号",
                MockLoginFragment::class.java.name, buildArgs(false, false)),
            Demo("ButterKnife点击事件测试",
                ButterKnifeFragment::class.java.name, buildArgs(false, false)),
            Demo("Rx-Binding事件测试",
                RxBindingFragment::class.java.name, buildArgs(false, false))
        )
    )

    rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    rv.adapter = adapter

    title = getTrackName(this)
  }

  override fun getTrackName(context: Context) = "主Activity"

  override fun getTrackProperties(context: Context): Map<String, Any?>? = null

  private fun buildArgs(isVisibilityEnable: Boolean, isChildrenEnable: Boolean): Bundle {
    val args = Bundle()
    args.putBoolean("visibility_enable", isVisibilityEnable)
    args.putBoolean("children_enable", isChildrenEnable)
    return args
  }

  private inner class FragmentsAdapter(
      private val demos: List<Demo>) : RecyclerView.Adapter<FragmentsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FragmentsHolder {
      val itemView = LayoutInflater.from(parent.context).inflate(
          android.R.layout.simple_list_item_1,
          parent,
          false)
      return FragmentsHolder(itemView)
    }

    override fun onBindViewHolder(holder: FragmentsHolder, position: Int) {
      val item = getItem(position)
      holder.textView?.text = item.desc
      holder.itemView?.tag = item
      holder.itemView?.setOnClickListener {
        startFragment(item.name, item.args)
        if (position % 2 == 0) {// 在position=0,2,4...时忽略点击事件
          Tracker.ignoreView(holder.itemView)
        }
      }
    }

    override fun getItemCount(): Int = this.demos.size

    fun getItem(position: Int) = this.demos[position]
  }

  private class FragmentsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textView: TextView? = itemView.findViewById(android.R.id.text1)

  }
}


