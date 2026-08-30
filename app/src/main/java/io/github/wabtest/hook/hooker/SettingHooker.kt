package io.github.wabtest.hook.hooker

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import io.github.wabtest.hook.HostData.toHostClazzOrNull
import io.github.wabtest.ui.SettingDialog

object SettingHooker : YukiBaseHooker() {
    private const val NEW_BASE_SETTING_CLASS = "com.tencent.mm.plugin.setting.ui.setting_new.base.BaseSettingPrefUI"
    private const val NEW_SETTING_CLASS = "com.tencent.mm.plugin.setting.ui.setting_new.MainSettingsUI"
    private const val OLD_SETTING_CLASS = "com.tencent.mm.plugin.setting.ui.setting.SettingsUI"

    private fun Activity.addTextOptionMenu(itemId: Int, text: String, itemClick: MenuItem.OnMenuItemClickListener) {
        asResolver().firstMethod {
            name = "addTextOptionMenu"
            parameters(Int::class, String::class, MenuItem.OnMenuItemClickListener::class)
            superclass()
        }.invoke(itemId, text, itemClick)
    }

    override fun onHook() {
        NEW_BASE_SETTING_CLASS.toHostClazzOrNull()?.resolve()?.apply {
            firstMethod {
                name = "onCreate"
                parameters(Bundle::class)
            }.hook {
                after {
                    if (instanceClass?.name != NEW_SETTING_CLASS) return@after
                    val activity = instance<Activity>()
                    activity.addTextOptionMenu(itemId = hashCode(), text = "AB") {
                        SettingDialog.show(activity)
                        true
                    }
                }
            }
        }
        OLD_SETTING_CLASS.toHostClazzOrNull()?.resolve()?.apply {
            firstMethod {
                name = "onCreate"
                parameters(Bundle::class)
            }.hook {
                after {
                    val activity = instance<Activity>()
                    activity.addTextOptionMenu(itemId = hashCode(), text = "AB") {
                        SettingDialog.show(activity)
                        true
                    }
                }
            }
        }
    }
}
