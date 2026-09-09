package io.github.wabtest.core.util

import io.github.wabtest.core.expt.ExptManager
import io.github.wabtest.core.repairer.RepairerManager
import io.github.wabtest.core.test.ConfigItem
import io.github.wabtest.core.test.ConfigType

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
