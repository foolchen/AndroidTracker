package com.foolchen.lib.tracker.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment

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