package com.foolchen.lib.tracker.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.foolchen.lib.tracker.demo.fragments.DemoFragment
import com.foolchen.lib.tracker.demo.fragments.FragmentInFragment
import com.foolchen.lib.tracker.demo.fragments.ViewPagerFragment
import kotlinx.android.synthetic.main.activity_main.*

fun Activity.listeners() {
  btn_simple_activity?.setOnClickListener {
    startActivity(Intent(this, SimpleActivity::class.java))
  }
  btn_simple_fragment?.setOnClickListener {
    startFragment(DemoFragment::class.java.name)
  }
  btn_nested_fragment?.setOnClickListener {
    startFragment(FragmentInFragment::class.java.name)
  }
  btn_fragments_in_vp?.setOnClickListener {
    startFragment(ViewPagerFragment::class.java.name)
  }
}

fun Fragment.listeners() {
  btn_simple_activity?.setOnClickListener {
    startActivity(Intent(this.context, SimpleActivity::class.java))
  }
  btn_simple_fragment?.setOnClickListener {
    startFragment(this::class.java.name)
  }
  btn_nested_fragment?.setOnClickListener {
    startFragment(FragmentInFragment::class.java.name)
  }
  btn_fragments_in_vp?.setOnClickListener {
    startFragment(ViewPagerFragment::class.java.name)
  }
}

fun Activity.startFragment(fragment: String, args: Bundle? = null) {
  val intent = Intent(this, FragmentContainerActivity::class.java)
  intent.putExtra("fragment", fragment)
  if (args != null) {
    intent.putExtras(args)
  }
  startActivity(intent)
}

fun Fragment.startFragment(fragment: String, args: Bundle? = null) {
  val intent = Intent(context, FragmentContainerActivity::class.java)
  intent.putExtra("fragment", fragment)
  if (args != null) {
    intent.putExtras(args)
  }
  startActivity(intent)
}

fun Activity.createFragment() = Fragment.instantiate(this, intent.getStringExtra("fragment"),
    intent.extras)!!