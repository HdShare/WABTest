package io.github.hdshare.wabtest.hook

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
            withApplicationReady { application ->
                HostData.init(application.baseContext)
                withProcess(mainProcessName) {
                    loadHooker(SettingHooker)
                }
            }
        }
    }
}
