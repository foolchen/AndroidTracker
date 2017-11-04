package com.foolchen.lib.tracker.demo.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.foolchen.lib.tracker.demo.R
import com.foolchen.lib.tracker.lifecycle.IFragments

/**
 * 嵌套Fragment
 * @author chenchong
 * 2017/11/4
 * 下午5:31
 */
class FragmentInFragment : DemoFragment(), IFragments {
  override fun hasChildFragments() = true

  override fun getTrackName(): String = getString(R.string.text_has_nested_fragment)

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val fragment = Fragment.instantiate(context, NestedFragment::class.java.name, null)
    childFragmentManager.beginTransaction().replace(R.id.child_fragment_container,
        fragment).commit()
  }
}