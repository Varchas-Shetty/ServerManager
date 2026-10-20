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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.servermanag.ui.theme.ServerManagerTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ServerManagerTheme {
                val cpuUsage = remember {
                    mutableIntStateOf(35)
                }
                val memoryUsage = remember {
                    mutableIntStateOf(value = 69)
                }
                val diskUsage = remember {
                    mutableIntStateOf(value=10)
                }
                val metrics = ServerMetrics(
                    cpuUsage = cpuUsage.intValue,
                    memoryUsage = memoryUsage.intValue,
                    diskUsage = diskUsage.intValue
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
                            text = "Server Status: Disconnected",
                            style = MaterialTheme.typography.titleMedium
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
                                cpuUsage.intValue =
                                    if (cpuUsage.intValue == 35) 75 else 35
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Change CPU Usage")
                        }
                        Button(
                            onClick = {
                                memoryUsage.intValue = if(memoryUsage.intValue==69) 40 else 69
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        {
                            Text("Change Memory Usage")
                        }
                        Button(
                            onClick = {
                                diskUsage.intValue = if(diskUsage.intValue == 10) 50 else 10
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        {
                            Text("Change Disk Usage")
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
fun Greeting(name: String, modifier: Modifier = Modifier) {
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