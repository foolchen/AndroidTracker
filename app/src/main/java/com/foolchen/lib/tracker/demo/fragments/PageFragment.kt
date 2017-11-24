package com.foolchen.lib.tracker.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foolchen.lib.tracker.demo.R
import kotlinx.android.synthetic.main.fragment_simple.*

/**
 * ViewPagerFragment的Item
 * @author chenchong
 * 2017/11/23
 * 下午5:34
 */
class PageFragment : BaseFragment() {

  private var position = 0
  private var hasChildren = false

  companion object {
    private val KEY_POSITION = "position"
    private val KEY_HAS_CHILDREN = "has_children"

    fun newInstance(position: Int, hasChildren: Boolean): PageFragment {
      val f = PageFragment()
      val args = Bundle()
      args.putInt(KEY_POSITION, position)
      args.putBoolean(KEY_HAS_CHILDREN, hasChildren)
      f.arguments = args
      return f
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    position = arguments?.getInt(KEY_POSITION) ?: 0
    hasChildren = arguments?.getBoolean(KEY_HAS_CHILDREN) ?: false
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_simple,
      container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    tv_desc.text = getString(R.string.text_vp_item_desc_mask, position)

    if (hasChildFragments()) {
      fragment_container.visibility = View.VISIBLE
      childFragmentManager.beginTransaction().replace(R.id.fragment_container,
          NestedFragment.newInstance(
              getString(R.string.text_vp_nested_fragment_mask, position))).commit()
    } else {
      fragment_container.visibility = View.GONE
    }
  }

  override fun hasChildFragments(): Boolean = hasChildren

  override fun getTrackName(): String? = tv_desc.text.toString()
}