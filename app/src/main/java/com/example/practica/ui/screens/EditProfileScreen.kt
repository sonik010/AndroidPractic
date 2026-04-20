package com.example.practica.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var fullName by remember { mutableStateOf(profile.fullName) }
    var position by remember { mutableStateOf(profile.position) }
    var resumeUrl by remember { mutableStateOf(profile.resumeUrl) }
    var avatarPath by remember { mutableStateOf(profile.avatarPath) }
    var reminder by remember { mutableStateOf(profile.reminder) }
    var showTimePicker by remember { mutableStateOf(false) }
    var timeError by remember { mutableStateOf<String?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val hasCameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    val hasStoragePermission = if (Build.VERSION.SDK_INT >= 33) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val storageGranted = if (Build.VERSION.SDK_INT >= 33) {
            permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false
        } else {
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
        }
        if (cameraGranted || storageGranted) showImageSourceDialog = true
        else Toast.makeText(context, "Нужны разрешения для выбора фото", Toast.LENGTH_SHORT).show()
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            avatarPath = "" // Временно очищаем, чтобы показать загрузку
            onAvatarSelected(it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && photoUri != null) {
            avatarPath = "" // Временно очищаем, чтобы показать загрузку
            onAvatarSelected(photoUri!!)
        }
    }

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", context.cacheDir)
    }

    fun takePhoto() {
        val file = createImageFile()
        photoUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        cameraLauncher.launch(photoUri!!)
    }

    fun isValidTime(time: String): Boolean {
        if (time.isBlank()) return true
        return Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$").matches(time)
    }

    // Обновляем avatarPath при изменении профиля
    LaunchedEffect(profile.avatarPath) {
        if (profile.avatarPath.isNotBlank()) {
            avatarPath = profile.avatarPath
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактирование профиля") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Отмена")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        if (reminder.isBlank() || isValidTime(reminder)) {
                            val updatedProfile = profile.copy(
                                fullName = fullName,
                                avatarPath = avatarPath,
                                resumeUrl = resumeUrl,
                                position = position,
                                reminder = reminder
                            )
                            onSave(updatedProfile)
                            focusManager.clearFocus()
                        } else {
                            timeError = "Неверный формат. Используйте HH:MM"
                            Toast.makeText(context, "Неверный формат времени. Используйте HH:MM", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Сохранить")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            // Снимаем фокус только при клике на пустое пространство
                            focusManager.clearFocus()
                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Аватар
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        focusManager.clearFocus() // Снимаем фокус перед открытием диалога
                        if (hasCameraPermission && hasStoragePermission) {
                            showImageSourceDialog = true
                        } else {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.CAMERA,
                                    if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
                                )
                            )
                        }
                    }
            ) {
                EditAvatarImage(avatarPath)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            focusManager.clearFocus() // Снимаем фокус перед открытием диалога
                            if (hasCameraPermission && hasStoragePermission) {
                                showImageSourceDialog = true
                            } else {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.CAMERA,
                                        if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
                                    )
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Изменить фото", modifier = Modifier.size(18.dp), tint = Color.White)
                }
            }

            // ФИО
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("ФИО") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Должность
//            OutlinedTextField(
//                value = position,
//                onValueChange = { position = it },
//                label = { Text("Должность") },
//                modifier = Modifier.fillMaxWidth(),
//                singleLine = true
//            )

            // Ссылка на резюме
            OutlinedTextField(
                value = resumeUrl,
                onValueChange = { resumeUrl = it },
                label = { Text("Ссылка на резюме (PDF)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // === ПОЛЕ ДЛЯ ВРЕМЕНИ - ПОЛНОСТЬЮ РАБОЧАЯ ВЕРСИЯ ===
            OutlinedTextField(
                value = reminder,
                onValueChange = { newValue ->
                    reminder = newValue
                    timeError = if (newValue.isNotBlank() && !isValidTime(newValue)) {
                        "Неверный формат. Используйте HH:MM"
                    } else null
                },
                label = { Text("Время любимой пары") },
                placeholder = { Text("14:30") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                isError = timeError != null,
                supportingText = {
                    if (timeError != null) {
                        Text(
                            text = timeError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Text(
                            text = "Формат: часы:минуты (00:00-23:59)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            showTimePicker = true
                        }
                    ) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = "Выбрать время"
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }
    }

    // Диалог выбора источника фото
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Выберите источник фото") },
            text = { Text("Откуда вы хотите взять фото?") },
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

    // Диалог выбора времени (TimePicker)
    if (showTimePicker) {
        val initialHour = reminder.split(":").getOrNull(0)?.toIntOrNull()?.coerceIn(0, 23) ?: 12
        val initialMinute = reminder.split(":").getOrNull(1)?.toIntOrNull()?.coerceIn(0, 59) ?: 0

        val timePickerState = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Выберите время") },
            text = {
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    reminder = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    timeError = null
                    showTimePicker = false
                }) {
                    Text("Установить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun EditAvatarImage(avatarPath: String) {
    val context = LocalContext.current
    val file = File(avatarPath)

    if (avatarPath.isNotBlank() && file.exists()) {
        val uri = runCatching {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        }.getOrNull()

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(uri ?: file)
                .crossfade(true)
                .build(),
            contentDescription = "Аватар",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Нет аватара",
            modifier = Modifier.fillMaxSize().padding(30.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}