package io.github.wabtest.core.test

import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import io.github.wabtest.core.expt.ExptManager
import io.github.wabtest.hook.HostData.appContext
import io.github.wabtest.util.JsonUtil

object TestManager {
    private const val JSON_FILE = "test.json"

    fun getList(): List<TestItem> {
        val ctx = appContext.also { it.injectModuleAppResources() }
        val jsonString = ctx.assets.open(JSON_FILE).bufferedReader().use { it.readText() }
        return JsonUtil.fromJson<List<TestItem>>(jsonString)
    }

    fun getOptionsAlias(options: List<ConfigOption>, key: String): String {
        return ExptManager.getArgValue(key)?.let { value ->
            options.firstOrNull { it.value == value }?.alias ?: "未知$value"
        } ?: "未下发"
    }
}
