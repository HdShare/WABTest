package me.hd.wabtest.core.expt

import me.hd.wabtest.hook.HostData
import me.hd.wabtest.hook.wrapper.ConfigWrapper
import me.hd.wabtest.hook.wrapper.MMKVWrapper
import me.hd.wabtest.util.Base64Util
import me.hd.wabtest.util.JsonUtil
import java.io.File

object ExptManager {
    private val uin by lazy { ConfigWrapper.get("system_config_prefs").getUin() }
    private val appKeyName by lazy { "${uin}_WxExptAppKeyMmkv" }
    private val appKeyMmkv by lazy { MMKVWrapper.get(appKeyName) }
    private val appIdName by lazy { "${uin}_WxExptAppIdMmkv" }
    private val appIdMmkv by lazy { MMKVWrapper.get(appIdName) }

    private const val FAKE_EXPT_ID = 99999

    fun delMmkvFiles(): Boolean {
        val rootDir = "${HostData.appContext.filesDir.absolutePath}/mmkv"
        val appKeyFile = File(rootDir, appKeyName)
        val appIdFile = File(rootDir, appIdName)
        return appKeyFile.delete() && appIdFile.delete()
    }

    fun getArgValue(exptKey: String): String? {
        return if (appKeyMmkv.containsKey(exptKey)) {
            val exptId = appKeyMmkv.getInt(exptKey, FAKE_EXPT_ID)
            val itemStr = if (exptId != FAKE_EXPT_ID) { // 读取 下发
                val defStr = JsonUtil.toJson(ExptItem(exptId))
                appIdMmkv.getString(exptId.toString(), defStr)
            } else { // 读取 伪造
                val defStr = JsonUtil.toJson(ExptItem(FAKE_EXPT_ID))
                appIdMmkv.getString(FAKE_EXPT_ID.toString(), defStr)
            }
            val itemObj = JsonUtil.fromJson<ExptItem>(itemStr)
            val arg = itemObj.args.firstOrNull { arg -> arg.key == exptKey }
            if (arg != null) {
                Base64Util.decode(arg.value)
            } else null
        } else null
    }

    fun putArgValue(exptKey: String, argValue: String) {
        val exptId = appKeyMmkv.getInt(exptKey, FAKE_EXPT_ID)
        val defStr = JsonUtil.toJson(ExptItem(exptId))
        val itemStr = appIdMmkv.getString(exptId.toString(), defStr)
        val itemObj = JsonUtil.fromJson<ExptItem>(itemStr)
        itemObj.args.apply {
            val arg = firstOrNull { arg -> arg.key == exptKey }
            if (arg != null) {
                arg.value = Base64Util.encode(argValue) // 修改 下发或伪造
            } else {
                add(ExptArg(exptKey, Base64Util.encode(argValue))) // 追加 伪造
            }
        }
        appIdMmkv.putString(exptId.toString(), JsonUtil.toJson(itemObj))
        if (exptId == FAKE_EXPT_ID && !appKeyMmkv.containsKey(exptKey)) {
            appKeyMmkv.putInt(exptKey, FAKE_EXPT_ID) // 写入 伪造
        }
    }
}
