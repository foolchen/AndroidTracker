package com.foolchen.lib.tracker.demo.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foolchen.lib.tracker.demo.R
import kotlinx.android.synthetic.main.fragment_nested.*

/**
 * 内部嵌套的Fragment
 * @author chenchong
 * 2017/11/23
 * 下午4:35
 */
class NestedFragment : BaseFragment() {

  private var desc: String? = null

  companion object {
    private val KEY_DESC = "desc"

    fun newInstance(desc: String? = null): NestedFragment {
      val f = NestedFragment()
      val args = Bundle()
      args.putString(KEY_DESC, desc)
      f.arguments = args
      return f
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    desc = arguments?.getString(KEY_DESC)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_nested, container,
      false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (!desc.isNullOrEmpty()) {
      tv_desc.text = desc
    }
  }

  override fun getTrackName(context: Context): String? = tv_desc?.text.toString()
}