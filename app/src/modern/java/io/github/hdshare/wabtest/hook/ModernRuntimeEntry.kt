package io.github.hdshare.wabtest.hook

import com.highcapable.yukihookapi.annotation.xposed.YukiHookLibXposedEntry
import com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule

@YukiHookLibXposedEntry(
    entryClassName = "Entry",
    targetApiVersion = 102,
    scope = ["com.tencent.mm"],
    staticScope = true,
)
object ModernRuntimeEntry : YukiHookXposedModule by HookEntry
