package com.foolchen.lib.tracker.demo

import android.app.Activity
import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*

fun Activity.listeners() {
  btn_simple_activity.setOnClickListener {
    startActivity(Intent(this, SimpleActivity::class.java))
  }
}