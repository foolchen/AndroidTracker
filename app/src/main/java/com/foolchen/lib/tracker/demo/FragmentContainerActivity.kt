package com.foolchen.lib.tracker.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.foolchen.lib.tracker.lifecycle.IFragments

/**
 * 用于显示Fragment的Activity，本身不应该被统计
 * @author chenchong
 * 2017/11/4
 * 下午5:00
 */
class FragmentContainerActivity : AppCompatActivity(), IFragments {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_fragment_container)

    val fragment = createFragment()
    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment,
        fragment.javaClass.simpleName)
        .commit()
  }

  override fun hasChildFragments() = true
}