package com.foolchen.lib.tracker.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.demo.R
import kotlinx.android.synthetic.main.fragment_mock_login.*

/**
 * 该Fragment用于模拟登录（用于设置统计的用户id等）
 * @author chenchong
 * 2017/11/29
 * 上午9:16
 */
class MockLoginFragment : BaseFragment() {
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_mock_login, container,
      false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    btn_apply_user_id.setOnClickListener {
      val text = tie_user_id?.text?.toString() ?: ""
      if (text.isBlank()) {
        Tracker.logout()
      } else {
        Tracker.login(text)
      }
    }

    btn_apply_channel_id.setOnClickListener {
      val text = tie_channel_id?.text?.toString() ?: ""
      Tracker.setChannelId(text)
    }
  }
}