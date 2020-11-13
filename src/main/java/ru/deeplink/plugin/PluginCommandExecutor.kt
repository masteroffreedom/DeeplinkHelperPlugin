package ru.deeplink.plugin

import com.android.ddmlib.IShellOutputReceiver
import com.intellij.openapi.project.Project
import org.jetbrains.android.sdk.AndroidSdkUtils

class PluginCommandExecutor(private val project: Project): CommandExecutor {
    override fun runCommand(command: String, errorCallback: () -> Unit) {
        println("exec: $command")
        val devices = AndroidSdkUtils.getDebugBridge(project)?.devices
        println("devices: $devices")
        devices?.firstOrNull()?.executeShellCommand(command, object : IShellOutputReceiver {
            override fun addOutput(p0: ByteArray?, p1: Int, p2: Int) {
                // nop
            }

            override fun flush() {
                // nop
            }

            override fun isCancelled(): Boolean {
                return false
            }

        })
    }
}