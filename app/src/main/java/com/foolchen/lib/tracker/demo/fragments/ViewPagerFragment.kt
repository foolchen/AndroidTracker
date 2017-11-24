package com.foolchen.lib.tracker.demo.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.*
import com.foolchen.lib.tracker.demo.R
import kotlinx.android.synthetic.main.fragment_view_pager.*

/**
 * ViewPager+Fragment的实现
 * @author chenchong
 * 2017/11/23
 * 下午5:24
 */
class ViewPagerFragment : BaseFragment() {
  private var nestedFragment: NestedFragment? = null
  private var isChildrenEnable = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    isChildrenEnable = arguments?.getBoolean("children_enable") ?: false
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? =
      inflater.inflate(R.layout.fragment_view_pager, container, false)


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    vp.adapter = VpAdapter(childFragmentManager, isChildrenEnable)
  }

  override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
    inflater?.inflate(R.menu.menu_fragments, menu)
  }


  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.action_show -> {

        return true
      }
      R.id.action_hide -> {
        return true
      }
    }

    return super.onOptionsItemSelected(item)
  }

  override fun hasChildFragments(): Boolean = true
}

private class VpAdapter : FragmentStatePagerAdapter {
  private val isChildrenEnable: Boolean

  constructor(fm: FragmentManager, isChildrenEnable: Boolean) : super(fm) {
    this.isChildrenEnable = isChildrenEnable
  }

  override fun getCount(): Int = 20


  override fun getItem(position: Int): Fragment = PageFragment.newInstance(position,
      isChildrenEnable)
}

