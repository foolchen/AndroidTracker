package com.foolchen.lib.tracker.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.foolchen.lib.tracker.Tracker
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

  override fun onCreate(db: SQLiteDatabase) {
    db.createTable("data", true,
        "_id" to INTEGER + PRIMARY_KEY + UNIQUE + AUTOINCREMENT,
        "data" to TEXT,
        "time" to INTEGER)
  }

  override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
  }
}

internal val Context.database: TrackerDbOpenHelper
  get() = TrackerDbOpenHelper.getInstance(Tracker.trackContext!!.getApplicationContext())