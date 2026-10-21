
package com.example.servermanag

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servermanag.ui.theme.ServerManagerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ServerManagerTheme {
                val viewModel: DashboardViewModel = viewModel()
                val context = LocalContext.current

                val metrics = ServerMetrics(
                    cpuUsage = viewModel.cpuUsage,
                    memoryUsage = viewModel.memoryUsage,
                    diskUsage = viewModel.diskUsage
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "ServerManager",
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Your Linux server at a glance",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Server Status: ${
                                viewModel.connectionStatus.name
                                    .lowercase()
                                    .replaceFirstChar { it.uppercase() }
                            }",
                            style = MaterialTheme.typography.titleMedium
                        )

                        OutlinedTextField(
                            value = viewModel.ubuntuPassword,
                            onValueChange = {
                                viewModel.updateUbuntuPassword(it)
                            },
                            label = {
                                Text("Ubuntu Password")
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                viewModel.connectToUbuntu(context)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Connect to Ubuntu")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.runHostnameCommand(context)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Run hostname command")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                viewModel.refreshServerMetrics(context)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Refresh Real Server Metrics")
                        }
                        Text(
                            text = "Command output: ${
                                viewModel.commandOutput.ifBlank { "No output yet" }
                            }",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = viewModel.connectionMessage,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                MetricCard(
                                    title = "CPU Usage",
                                    usage = metrics.cpuUsage
                                )
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                MetricCard(
                                    title = "Memory Usage",
                                    usage = metrics.memoryUsage
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        MetricCard(
                            title = "Disk Usage",
                            usage = metrics.diskUsage
                        )

                        Button(
                            onClick = {
                                viewModel.simulateServerUpdate()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Simulate Server Update")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    usage: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$usage%",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(usage.coerceIn(0, 100) / 100f)
                        .height(8.dp)
                        .background(
                            colorScheme.primary,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    }
}

data class ServerMetrics(
    val cpuUsage: Int,
    val memoryUsage: Int,
    val diskUsage: Int
)

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ServerManagerTheme {
        Greeting("Android")
    }
}
