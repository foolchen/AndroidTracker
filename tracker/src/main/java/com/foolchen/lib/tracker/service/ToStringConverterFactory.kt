package com.foolchen.lib.tracker.service

import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type


/**
 * @author chenchong
 * 2017/12/6
 * 下午4:40
 */
class ToStringConverterFactory : Converter.Factory() {
  private val MEDIA_TYPE = MediaType.parse("text/plain")

  override fun responseBodyConverter(type: Type, annotations: Array<Annotation>,
      retrofit: Retrofit): Converter<ResponseBody, *>? {
    return if (String::class.java == type) {
      Converter<ResponseBody, String> { value -> value.string() }
    } else null
  }

  override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<out Annotation>?,
      methodAnnotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody>? {
    return if (String::class.java == type) {
      Converter<String, RequestBody> { value -> RequestBody.create(MEDIA_TYPE, value) }
    } else null
  }
}