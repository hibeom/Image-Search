package com.pinkcloud.imagesearch.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.window.layout.WindowMetricsCalculator
import timber.log.Timber
import kotlin.math.roundToInt

private const val THUMBNAIL_SIZE_IN_DP = 120

fun hideKeyboard(context: Context, v: View) {
    (context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
        hideSoftInputFromWindow(v.windowToken, 0)
    }
}

fun calculateSpanCount(activity: Activity): Int {
    val widthPixel =
        WindowMetricsCalculator
            .getOrCreate()
            .computeCurrentWindowMetrics(activity)
            .bounds.let { rect ->
                rect.right - rect.left
            }
    val density = activity.resources.displayMetrics.density
    val widthDp = (widthPixel / density).roundToInt()
    return widthDp / THUMBNAIL_SIZE_IN_DP
}