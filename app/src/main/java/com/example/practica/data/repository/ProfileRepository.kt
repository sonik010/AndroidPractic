package com.example.practica.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.practica.domain.model.UserProfile
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream

class ProfileRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveProfile(profile: UserProfile) {
        val json = gson.toJson(profile)
        prefs.edit().putString("user_profile", json).apply()
        Log.d("ProfileRepo", "Profile saved: $json")
    }

    fun getProfile(): UserProfile {
        val json = prefs.getString("user_profile", null)
        return if (json != null) {
            gson.fromJson(json, UserProfile::class.java)
        } else {
            UserProfile()
        }
    }

    fun saveAvatar(uri: Uri): String {
        return try {
            Log.d("ProfileRepo", "Saving avatar from URI: $uri")

            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e("ProfileRepo", "Cannot open input stream for URI: $uri")
                return ""
            }

            val avatarDir = File(context.filesDir, "avatars")
            if (!avatarDir.exists()) {
                avatarDir.mkdirs()
                Log.d("ProfileRepo", "Created avatars dir: ${avatarDir.absolutePath}")
            }

            val avatarFile = File(avatarDir, "avatar_${System.currentTimeMillis()}.jpg")
            FileOutputStream(avatarFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()

            val savedPath = avatarFile.absolutePath
            Log.d("ProfileRepo", "Avatar saved to: $savedPath")
            Log.d("ProfileRepo", "File exists: ${avatarFile.exists()}, size: ${avatarFile.length()}")

            savedPath
        } catch (e: Exception) {
            Log.e("ProfileRepo", "Error saving avatar", e)
            ""
        }
    }
}