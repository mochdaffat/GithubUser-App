package com.example.submissiongithubuserapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.submissiongithubuserapp.helper.Event
import com.example.submissiongithubuserapp.data.model.response.GithubUser
import com.example.submissiongithubuserapp.data.room.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    val listGithubUser: LiveData<List<GithubUser>> = repository.listGithubUser
    val isLoading: LiveData<Boolean> = repository.isLoading
    val toastText: LiveData<Event<String>> = repository.toastText


    fun getUser(query: String) {
        viewModelScope.launch {
            repository.getUser(query)
        }
    }

    fun getThemeSetting(): LiveData<Boolean> = repository.getThemeSetting().asLiveData()

    fun saveThemeSetting(newSetting: Boolean) {
        viewModelScope.launch {
            repository.saveThemeSetting(newSetting)
        }
    }
}