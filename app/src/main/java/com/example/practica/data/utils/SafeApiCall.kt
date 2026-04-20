package com.example.practica.data.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(
    block: suspend () -> T
): Result<T> = withContext(Dispatchers.IO) {
    try {
        Result.success(block())
    } catch (e: UnknownHostException) {
        Result.failure(Exception("Нет подключения к интернету. Проверьте соединение."))
    } catch (e: IOException) {
        Result.failure(Exception("Ошибка сети: ${e.message}"))
    } catch (e: Exception) {
        Result.failure(Exception("Ошибка загрузки данных: ${e.message}"))
    }
}