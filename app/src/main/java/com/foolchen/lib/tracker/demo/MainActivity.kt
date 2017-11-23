package com.foolchen.lib.tracker.demo

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.foolchen.lib.tracker.demo.data.Demo
import com.foolchen.lib.tracker.demo.fragments.SimpleFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

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
            Demo("内嵌可显隐的Fragment", SimpleFragment::class.java.name, buildArgs(true, true))
        )
    )

    rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    rv.adapter = adapter

    title = getTrackName()
  }

  override fun getTrackName() = "主Activity"

  override fun getTrackProperties() = null

  private fun buildArgs(isVisibilityEnable: Boolean, isChildrenEnable: Boolean): Bundle {
    val args = Bundle()
    args.putBoolean("visibility_enable", isVisibilityEnable)
    args.putBoolean("children_enable", isChildrenEnable)
    return args
  }

  private inner class FragmentsAdapter : RecyclerView.Adapter<FragmentsHolder> {
    private val demos: List<Demo>

    constructor(demos: List<Demo>) {
      this.demos = demos
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FragmentsHolder {
      val itemView = LayoutInflater.from(parent.context).inflate(
          android.R.layout.simple_list_item_1,
          parent,
          false)
      itemView.setOnClickListener {
        val tag = itemView.tag
        if (tag is Demo) {
          startFragment(tag.name, tag.args)

        }
      }
      return FragmentsHolder(itemView)
    }

    override fun onBindViewHolder(holder: FragmentsHolder?, position: Int) {
      val item = getItem(position)
      holder?.textView?.text = item.desc
      holder?.itemView?.tag = item
    }

    override fun getItemCount(): Int = this.demos.size

    fun getItem(position: Int) = this.demos[position]
  }

  private class FragmentsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textView: TextView? = itemView.findViewById(android.R.id.text1)

  }
}


