package ru.deeplink.plugin.dto

import com.google.gson.annotations.SerializedName

data class DeeplinkDto(
    @SerializedName("deeplink") val deeplink: String?,
    @SerializedName("hasArgument") val hasArgument: Boolean?,
    @SerializedName("argumentName") val argumentName: String?,
    @SerializedName("name") val name: String?
)