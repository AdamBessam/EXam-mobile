package com.example.firstaidfront.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


import androidx.lifecycle.viewModelScope
import com.example.firstaidfront.models.Notification
import com.example.firstaidfront.models.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class NotificationsViewModel : ViewModel() {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadNotifications()
    }

    fun refreshNotifications() {
        loadNotifications()
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val updatedNotifications = _notifications.value.map {
                it.copy(isRead = true)
            }
            _notifications.value = updatedNotifications
            // Here you would typically call your repository to update the read status
            // repository.updateNotifications(updatedNotifications)
        }
    }

    fun markAsRead(notificationId: Long) {
        viewModelScope.launch {
            val updatedNotifications = _notifications.value.map { notification ->
                if (notification.id == notificationId) {
                    notification.copy(isRead = true)
                } else {
                    notification
                }
            }
            _notifications.value = updatedNotifications
            // Here you would typically call your repository to update the read status
            // repository.updateNotification(notificationId, isRead = true)
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // In a real app, you would get this from a repository
                // val notifications = repository.getNotifications()
                _notifications.value = generateSampleNotifications()
            } catch (e: Exception) {
                // Handle error
                // _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Sample data generator - Replace with actual API/database calls
    private fun generateSampleNotifications(): List<Notification> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            Notification(
                id = 1,
                title = "New First Aid Course Available",
                message = "Check out our latest course on Emergency Response",
                type = NotificationType.COURSE_UPDATE,
                timestamp = currentTime - 5 * 60 * 1000, // 5 minutes ago
                isRead = false
            ),
            Notification(
                id = 2,
                title = "Test Results Ready",
                message = "Your CPR certification test results are now available",
                type = NotificationType.TEST_RESULT,
                timestamp = currentTime - 30 * 60 * 1000, // 30 minutes ago
                isRead = false
            ),
            Notification(
                id = 3,
                title = "Certificate Ready",
                message = "Congratulations! Your First Aid certificate is ready for download",
                type = NotificationType.CERTIFICATE_READY,
                timestamp = currentTime - 2 * 60 * 60 * 1000, // 2 hours ago
                isRead = true
            ),
            Notification(
                id = 4,
                title = "Training Reminder",
                message = "Don't forget your scheduled training session tomorrow",
                type = NotificationType.TRAINING_REMINDER,
                timestamp = currentTime - 24 * 60 * 60 * 1000, // 1 day ago
                isRead = true
            ),
            Notification(
                id = 5,
                title = "Course Update",
                message = "Important updates have been made to your enrolled courses",
                type = NotificationType.COURSE_UPDATE,
                timestamp = currentTime - 2 * 24 * 60 * 60 * 1000, // 2 days ago
                isRead = true
            ),
            Notification(
                id = 6,
                title = "Welcome!",
                message = "Welcome to First Aid Training. Start your journey to becoming certified!",
                type = NotificationType.GENERAL_INFO,
                timestamp = currentTime - 7 * 24 * 60 * 60 * 1000, // 7 days ago
                isRead = true
            )
        )
    }

    fun deleteNotification(notificationId: Long) {
        viewModelScope.launch {
            _notifications.value = _notifications.value.filter { it.id != notificationId }
            // Here you would typically call your repository to delete the notification
            // repository.deleteNotification(notificationId)
        }
    }

    fun getUnreadCount(): Int {
        return _notifications.value.count { !it.isRead }
    }

    // For handling notification actions
    fun handleNotificationAction(notification: Notification) {
        when (notification.type) {
            NotificationType.TEST_RESULT -> {
                // Navigate to test results
            }
            NotificationType.CERTIFICATE_READY -> {
                // Navigate to certificate download
            }
            NotificationType.TRAINING_REMINDER -> {
                // Navigate to training schedule
            }
            NotificationType.COURSE_UPDATE -> {
                // Navigate to course details
            }
            NotificationType.GENERAL_INFO -> {
                // Handle general info action
            }
        }
        // Mark as read after handling
        markAsRead(notification.id)
    }
}