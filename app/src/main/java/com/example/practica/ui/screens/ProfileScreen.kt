package com.example.practica.ui.screens

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.webkit.URLUtil
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.practica.data.repository.ProfileRepository
import com.example.practica.domain.model.UserProfile
import com.example.practica.ui.viewmodel.ProfileViewModel
import com.example.practica.ui.viewmodel.ProfileViewModelFactory
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val repository = remember { ProfileRepository(context) }
    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(repository)
    )

    val profile by viewModel.profile.observeAsState(UserProfile())
    val isEditing by viewModel.isEditing.observeAsState(false)
    var isDownloading by remember { mutableStateOf(false) }

    if (isEditing) {
        EditProfileScreen(
            profile = profile,
            onSave = { viewModel.saveProfile(it) },
            onCancel = { viewModel.cancelEditing() },
            onAvatarSelected = { viewModel.updateAvatar(it) }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Профиль") },
                    actions = {
                        IconButton(onClick = { viewModel.startEditing() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Аватар
                AvatarImage(avatarPath = profile.avatarPath)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = profile.fullName.ifBlank { "Не указано" },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                if (profile.position.isNotBlank()) {
                    Text(
                        text = profile.position,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (profile.reminder.isNotBlank()) {
                    Text(
                        text = "Напоминание: ${profile.reminder}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Резюме", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (profile.resumeUrl.isNotBlank()) {
                            Button(
                                onClick = {
                                    if (!isDownloading) {
                                        isDownloading = true
                                        downloadResume(context, profile.resumeUrl) {
                                            isDownloading = false
                                        }
                                    }
                                },
                                enabled = !isDownloading
                            ) {
                                if (isDownloading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Скачивание...")
                                } else {
                                    Icon(Icons.Default.Download, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Скачать резюме")
                                }
                            }
                        } else {
                            Text(
                                text = "Не указано",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AvatarImage(avatarPath: String) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        if (avatarPath.isNotBlank()) {
            val file = File(avatarPath)
            if (file.exists()) {
                val uri = runCatching {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                }.getOrNull()

                if (uri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(uri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Аватар",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(file)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Аватар",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Файл не найден",
                    modifier = Modifier.fillMaxSize().padding(30.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Аватар",
                modifier = Modifier.fillMaxSize().padding(30.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

fun downloadResume(context: Context, url: String, onComplete: () -> Unit) {
    try {
        val fileName = URLUtil.guessFileName(url, null, "application/pdf")

        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle("Резюме")
            setDescription("Загрузка резюме...")
            setMimeType("application/pdf")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        Toast.makeText(context, "Загрузка началась...", Toast.LENGTH_SHORT).show()
        onComplete()

        // Открываем файл после загрузки (можно через BroadcastReceiver, но для простоты оставляем так)
    } catch (e: Exception) {
        Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
        onComplete()
    }
}