package io.github.wabtest.core.test

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestItem(
    @SerialName("group")
    var group: String = "",
    @SerialName("configs")
    var configs: List<ConfigItem> = emptyList(),
)

@Serializable
data class ConfigItem(
    @SerialName("type")
    var type: ConfigType = ConfigType.EXPT,
    @SerialName("key")
    var key: String = "",
    @SerialName("title")
    var title: String = "",
    @SerialName("desc")
    var desc: String = "",
    @SerialName("version")
    var version: ConfigVersion = ConfigVersion(),
    @SerialName("ui")
    var ui: ConfigUi = ConfigUi(),
)

@Serializable
enum class ConfigType {
    @SerialName("expt")
    EXPT,

    @SerialName("repairer")
    REPAIRER,
}

@Serializable
data class ConfigVersion(
    @SerialName("min")
    var min: String = "",
    @SerialName("max")
    var max: String = "",
)

@Serializable
data class ConfigUi(
    @SerialName("type")
    var type: ConfigUiType = ConfigUiType.INPUT,
    @SerialName("values")
    var values: SwitchValues = SwitchValues(),
    @SerialName("options")
    var options: List<ConfigOption> = emptyList(),
)

@Serializable
enum class ConfigUiType {
    @SerialName("input")
    INPUT,

    @SerialName("switch")
    SWITCH,

    @SerialName("single_choice")
    SINGLE_CHOICE,
}

@Serializable
data class SwitchValues(
    @SerialName("off")
    var off: String = "0",
    @SerialName("on")
    var on: String = "1",
)

@Serializable
data class ConfigOption(
    @SerialName("alias")
    var alias: String = "",
    @SerialName("value")
    var value: String = "",
)
