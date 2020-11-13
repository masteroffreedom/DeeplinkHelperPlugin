package ru.deeplink.plugin

import java.io.IOException

class SimpleCommandExecutor: CommandExecutor {
    override fun runCommand(command: String, errorCallback: () -> Unit) {
        try {
            val adbCommand = "adb shell $command"
            println("exec: $adbCommand")
            val p = Runtime.getRuntime().exec(adbCommand)
            p.errorStream.let {
                if (it.available() > 0) {
                    errorCallback.invoke()
                }
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
    }
}