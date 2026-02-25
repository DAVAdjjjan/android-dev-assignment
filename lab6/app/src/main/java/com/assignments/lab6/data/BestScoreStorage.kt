package com.assignments.lab6.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_prefs")

class BestScoreStorage(private val context: Context) {

    companion object {
        private val BEST_SCORE_KEY = intPreferencesKey("best_score")
    }

    val bestScore: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[BEST_SCORE_KEY] ?: 0
    }

    suspend fun saveBestScore(score: Int) {
        context.dataStore.edit { prefs ->
            prefs[BEST_SCORE_KEY] = score
        }
    }
}
