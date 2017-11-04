package com.foolchen.lib.tracker.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.foolchen.lib.tracker.lifecycle.ITrack

class MainActivity : AppCompatActivity(), ITrack {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    listeners()
    title = getTrackName()
  }

  override fun getTrackName() = "主Activity"

  override fun getTrackProperties() = null
}
