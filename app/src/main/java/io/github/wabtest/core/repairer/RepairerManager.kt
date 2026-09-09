package io.github.wabtest.core.repairer

import io.github.wabtest.hook.wrapper.MMKVWrapper

object RepairerManager {
    private val repairerMmkv by lazy { MMKVWrapper.get("Repairer") }

    fun getValue(key: String): String {
        val parts = key.split("_")
        val type = parts[parts.size - 1]
        return when (type) {
            "Int" -> repairerMmkv.getInt(key, 0).toString()
            else -> ""
        }
    }

    fun putValue(key: String, value: String) {
        val parts = key.split("_")
        val type = parts[parts.size - 1]
        when (type) {
            "Int" -> repairerMmkv.putInt(key, value.toInt()).toString()
        }
    }
}
