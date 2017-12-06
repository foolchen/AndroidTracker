package com.foolchen.lib.tracker.service

import android.util.Log
import com.foolchen.lib.tracker.Tracker
import com.foolchen.lib.tracker.data.TrackerMode
import com.google.gson.GsonBuilder
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.reflect.Modifier
import java.util.concurrent.TimeUnit

/**
 * 用于网络请求
 * @author chenchong
 * 2017/11/4
 * 下午3:13
 */
object TrackerService {
  private val mService = createRetrofit().create(TrackerAPIDef::class.java)

  /**
   * 上报数据
   *
   * @param data 要上报的JSON数据
   */
  fun report(data: String) {
    mService.report(Tracker.servicePath!!, Tracker.projectName!!, data, mode())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<Response<String>> {
      override fun onSubscribe(d: Disposable) {
      }

      override fun onError(e: Throwable) {
        if (Tracker.mode != TrackerMode.RELEASE) {
          Log.e("TrackerService", "error = ${e.toString()}")
        }
      }

      override fun onNext(response: Response<String>) {
        if (Tracker.mode != TrackerMode.RELEASE) {
          Log.i("TrackerService", "result = $response")
        }
        if (response.code() == 200) {
          // 接口请求成功
        }
      }

      override fun onComplete() {
      }
    })
  }

  private fun mode(): Int {
    return when (Tracker.mode) {
      TrackerMode.DEBUG_ONLY -> 1
      TrackerMode.DEBUG_TRACK -> 2
      else -> {
        3
      }
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
        val gson = GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT,
            Modifier.STATIC)
            .create()
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