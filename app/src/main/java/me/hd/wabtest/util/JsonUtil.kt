package me.hd.wabtest.util

import kotlinx.serialization.json.Json

object JsonUtil {
    val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    inline fun <reified T> toJson(value: T): String {
        return json.encodeToString(value)
    }

    inline fun <reified T> fromJson(value: String): T {
        return json.decodeFromString(value)
    }
}
