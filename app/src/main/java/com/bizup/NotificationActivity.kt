package com.bizup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Calendar

class NotificationActivity : AppCompatActivity() {
    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private val notificationList = mutableListOf<Notification>()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        notificationRecyclerView = findViewById(R.id.notificationRecyclerView)
        notificationRecyclerView.layoutManager = LinearLayoutManager(this)
        notificationAdapter = NotificationAdapter(notificationList)
        notificationRecyclerView.adapter = notificationAdapter


        loadNotifications()
        deleteOldNotifications()
    }

    private fun loadNotifications() {
        db.collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    notificationList.clear()
                    for (document in task.result) {
                        val notification = Notification(
                            document.getString("title") ?: "",
                            document.getString("message") ?: "",
                            document.getTimestamp("timestamp")?.seconds ?: 0L
                        )
                        notificationList.add(notification)
                    }
                    notificationAdapter.notifyDataSetChanged()
                } else {

                }
            }
    }

    private fun deleteOldNotifications() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -2)
        val twoDaysAgo = Timestamp(calendar.time)

        db.collection("notifications")
            .whereLessThan("timestamp", twoDaysAgo)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        db.collection("notifications").document(document.id).delete()
                    }
                } else {

                }
            }
    }

    data class Notification(
        val title: String,
        val message: String,
        val timestamp: Long
    )
}
