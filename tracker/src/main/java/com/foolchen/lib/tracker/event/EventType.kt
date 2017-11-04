package com.foolchen.lib.tracker.event

import android.support.annotation.StringDef

const val VIEW_SCREEN = "AppViewScreen"
const val CLICK = "AppClick"

@StringDef(VIEW_SCREEN, CLICK)
@Retention(AnnotationRetention.SOURCE) internal annotation class EventType
