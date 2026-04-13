package com.example.practica.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.practica.data.repository.ProfileRepository
import com.example.practica.domain.model.UserProfile

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _profile = MutableLiveData<UserProfile>()
    val profile: LiveData<UserProfile> = _profile

    private val _isEditing = MutableLiveData(false)
    val isEditing: LiveData<Boolean> = _isEditing

    init {
        _profile.value = repository.getProfile()
    }

    fun saveProfile(profile: UserProfile) {
        repository.saveProfile(profile)
        _profile.value = profile
        _isEditing.value = false
    }

    fun startEditing() {
        _isEditing.value = true
    }

    fun cancelEditing() {
        _isEditing.value = false
        _profile.value = repository.getProfile()
    }

    fun updateAvatar(uri: Uri) {
        // Сохраняем фото и получаем путь к файлу
        val savedPath = repository.saveAvatar(uri)
        if (savedPath.isNotBlank()) {
            val current = _profile.value ?: UserProfile()
            val updated = current.copy(avatarPath = savedPath)
            repository.saveProfile(updated)
            _profile.value = updated
        }
    }
}