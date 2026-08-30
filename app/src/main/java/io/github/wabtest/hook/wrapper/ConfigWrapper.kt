package io.github.wabtest.hook.wrapper

import android.content.Context
import android.content.SharedPreferences
import io.github.wabtest.hook.HostData.appContext

class ConfigWrapper private constructor(private val pref: SharedPreferences) {

    companion object {
        private fun getSharedPreferences(name: String): SharedPreferences {
            return appContext.getSharedPreferences(name, Context.MODE_PRIVATE)
        }

        fun get(name: String): ConfigWrapper {
            return ConfigWrapper(getSharedPreferences(name))
        }
    }

    fun getUin(): Int {
        return pref.getInt("default_uin", 0)
    }
}
