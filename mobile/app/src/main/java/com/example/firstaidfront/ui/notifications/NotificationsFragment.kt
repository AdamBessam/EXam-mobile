package com.example.firstaidfront.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firstaidfront.R
import com.example.firstaidfront.adapter.NotificationAdapter
import com.example.firstaidfront.databinding.FragmentNotificationsBinding
import com.example.firstaidfront.models.Notification
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {
    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var notificationAdapter: NotificationAdapter
    private val viewModel: NotificationsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        observeNotifications()

        // Mark all as read when fragment is visible
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.markAllAsRead()
                notificationAdapter.markAllAsRead()
            }
        }
    }

    private fun setupRecyclerView() {
        notificationAdapter = NotificationAdapter()
        binding.notificationsRecyclerView.apply {
            adapter = notificationAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.primary)
            setOnRefreshListener {
                viewModel.refreshNotifications()
            }
        }
    }

    private fun observeNotifications() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notifications.collect { notifications ->
                binding.emptyState.isVisible = notifications.isEmpty()
                notificationAdapter.setNotifications(notifications)
                updateUnreadCount(notifications)
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun updateUnreadCount(notifications: List<Notification>) {
        val unreadCount = notifications.count { !it.isRead }
        binding.unreadCountText.text = "$unreadCount unread notifications"
        binding.unreadCountText.isVisible = unreadCount > 0
    }
}