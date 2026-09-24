package io.github.hdshare.wabtest.hook

import com.highcapable.yukihookapi.annotation.xposed.YukiHookXposed82Entry
import com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule

@YukiHookXposed82Entry(
    entryClassName = "Entry",
)
object LegacyRuntimeEntry : YukiHookXposedModule by HookEntry
