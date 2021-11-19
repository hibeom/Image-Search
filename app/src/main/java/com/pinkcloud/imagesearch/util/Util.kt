package com.pinkcloud.imagesearch.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

fun hideKeyboard(context: Context, v: View) {
    (context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
        hideSoftInputFromWindow(v.windowToken, 0)
    }
}