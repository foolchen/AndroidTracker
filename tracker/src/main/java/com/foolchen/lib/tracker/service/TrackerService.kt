package com.foolchen.lib.tracker.service

import android.util.Log
import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.data.TrackerEvent
import com.foolchen.lib.tracker.data.TrackerMode
import com.foolchen.lib.tracker.utils.GSON
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Response
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
  private val TAG = "TrackerService"
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
    if (Tracker.mode == TrackerMode.RELEASE) {
      // 如果为release模式，则此处需要考虑同步、批量上传等处理
      // 首先将事件添加到事件列表中
      mEvents.add(event)
      // 然后判断事件数量是否达到了上传的阈值
      if (mEvents.size >= threshold()) {
        // 如果达到了阈值，则进行后续操作
        val reportEvents = prepareReportEvents()// 准备要上报的事件
        mService.report(Tracker.servicePath!!, Tracker.projectName!!,
            prepareReportJson(reportEvents), mode()).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(object : IgnoreObserver() {
              override fun onNext(t: Response<String>) {
                super.onNext(t)
                if (t.code() == 200) {
                  // 接口请求成功
                  // 则此时不做任何处理
                } else {
                  // 接口请求失败
                  // 则此时将上报失败的事件添加回待上报的事件列表中
                  addEvents(reportEvents)
                }
              }

              override fun onError(e: Throwable) {
                super.onError(e)
                // 接口请求失败
                // 则此时将上报失败的事件添加回待上报的事件列表中
                addEvents(reportEvents)
              }
            })
      }
    } else if (Tracker.mode == TrackerMode.DEBUG_TRACK) {
      // 如果为debug&track模式，则直接上传数据，并且不关注失败
      mService.report(Tracker.servicePath!!, Tracker.projectName!!,
          GSON.toJson(listOf(event.build())),
          mode()).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(
          IgnoreObserver())
    }
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

  /**
   * 准备上报用的事件
   *
   * 该方法会将要上传的事件从事件列表中暂时移除，并生成一个新的事件列表用于上报。该操作为同步操作，防止发生错误
   */
  @Synchronized
  private fun prepareReportEvents(): List<TrackerEvent> {
    val reportEvents = ArrayList<TrackerEvent>(mEvents)
    mEvents.removeAll(reportEvents)
    return reportEvents
  }

  /** 将一批事件添加到事件列表中，该操作为同步操作 */
  @Synchronized
  private fun addEvents(events: List<TrackerEvent>) {
    mEvents.addAll(events)
    // 在添加到列表中后，根据时间对所有的时间进行排序
    mEvents.sortBy { it.time }
  }


  /** 准备上传使用的JSON数据 */
  private fun prepareReportJson(events: List<TrackerEvent>): String {
    val array = ArrayList<Map<String, Any>>(events.size)
    events.mapTo(array) { it.build() }
    return GSON.toJson(array)
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

  /** [Observer]的实现类，默认实现忽略所有回调。如有需要可覆写对应方法 */
  private open class IgnoreObserver : Observer<Response<String>> {
    override fun onError(e: Throwable) {
      if (Tracker.mode != TrackerMode.RELEASE) {
        Log.w(TAG, "onError() , ex = $e")
      }
    }

    override fun onComplete() {
    }

    override fun onNext(t: Response<String>) {
      if (Tracker.mode != TrackerMode.RELEASE) {
        Log.d(TAG, "onNext() , response = $t")
      }
    }

    override fun onSubscribe(d: Disposable) {
    }

  }
}