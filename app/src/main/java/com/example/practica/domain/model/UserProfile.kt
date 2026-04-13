package com.example.practica.domain.model

import java.io.Serializable

data class UserProfile(
    val fullName: String = "",
    val avatarPath: String = "",
    val resumeUrl: String = "",
    val position: String = "",
    val bio: String = ""
) : Serializable