package com.foolchen.lib.tracker.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.foolchen.lib.tracker.lifecycle.ITrackerIgnore

/**
 * 用于显示Fragment的Activity，本身不应该被统计
 * @author chenchong
 * 2017/11/4
 * 下午5:00
 */
class FragmentContainerActivity : AppCompatActivity(), ITrackerIgnore {

  override fun onCreate(savedInstanceState: Bundle?) {
    if (intent.extras?.getBoolean("useToolBar") == true) {
      setTheme(R.style.AppTheme_NoActionBar)
    }
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_fragment_container)

    val fragment = createFragment()
    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment,
        fragment.javaClass.simpleName)
        .commit()
  }

  override fun isIgnored() = true
}