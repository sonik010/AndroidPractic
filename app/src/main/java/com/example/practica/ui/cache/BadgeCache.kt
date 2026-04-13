package com.example.practica.ui.cache

class BadgeCache {
    private var _shouldShowBadge = true

    fun shouldShowBadge(): Boolean = _shouldShowBadge

    fun setBadgeShown() {
        _shouldShowBadge = false
    }

    fun resetBadge() {
        _shouldShowBadge = true
    }
}