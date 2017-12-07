package com.foolchen.lib.tracker.data

import retrofit2.Response

/**
 * 用于存储上报接口的结果及上报的数据
 *
 * 用于上传结果的回调，内部含有上报的时间，便于在回调时对数据进行移除或者持久化处理
 *
 * @author chenchong
 * 2017/12/7
 * 上午11:55
 */
data class TrackerResult(val events: List<TrackerEvent>, val response: Response<String>)