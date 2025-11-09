package com.ghydration.dosey

import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.content.Context

val Context.dataStore by preferencesDataStore("dosey_prefs")

class DoseRepository(private val context: Context) {

    private val LAST_DOSE_TIME = longPreferencesKey("last_dose_time")
    private val TOTAL_DOSES = intPreferencesKey("total_doses")
    private val TOTAL_ML = intPreferencesKey("total_ml")

    val lastDoseTime: Flow<Long> = context.dataStore.data
        .map { it[LAST_DOSE_TIME] ?: 0L }

    val totalDoses: Flow<Int> = context.dataStore.data
        .map { it[TOTAL_DOSES] ?: 0 }

    val totalML: Flow<Int> = context.dataStore.data
        .map { it[TOTAL_ML] ?: 0 }

    suspend fun logDose(amount: Int, timestamp: Long = System.currentTimeMillis()) {
        context.dataStore.edit { prefs ->
            prefs[LAST_DOSE_TIME] = timestamp
            prefs[TOTAL_DOSES] = (prefs[TOTAL_DOSES] ?: 0) + 1
            prefs[TOTAL_ML] = (prefs[TOTAL_ML] ?: 0) + amount
        }
    }
}
