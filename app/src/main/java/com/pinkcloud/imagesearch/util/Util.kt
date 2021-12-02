package com.pinkcloud.imagesearch.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

private const val ROUGH_THUMBNAIL_SIZE = 120

fun hideKeyboard(context: Context, v: View) {
    (context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
        hideSoftInputFromWindow(v.windowToken, 0)
    }
}

fun calculateSpanCount(activity: Activity): Int {
    val widthPixel = activity.resources.displayMetrics.widthPixels
    val density = activity.resources.displayMetrics.density
    val widthDp = (widthPixel / density).roundToInt()
    return widthDp / ROUGH_THUMBNAIL_SIZE
}