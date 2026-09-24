package io.github.hdshare.wabtest.hook

import android.app.Application
import android.app.Instrumentation
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.YukiHook.configure
import com.highcapable.yukihookapi.YukiHook.encase
import com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule
import io.github.hdshare.wabtest.BuildConfig
import io.github.hdshare.wabtest.hook.hooker.SettingHooker

object HookEntry : YukiHookXposedModule {
    override fun onInit() = configure {
        logging { tag = BuildConfig.APP_NAME }
        debug = false
        dataChannel = false
    }

    override fun onHook() = encase {
        loadApp("com.tencent.mm") {
            Instrumentation::class.resolve().apply {
                firstMethod {
                    name = "callApplicationOnCreate"
                    parameters(Application::class)
                }.hook {
                    after {
                        val application = arg(0).get<Application>()!!
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
