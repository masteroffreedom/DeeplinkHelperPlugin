package ru.deeplink.plugin

interface CommandExecutor {
    fun runCommand(command: String, errorCallback: () -> Unit)
}