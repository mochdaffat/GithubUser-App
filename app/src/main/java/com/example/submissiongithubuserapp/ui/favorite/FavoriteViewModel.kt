package com.example.submissiongithubuserapp.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submissiongithubuserapp.data.model.entity.UserEntity
import com.example.submissiongithubuserapp.data.room.UserRepository
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: UserRepository) : ViewModel() {

    fun getFavoritedUser() = repository.getFavoritedUser()

    fun saveDeleteUser(user: UserEntity, isFavorited: Boolean) {
        viewModelScope.launch {
            if (isFavorited) {
                repository.deleteFavoriteUser(user, false)
            } else {
                repository.addFavoriteUser(user, true)
            }
        }
    }
}