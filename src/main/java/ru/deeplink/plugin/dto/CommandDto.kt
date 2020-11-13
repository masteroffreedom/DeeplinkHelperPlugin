package ru.deeplink.plugin.dto

import com.google.gson.annotations.SerializedName

data class CommandDto(
    @SerializedName("command") val command: String?,
    @SerializedName("name") val name: String?
)