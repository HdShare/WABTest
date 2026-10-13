package io.github.hdshare.wabtest.hook

import com.highcapable.yukihookapi.annotation.xposed.YukiHookLibXposedEntry
import com.highcapable.yukihookapi.annotation.xposed.YukiHookLibXposedEntry.HotReload
import com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule

@YukiHookLibXposedEntry(
    entryClassName = "Entry",
    minApiVersion = 101,
    targetApiVersion = 102,
    scope = ["com.tencent.mm"],
    staticScope = true,
    hotReload = HotReload.AUTO,
)
object ModernRuntimeEntry : YukiHookXposedModule by HookEntry
