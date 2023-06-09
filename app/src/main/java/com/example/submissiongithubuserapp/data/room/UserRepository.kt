package com.example.submissiongithubuserapp.data.room

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.submissiongithubuserapp.data.api.ApiService
import com.example.submissiongithubuserapp.data.model.entity.UserEntity
import com.example.submissiongithubuserapp.data.model.response.GithubSearchResponse
import com.example.submissiongithubuserapp.data.model.response.GithubUser
import com.example.submissiongithubuserapp.helper.Event
import com.example.submissiongithubuserapp.helper.SettingPreferences
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository private constructor(
    private val preferences: SettingPreferences,
    private val apiService: ApiService,
    private val userDao: UserDao
) {

    private val _listGithubUser = MutableLiveData<List<GithubUser>>()
    val listGithubUser: LiveData<List<GithubUser>> = _listGithubUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    fun getUser(query: String?) {
        _isLoading.value = true
        val client = apiService.getUser(query)
        client.enqueue(object : Callback<GithubSearchResponse> {
            override fun onResponse(
                call: Call<GithubSearchResponse>,
                response: Response<GithubSearchResponse>
            ) {
                _isLoading.value = false
                val listUser = response.body()?.items
                if (response.isSuccessful) {
                    if (listUser.isNullOrEmpty()) {
                        _toastText.value = Event("User tidak ditemukan!")
                    } else {
                        _listGithubUser.value = listUser
                    }
                } else {
                    _toastText.value = Event(response.message())
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GithubSearchResponse>, t: Throwable) {
                _isLoading.value = false
                _toastText.value = Event("Tidak Ada Jaringan Internet!")
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getThemeSetting(): Flow<Boolean> = preferences.getThemeSetting()

    suspend fun saveThemeSetting(isNightModeActive: Boolean) {
        preferences.saveThemeSetting(isNightModeActive)
    }

    fun getFavoritedUser(): LiveData<List<UserEntity>> {
        return userDao.getFavoritedUser().asLiveData()
    }

    suspend fun addFavoriteUser(user: UserEntity, favoriteState: Boolean) {
        user.isFavorited = favoriteState
        userDao.insertUser(user)
    }

    suspend fun deleteFavoriteUser(user: UserEntity, favoriteState: Boolean) {
        user.isFavorited = favoriteState
        userDao.deleteUser(user)
    }

    companion object {
        private const val TAG = "UserRepository"

        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            preferences: SettingPreferences,
            apiService: ApiService,
            userDao: UserDao,
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(preferences, apiService, userDao)
            }.also { instance = it }
    }
}