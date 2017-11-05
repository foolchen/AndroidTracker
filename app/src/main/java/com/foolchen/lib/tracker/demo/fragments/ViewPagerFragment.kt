package com.foolchen.lib.tracker.demo.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.foolchen.lib.tracker.demo.R
import com.foolchen.lib.tracker.lifecycle.IFragmentVisible
import com.foolchen.lib.tracker.lifecycle.IFragmentVisibleHlper
import com.foolchen.lib.tracker.lifecycle.IFragments
import com.foolchen.lib.tracker.lifecycle.ITrack

/**
 * 含有ViewPager的Fragment
 * @author chenchong
 * 2017/11/5 0005
 * 17:08
 */
class ViewPagerFragment : Fragment(), IFragments {

  private val colors = arrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.BLUE,
      Color.YELLOW, Color.MAGENTA)
  private var vp: ViewPager? = null

  override fun hasChildFragments() = true

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    vp = ViewPager(context)
    vp?.id = R.id.view_pager
    vp!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT)
    return vp
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    vp!!.adapter = NavAdapter(childFragmentManager)
  }

  inner class NavAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
      val f = ItemFragment()
      val arguments = Bundle()
      arguments.putInt("position", position)
      arguments.putInt("color", colors[position])
      f.arguments = arguments
      return f
    }

    override fun getCount() = colors.size
  }

  class ItemFragment : Fragment(), ITrack, IFragmentVisibleHlper {

    private var position = 0
    private var color = Color.WHITE

    private var iFragmentVisible: IFragmentVisible? = null

    override fun getTrackName() = "Fragment : $position"

    override fun getTrackProperties() = null

    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      color = arguments.getInt("color")
      position = arguments.getInt("position")
    }

    override fun onStart() {
      super.onStart()
    }

    override fun onResume() {
      super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
      val text = TextView(context)
      text.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT)
      text.text = getTrackName()
      text.gravity = Gravity.CENTER
      text.setBackgroundColor(color)
      return text
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
      super.setUserVisibleHint(isVisibleToUser)
      if (isVisibleToUser) iFragmentVisible?.onFragmentVisible(
          this) else iFragmentVisible?.onFragmentHide(this)
    }

    override fun unregisterIFragmentVisible(it: IFragmentVisible) {
      iFragmentVisible = null
    }

    override fun registerIFragmentVisible(it: IFragmentVisible) {
      iFragmentVisible = it
    }

  }
}