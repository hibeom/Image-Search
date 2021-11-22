package com.pinkcloud.imagesearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.pinkcloud.imagesearch.ui.main.MainFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ImageSearch)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
        suppressDraw()
    }

    private fun suppressDraw() {
        var isReady = false
        lifecycleScope.launch(Dispatchers.IO) {
            Glide.get(application).clearDiskCache()
            isReady = true
        }

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener (
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (isReady) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )
    }
}