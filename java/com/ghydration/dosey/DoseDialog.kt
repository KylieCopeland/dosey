package com.ghydration.dosey

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DoseDialog(
    onDismiss: () -> Unit,
    onSubmit: (amount: Int, timestamp: Long) -> Unit,
    lastDoseTime: Long
) {
    var amountText by remember { mutableStateOf("") }
    val now = System.currentTimeMillis()
    val minutesSinceLastDose = if (lastDoseTime == 0L) Long.MAX_VALUE
    else (now - lastDoseTime) / 1000 / 60

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Dose") },
        text = {
            Column {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                    label = { Text("Amount (mL)") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (minutesSinceLastDose < 120) {
                    Text(
                        text = "⚠️ Only $minutesSinceLastDose minutes since last dose!",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (amountText.isNotEmpty()) {
                    onSubmit(amountText.toInt(), now)
                    onDismiss()
                }
            }) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
