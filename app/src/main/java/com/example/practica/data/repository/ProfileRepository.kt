package com.example.practica.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.practica.domain.model.UserProfile
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream

class ProfileRepository(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveProfile(profile: UserProfile) {
        prefs.edit().putString("user_profile", gson.toJson(profile)).apply()
    }

    fun getProfile(): UserProfile {
        val json = prefs.getString("user_profile", null)
        return if (json != null) gson.fromJson(json, UserProfile::class.java) else UserProfile()
    }

    fun saveAvatar(uri: Uri): String {
        return try {
            val inputStream = appContext.contentResolver.openInputStream(uri) ?: return ""
            val avatarFile = File(File(appContext.filesDir, "avatars"), "avatar_${System.currentTimeMillis()}.jpg").apply {
                parentFile?.mkdirs()
            }
            FileOutputStream(avatarFile).use { inputStream.copyTo(it) }
            inputStream.close()
            avatarFile.absolutePath
        } catch (e: Exception) {
            Log.e("ProfileRepo", "Error saving avatar", e)
            ""
        }
    }

    fun getAppContext() = appContext
}