package com.foolchen.lib.tracker.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * 简单的Activity，内部没有任何Fragment
 * @author chenchong
 * 2017/11/4
 * 下午4:20
 */
class SimpleActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    listeners()
  }
}