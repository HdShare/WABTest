package io.github.hdshare.wabtest.core.util

import io.github.hdshare.wabtest.core.expt.ExptManager
import io.github.hdshare.wabtest.core.repairer.RepairerManager
import io.github.hdshare.wabtest.core.test.ConfigItem
import io.github.hdshare.wabtest.core.test.ConfigType

object ConfigValueManager {
    fun getValue(config: ConfigItem): String? {
        return when (config.type) {
            ConfigType.EXPT -> ExptManager.getValue(config.key)
            ConfigType.REPAIRER -> RepairerManager.getValue(config.key)
        }
    }

    fun putValue(config: ConfigItem, value: String) {
        when (config.type) {
            ConfigType.EXPT -> ExptManager.putValue(config.key, value)
            ConfigType.REPAIRER -> RepairerManager.putValue(config.key, value)
        }
    }
}
