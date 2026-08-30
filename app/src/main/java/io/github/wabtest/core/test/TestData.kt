package io.github.wabtest.core.test

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestItem(
    @SerialName("group") var group: String = "",
    @SerialName("configs") var configs: List<ConfigItem> = emptyList(),
)

@Serializable
data class ConfigItem(
    @SerialName("key") var key: String = "",
    @SerialName("title") var title: String = "",
    @SerialName("desc") var desc: String = "",
    @SerialName("version") var version: ConfigVersion = ConfigVersion(),
    @SerialName("options") var options: List<ConfigOption> = emptyList(),
)

@Serializable
data class ConfigVersion(
    @SerialName("min") var min: String = "",
    @SerialName("max") var max: String = "",
)

@Serializable
data class ConfigOption(
    @SerialName("alias") var alias: String = "",
    @SerialName("value") var value: String = "",
)
