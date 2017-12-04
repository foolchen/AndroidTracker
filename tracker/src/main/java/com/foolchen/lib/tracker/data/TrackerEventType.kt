package com.foolchen.lib.tracker.data

import android.support.annotation.StringDef

const val VIEW_SCREEN = "AppViewScreen"
const val CLICK = "AppClick"
const val APP_START = "AppStart"
const val APP_END = "AppEnd"

@StringDef(VIEW_SCREEN, CLICK, APP_START, APP_END)
@Retention(AnnotationRetention.SOURCE) internal annotation class EventType
