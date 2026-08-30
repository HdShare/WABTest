package io.github.wabtest.hook

import android.app.Application
import android.app.Instrumentation
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.YukiHookAPI.configs
import com.highcapable.yukihookapi.YukiHookAPI.encase
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import io.github.wabtest.BuildConfig
import io.github.wabtest.hook.hooker.SettingHooker

@InjectYukiHookWithXposed(entryClassName = "Entry")
object HookEntry : IYukiHookXposedInit {
    override fun onInit() = configs {
        debugLog { tag = BuildConfig.APP_NAME }
        isDebug = false
        isEnableDataChannel = false
    }

    override fun onHook() = encase {
        loadApp("com.tencent.mm") {
            Instrumentation::class.resolve().apply {
                firstMethod {
                    name = "callApplicationOnCreate"
                    parameters(Application::class)
                }.hook {
                    after {
                        val application = args(0).cast<Application>()!!
                        val context = application.baseContext
                        HostData.init(context)
                        withProcess(mainProcessName) {
                            loadHooker(SettingHooker)
                        }
                    }
                }
            }
        }
    }
}
