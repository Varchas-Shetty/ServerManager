
package com.example.servermanag

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.OpenSSHKnownHosts
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.File
import java.security.Security

class SshManager(private val context: Context) {

    companion object {
        private const val TAG = "SshManager"
    }

    suspend fun connect(
        host: String,
        username: String,
        password: String
    ): Result<Unit> = withContext(Dispatchers.IO) {

        Security.removeProvider("BC")
        Security.insertProviderAt(BouncyCastleProvider(), 1)

        val ssh = SSHClient()

        try {
            val knownHostsFile = File(context.cacheDir, "known_hosts")

            context.assets.open("known_hosts").use { input ->
                knownHostsFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            ssh.addHostKeyVerifier(
                OpenSSHKnownHosts(knownHostsFile)
            )

            ssh.connect(host, 22)
            ssh.authPassword(username, password)

            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "SSH connection failed", e)
            Result.failure(e)

        } finally {
            if (ssh.isConnected) {
                ssh.disconnect()
            }
        }
    }

    suspend fun executeCommand(
        host: String,
        username: String,
        password: String,
        command: String
    ): Result<String> = withContext(Dispatchers.IO) {

        Security.removeProvider("BC")
        Security.insertProviderAt(BouncyCastleProvider(), 1)

        val ssh = SSHClient()

        try {
            val knownHostsFile = File(context.cacheDir, "known_hosts")

            context.assets.open("known_hosts").use { input ->
                knownHostsFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            ssh.addHostKeyVerifier(
                OpenSSHKnownHosts(knownHostsFile)
            )

            ssh.connect(host, 22)
            ssh.authPassword(username, password)

            val session = ssh.startSession()
            try {
                val result = session.exec(command)
                val output = result.inputStream.bufferedReader().readText()
                val error = result.errorStream.bufferedReader().readText()

                if (error.isNotBlank()) {
                    Result.failure(Exception(error))
                } else {
                    Result.success(output)
                }
            } finally {
                session.close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "SSH command failed", e)
            Result.failure(e)
        } finally {
            if (ssh.isConnected) {
                ssh.disconnect()
            }
        }
    }

    suspend fun fetchServerMetrics(
        host: String,
        username: String,
        password: String
    ): Result<String> {
        return executeCommand(
            host = host,
            username = username,
            password = password,
            command = """
            echo "CPU:"
            top -bn1 | grep "Cpu(s)"
            echo "MEMORY:"
            free -m
            echo "DISK:"
            df -P /
        """.trimIndent()
        )
    }

}
