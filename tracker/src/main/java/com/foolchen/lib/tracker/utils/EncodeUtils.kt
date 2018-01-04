package com.foolchen.lib.tracker.utils

import android.util.Base64
import java.net.URLEncoder


fun String.encodeBASE64() = String(
    Base64.encode(toByteArray(Charsets.UTF_8), Base64.NO_WRAP), Charsets.UTF_8)

fun String.urlEncode(): String = URLEncoder.encode(this, "UTF-8")