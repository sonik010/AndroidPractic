package com.example.practica.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.practica.data.repository.ProfileRepository
import com.example.practica.domain.model.UserProfile
import com.example.practica.utils.ReminderScheduler

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
    private val _profile = MutableLiveData<UserProfile>().apply { value = repository.getProfile() }
    val profile: LiveData<UserProfile> = _profile
    private val _isEditing = MutableLiveData(false)
    val isEditing: LiveData<Boolean> = _isEditing

    fun saveProfile(profile: UserProfile) {
        repository.saveProfile(profile)
        _profile.value = profile
        _isEditing.value = false
        if (profile.reminder.isNotBlank()) {
            ReminderScheduler.scheduleReminder(repository.getAppContext(), profile.reminder, profile.fullName.ifBlank { "Студент" })
        }
    }

    fun startEditing() { _isEditing.value = true }
    fun cancelEditing() { _isEditing.value = false; _profile.value = repository.getProfile() }
    fun updateAvatar(uri: Uri) {
        repository.saveAvatar(uri).takeIf { it.isNotBlank() }?.let {
            _profile.value = (_profile.value ?: UserProfile()).copy(avatarPath = it)
            repository.saveProfile(_profile.value!!)
        }
    }
}