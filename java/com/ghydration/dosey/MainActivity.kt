package com.ghydration.dosey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.elevatedCardElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var repository: DoseRepository

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = DoseRepository(this)

        setContent {
            var showDialog by remember { mutableStateOf(false) }
            var lastDoseTime by remember { mutableStateOf(0L) }
            var totalDoses by remember { mutableStateOf(0) }
            var totalML by remember { mutableStateOf(0) }
            var timerText by remember { mutableStateOf("No dose logged") }

            // collect flows
            LaunchedEffect(Unit) {
                launch {
                    repository.lastDoseTime.collectLatest { lastDoseTime = it }
                }
                launch {
                    repository.totalDoses.collectLatest { totalDoses = it }
                }
                launch {
                    repository.totalML.collectLatest { totalML = it }
                }
            }

            // update timer every second
            LaunchedEffect(lastDoseTime) {
                while (true) {
                    if (lastDoseTime != 0L) {
                        val diff = System.currentTimeMillis() - lastDoseTime
                        val hours = TimeUnit.MILLISECONDS.toHours(diff)
                        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
                        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60
                        timerText = String.format("%02d:%02d:%02d since last dose", hours, minutes, seconds)
                    }
                    kotlinx.coroutines.delay(1000)
                }
            }

            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(title = { Text("DOSEY", fontSize = 24.sp) })
                },
                content = { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Today's Totals Card
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = elevatedCardElevation(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(Color(0xFFE3F2FD))
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Today's Totals", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Doses: $totalDoses", fontSize = 18.sp)
                                Text("Total mL: $totalML", fontSize = 18.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Timer Card
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = elevatedCardElevation(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFFF9C4))
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(timerText, fontSize = 22.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Log Dose Button
                        Button(
                            onClick = { showDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Log Dose", fontSize = 20.sp)
                        }
                    }
                }
            )

            if (showDialog) {
                DoseDialog(
                    onDismiss = { showDialog = false },
                    onSubmit = { amount, timestamp ->
                        lifecycleScope.launch {
                            repository.logDose(amount, timestamp)
                        }
                    },
                    lastDoseTime = lastDoseTime
                )
            }
        }
    }
}
