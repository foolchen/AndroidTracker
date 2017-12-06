package com.foolchen.lib.tracker.service

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * 上报接口的定义
 * @author chenchong
 * 2017/12/6
 * 下午3:18
 */
interface TrackerAPIDef {

  /**
   * 上报数据接口
   *
   * @param path 上报数据的接口地址
   * @param projectName 要上报数据的项目名称
   * @param data 要上报的JSON数据
   * @param mode 上报数据的模式
   */
  @FormUrlEncoded
  @POST("{url}")
  fun report(@Path("url") path: String, @Field("project") projectName: String, @Field(
      "data") data: String, @Field("mode") mode: Int): Observable<Response<String>>
}