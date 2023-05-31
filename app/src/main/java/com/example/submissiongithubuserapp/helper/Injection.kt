package com.example.submissiongithubuserapp.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.example.submissiongithubuserapp.data.api.ApiConfig
import com.example.submissiongithubuserapp.data.room.UserDatabase
import com.example.submissiongithubuserapp.data.room.UserRepository

private val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(
    "settings"
)

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val database = UserDatabase.getInstance(context)
        val dao = database.userDao()
        val preferences = SettingPreferences.getInstance(context.dataStore)
        return UserRepository.getInstance(preferences, apiService, dao)
    }
}