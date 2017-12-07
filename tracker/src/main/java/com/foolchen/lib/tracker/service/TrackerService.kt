package com.foolchen.lib.tracker.service

import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.data.TrackerEvent
import com.foolchen.lib.tracker.data.TrackerMode
import com.foolchen.lib.tracker.data.TrackerResult
import com.foolchen.lib.tracker.utils.GSON
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

/**
 * 用于网络请求
 * @author chenchong
 * 2017/11/4
 * 下午3:13
 */
object TrackerService {
  /** 每次上报数据的数量（每几条数据触发一次上报） */
  internal var mReportThreshold = 10
  private val mService = createRetrofit().create(TrackerAPIDef::class.java)
  /** 用于存储已触发的事件生成的JSON数据 */
  private val mEvents = ArrayList<TrackerEvent>()

  /**
   * 上报数据
   *
   * @param data 要上报的JSON数据
   */
  fun report(event: TrackerEvent) {
    Observable.create<List<TrackerEvent>> {
      mEvents.add(event)
      it.onNext(mEvents)
      it.onComplete()
    }
        .filter { it.size >= threshold() }
        .flatMap { events ->
          val data = ArrayList<Map<String, Any>>(events.size)
          events.mapTo(data) { it.build() }
          val reportJson = GSON.toJson(data) // 要上报的数据
          mService.report(Tracker.servicePath!!, Tracker.projectName!!, reportJson, mode())// 上报数据
              .map {
                // 将上传的事件与上传结果打包传递给下一步
                TrackerResult(events, it)
              }
        }
        .map {
          // 如果上报成功，则将数据从事件列表中移除
          if (it.response.code() == 200) {
            mEvents.removeAll(it.events)
          }
          // 如果未上报成功，则不处理这些事件，这些事件会在下次触发上报时再次尝试上传
          // 或者在应用切换到后台时保存到数据库并尝试上传
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe()
  }

  /** 根据枚举类型来计算上报接口时使用的模式 */
  private fun mode(): Int {
    return when (Tracker.mode) {
      TrackerMode.DEBUG_ONLY -> 1
      TrackerMode.DEBUG_TRACK -> 2
      else -> {
        3
      }
    }
  }

  /** 根据当前的上报模式计算触发上报的阈值 */
  private fun threshold(): Int {
    return when (Tracker.mode) {
      TrackerMode.DEBUG_ONLY -> 1
      TrackerMode.DEBUG_TRACK -> 1
      TrackerMode.RELEASE -> mReportThreshold
    }
  }

  private var mRetrofit: Retrofit? = null

  private fun createRetrofit(): Retrofit {
    if (Tracker.serviceHost.isNullOrBlank()) {
      throw RuntimeException("serviceHost未设置")
    }
    if (Tracker.servicePath.isNullOrBlank()) {
      throw RuntimeException("servicePath未设置")
    }
    if (Tracker.projectName.isNullOrBlank()) {
      throw RuntimeException("projectName未设置")
    }
    if (mRetrofit == null) {
      synchronized(this) {
        val builder = Retrofit.Builder()
        builder.baseUrl(Tracker.serviceHost!!)
        builder.client(createOkHttpClient())
        /*val gson = GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT,
            Modifier.STATIC)
            .create()*/
        mRetrofit = builder/*.addConverterFactory(GsonConverterFactory.create(gson))*/
            .addConverterFactory(ToStringConverterFactory())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
      }
    }
    return mRetrofit!!
  }

  private fun createOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder().connectTimeout(Tracker.timeoutDuration,
        TimeUnit.MILLISECONDS)
        .readTimeout(Tracker.timeoutDuration, TimeUnit.MILLISECONDS)
        .writeTimeout(Tracker.timeoutDuration, TimeUnit.MILLISECONDS).build()
  }
}