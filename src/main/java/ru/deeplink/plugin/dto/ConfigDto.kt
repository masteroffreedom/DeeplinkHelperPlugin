package ru.deeplink.plugin.dto

import com.google.gson.annotations.SerializedName

data class ConfigDto(
        @SerializedName("applicationId") val applicationId: String?,
        @SerializedName("deeplinks") val deeplinks: List<DeeplinkDto>?,
        @SerializedName("commands") val commands: List<CommandDto>?
)