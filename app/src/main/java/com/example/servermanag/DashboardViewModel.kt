
package com.example.servermanag

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    var commandOutput by mutableStateOf("")
        private set

    var cpuUsage by mutableIntStateOf(35)
        private set

    var memoryUsage by mutableIntStateOf(62)
        private set

    var diskUsage by mutableIntStateOf(48)
        private set

    var connectionStatus by mutableStateOf(ConnectionStatus.DISCONNECTED)
        private set

    var connectionMessage by mutableStateOf("Disconnected")
        private set

    var ubuntuPassword by mutableStateOf("")
        private set

    fun updateUbuntuPassword(password: String) {
        ubuntuPassword = password
    }

    fun updateConnectionMessage(message: String) {
        connectionMessage = message
    }

    fun simulateServerUpdate() {
        cpuUsage = if (cpuUsage == 35) 75 else 35
        memoryUsage = if (memoryUsage == 62) 80 else 62
        diskUsage = if (diskUsage == 48) 90 else 48
    }

    fun connectToUbuntu(context: Context) {
        val password = ubuntuPassword

        if (password.isBlank()) {
            updateConnectionMessage("Enter your Ubuntu password")
            return
        }

        viewModelScope.launch {
            connectionStatus = ConnectionStatus.CONNECTING
            updateConnectionMessage("Connecting to Ubuntu...")

            val result = SshManager(context.applicationContext).connect(
                host = "192.168.6.128",
                username = "varchas",
                password = password
            )

            if (result.isSuccess) {
                connectionStatus = ConnectionStatus.CONNECTED
                updateConnectionMessage("SSH authentication successful")
            } else {
                connectionStatus = ConnectionStatus.DISCONNECTED
                updateConnectionMessage(
                    "Connection failed: ${
                        result.exceptionOrNull()?.message
                            ?: "Unknown error"
                    }"
                )
            }
        }
    }

    fun runHostnameCommand(context: Context) {
        val password = ubuntuPassword

        if (password.isBlank()) {
            updateConnectionMessage("Enter your Ubuntu password")
            return
        }

        viewModelScope.launch {
            updateConnectionMessage("Running hostname command...")

            val result = SshManager(context.applicationContext)
                .executeCommand(
                    host = "192.168.6.128",
                    username = "varchas",
                    password = password,
                    command = "hostname"
                )

            if (result.isSuccess) {
                commandOutput = result.getOrNull()?.trim() ?: ""
                updateConnectionMessage("Command executed successfully")
            } else {
                updateConnectionMessage(
                    "Command failed: ${
                        result.exceptionOrNull()?.message
                            ?: "Unknown error"
                    }"
                )
            }
        }
    }

    fun refreshServerMetrics(context: Context) {
        val password = ubuntuPassword

        if (password.isBlank()) {
            updateConnectionMessage("Enter your Ubuntu password")
            return
        }

        viewModelScope.launch {
            updateConnectionMessage("Fetching real server metrics...")

            val result = SshManager(context.applicationContext)
                .fetchServerMetrics(
                    host = "192.168.6.128",
                    username = "varchas",
                    password = password
                )

            if (result.isFailure) {
                updateConnectionMessage(
                    "Metrics failed: ${
                        result.exceptionOrNull()?.message
                            ?: "Unknown error"
                    }"
                )
                return@launch
            }

            val output = result.getOrNull().orEmpty()

            try {
                val cpuLine = output
                    .substringAfter("CPU:")
                    .substringBefore("MEMORY:")
                    .trim()

                val idleMatch = Regex("""([\d.]+)\s*id""")
                    .find(cpuLine)

                val cpuIdle = idleMatch?.groupValues?.get(1)
                    ?.toFloatOrNull()
                    ?: error("Could not parse CPU usage")

                val memoryLine = output
                    .substringAfter("MEMORY:")
                    .substringBefore("DISK:")
                    .lines()
                    .first { it.trim().startsWith("Mem:") }
                    .trim()
                    .split(Regex("\\s+"))

                val memoryTotal = memoryLine[1].toFloat()
                val memoryUsed = memoryLine[2].toFloat()

                require(memoryTotal > 0f) {
                    "Invalid total memory value"
                }

                val diskLine = output
                    .substringAfter("DISK:")
                    .lines()
                    .first { it.trim().startsWith("/dev/") }
                    .trim()
                    .split(Regex("\\s+"))

                val diskPercent = diskLine[4]
                    .removeSuffix("%")
                    .toInt()

                cpuUsage = (100f - cpuIdle)
                    .toInt()
                    .coerceIn(0, 100)

                memoryUsage = (memoryUsed / memoryTotal * 100f)
                    .toInt()
                    .coerceIn(0, 100)

                diskUsage = diskPercent.coerceIn(0, 100)

                updateConnectionMessage("Real server metrics updated")
            } catch (e: Exception) {
                updateConnectionMessage(
                    "Could not parse metrics: ${
                        e.message ?: "Unknown error"
                    }"
                )
            }
        }
    }
}

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}
