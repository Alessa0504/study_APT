package com.example.study_apt

import android.app.Application
import com.example.apt.runtime.ActivityBuilder

/**
 * @Description:
 * @author zouji
 * @date 2023/1/28
 */
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        ActivityBuilder.INSTANCE.init(this)
    }
}