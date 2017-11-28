package com.foolchen.lib.tracker.data

/**
 * 网络类型枚举
 * @author chenchong
 * 2017/11/28
 * 下午5:11
 */
internal enum class NetworkType() {
  /** 未知类型  */
  UNKNOWN {
    override fun desc(): String = "未知"
  },
  /** wifi  */
  WIFI {
    override fun desc(): String = "WIFI"
  },
  /** 2G  */
  G2 {
    override fun desc(): String = "2G"
  },
  /** 3G  */
  G3 {
    override fun desc(): String = "3G"
  },
  /** 4G  */
  G4 {
    override fun desc(): String = "4G"
  },
  /** 暂未处理的类型  */
  NO_DEAL {
    override fun desc(): String = "未知"
  },
  /** 断网了  */
  NO_NET {
    override fun desc(): String = "无网络"
  },
  /** 飞行模式  */
  AIR_MODE {
    override fun desc(): String = "飞行模式"
  };

  abstract fun desc(): String
}