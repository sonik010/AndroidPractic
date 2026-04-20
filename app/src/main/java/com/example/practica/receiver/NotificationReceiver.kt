package com.example.practica.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.practica.MainActivity
import com.example.practica.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val userName = intent.getStringExtra("user_name") ?: "Студент"
        val time = intent.getStringExtra("time") ?: "сейчас"

        createNotificationChannel(context)

        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // ВАЖНО: используем ТО ЖЕ имя канала
        val notification = NotificationCompat.Builder(context, "pair_reminder_channel")  // было pair_remider_channel (опечатка)
            .setSmallIcon(android.R.drawable.ic_dialog_info)  // временная иконка
            .setContentTitle("❤️ Любимая пара начинается!")
            .setContentText("$userName, ваша пара в $time уже скоро!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // ВАЖНО: используем ТО ЖЕ имя канала
            val channel = NotificationChannel(
                "pair_reminder_channel",  // было pair_remider_channel (опечатка)
                "Напоминания о парах",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Уведомления о начале любимой пары"
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}