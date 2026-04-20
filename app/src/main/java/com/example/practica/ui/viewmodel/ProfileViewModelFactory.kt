package com.example.practica.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.practica.data.repository.ProfileRepository

class ProfileViewModelFactory(
    private val repository: ProfileRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(repository) as T
    }
}