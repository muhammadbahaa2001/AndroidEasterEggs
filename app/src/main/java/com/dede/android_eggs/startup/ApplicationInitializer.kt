package com.dede.android_eggs.startup

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.dede.android_eggs.settings.SettingsPrefs
import com.dede.android_eggs.util.ActivityActionDispatcher
import com.dede.basic.GlobalContext

class ApplicationInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val application = context.applicationContext as Application
        SettingsPrefs.apply(application)
        ActivityActionDispatcher.register(application)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        ReflectionInitializer::class.java,
        GlobalContext.Initializer::class.java
    )
}