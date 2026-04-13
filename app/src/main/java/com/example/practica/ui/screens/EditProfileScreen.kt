package com.example.practica.ui.screens

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.practica.domain.model.UserProfile
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    profile: UserProfile,
    onSave: (UserProfile) -> Unit,
    onCancel: () -> Unit,
    onAvatarSelected: (Uri) -> Unit
) {
    val context = LocalContext.current
    var fullName by remember { mutableStateOf(profile.fullName) }
    var position by remember { mutableStateOf(profile.position) }
    var bio by remember { mutableStateOf(profile.bio) }
    var resumeUrl by remember { mutableStateOf(profile.resumeUrl) }
    var avatarPath by remember { mutableStateOf(profile.avatarPath) }

    var showImageSourceDialog by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // Проверка разрешений
    val hasCameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    val hasStoragePermission = if (Build.VERSION.SDK_INT >= 33) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    // Запрос разрешений
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions.getOrDefault(Manifest.permission.CAMERA, false)
        val storageGranted = if (Build.VERSION.SDK_INT >= 33) {
            permissions.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES, false)
        } else {
            permissions.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false)
        }

        if (cameraGranted && storageGranted) {
            showImageSourceDialog = true
        } else if (cameraGranted) {
            showImageSourceDialog = true
        } else if (storageGranted) {
            showImageSourceDialog = true
        } else {
            Toast.makeText(context, "Нужны разрешения для выбора фото", Toast.LENGTH_SHORT).show()
        }
    }

    // Выбор из галереи
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Toast.makeText(context, "Фото выбрано, сохраняем...", Toast.LENGTH_SHORT).show()
            onAvatarSelected(it)
        }
    }

    // Съемка фото
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            Toast.makeText(context, "Фото сохранено", Toast.LENGTH_SHORT).show()
            onAvatarSelected(photoUri!!)
        }
    }

    // Создание временного файла для фото
    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = context.cacheDir
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    fun takePhoto() {
        val file = createImageFile()
        photoUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        cameraLauncher.launch(photoUri!!)
    }

    fun checkPermissionsAndShowDialog() {
        if (hasCameraPermission && hasStoragePermission) {
            showImageSourceDialog = true
        } else {
            val permissions = mutableListOf(Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT >= 33) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактирование") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Отмена")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        onSave(UserProfile(fullName, avatarPath, resumeUrl, position, bio))
                    }) {
                        Text("Готово")
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Аватар
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { checkPermissionsAndShowDialog() }
            ) {
                EditAvatarImage(avatarPath = avatarPath)

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { checkPermissionsAndShowDialog() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Изменить", modifier = Modifier.size(18.dp), tint = Color.White)
                }
            }

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("ФИО") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = position,
                onValueChange = { position = it },
                label = { Text("Должность") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = resumeUrl,
                onValueChange = { resumeUrl = it },
                label = { Text("Ссылка на резюме (PDF)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Диалог выбора источника
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Выберите источник") },
            text = { Text("Откуда взять фото?") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Галерея")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    takePhoto()
                }) {
                    Text("Камера")
                }
            }
        )
    }
}

@Composable
fun EditAvatarImage(avatarPath: String) {
    val context = LocalContext.current

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
            contentDescription = "Выбрать фото",
            modifier = Modifier.fillMaxSize().padding(30.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}