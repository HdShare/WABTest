package io.github.wabtest.core.test

import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import io.github.wabtest.hook.HostData.appContext
import io.github.wabtest.util.JsonUtil

object TestManager {
    private const val JSON_FILE = "test.json"

    fun getList(): List<TestItem> {
        val ctx = appContext.also { it.injectModuleAppResources() }
        val jsonString = ctx.assets.open(JSON_FILE).bufferedReader().use { it.readText() }
        return JsonUtil.fromJson<List<TestItem>>(jsonString)
    }

    fun getOptionAlias(options: List<ConfigOption>, value: String?): String? {
        return value?.let {
            options.firstOrNull { it.value == value }?.alias ?: "未知$value"
        }
    }
}
