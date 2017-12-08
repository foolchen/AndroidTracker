package com.foolchen.lib.tracker.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
 * 数据库工具
 * @author chenchong
 * 2017/12/6
 * 下午6:39
 */
class TrackerDbOpenHelper(context: Context) : ManagedSQLiteOpenHelper(context, "android_tracker",
    null, 1) {

  companion object {
    private var instance: TrackerDbOpenHelper? = null

    @Synchronized
    fun getInstance(context: Context): TrackerDbOpenHelper {
      if (instance == null) {
        instance = TrackerDbOpenHelper(context)
      }
      return instance!!
    }
  }

  override fun onCreate(db: SQLiteDatabase?) {
    db?.createTable(EventContract.TABLE_NAME, true,
        EventContract.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
        EventContract.DATA to TEXT,
        EventContract.TIME to INTEGER)
  }

  override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    // 此处对表结构进行更新
  }
}

internal val Context.database: TrackerDbOpenHelper
  get() = TrackerDbOpenHelper.getInstance(this)