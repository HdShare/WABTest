package me.hd.wabtest.core.expt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExptItem(
    @SerialName("ExptId") var exptId: Int = 0,
    @SerialName("GroupId") var groupId: Int = 0,
    @SerialName("ExptSequence") var exptSequence: Int = 1,
    @SerialName("Priority") var priority: Int = 1,
    @SerialName("NeedReport") var needReport: Int = 0,
    @SerialName("StartTime") var startTime: Long = 0,
    @SerialName("EndTime") var endTime: Long = 0,
    @SerialName("ExptType") var exptType: Int = 4,
    @SerialName("SvrType") var svrType: Int = 1,
    @SerialName("ExptCheckSum") var exptCheckSum: String = "",
    @SerialName("Args") var args: MutableList<ExptArg> = mutableListOf(),
)

@Serializable
data class ExptArg(
    @SerialName("Key") var key: String = "",
    @SerialName("Val") var value: String = "",
)
