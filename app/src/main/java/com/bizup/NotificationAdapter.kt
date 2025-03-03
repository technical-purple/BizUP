package com.bizup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import com.bizup.NotificationActivity.Notification


class NotificationAdapter(private val notificationList: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.notificationTitle)
        val messageTextView: TextView = view.findViewById(R.id.notificationMessage)
        val timestampTextView: TextView = view.findViewById(R.id.notificationTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationList[position]
        holder.titleTextView.text = notification.title
        holder.messageTextView.text = notification.message

        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        val dateString = sdf.format(Date(notification.timestamp))
        holder.timestampTextView.text = dateString
    }

    override fun getItemCount() = notificationList.size
}
