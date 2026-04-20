package com.example.practica.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.practica.receiver.NotificationReceiver

object ReminderScheduler {

    fun scheduleReminder(context: Context, time: String, userName: String) {
        if (time.isBlank()) return

        val parts = time.split(":")
        if (parts.size != 2) return

        val hour = parts[0].toIntOrNull() ?: return
        val minute = parts[1].toIntOrNull() ?: return

        // Используем applicationContext для предотвращения утечек
        val appContext = context.applicationContext
        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Отменяем старые напоминания
        cancelReminder(appContext)

        // Устанавливаем время
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, minute)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(appContext, NotificationReceiver::class.java).apply {
            putExtra("user_name", userName)
            putExtra("time", time)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            System.currentTimeMillis().toInt(), // Уникальный ID
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Устанавливаем будильник
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                try {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                    Log.d("ReminderScheduler", "Точный будильник установлен на ${calendar.time}")
                } catch (e: SecurityException) {
                    Log.e("ReminderScheduler", "SecurityException: ${e.message}")
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                }
            } else {
                Log.w("ReminderScheduler", "Нет разрешения на точные будильники")
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    fun cancelReminder(context: Context) {
        val appContext = context.applicationContext
        val intent = Intent(appContext, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            appContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}