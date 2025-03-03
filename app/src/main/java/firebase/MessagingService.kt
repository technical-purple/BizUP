package com.bizup.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MessagingService", "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("MessagingService", "Message received: ${remoteMessage.data}")
        sendNotification(remoteMessage.data)
    }

    private fun sendRegistrationToServer(token: String) {
        // Implement your method to send the token to your server
    }

    private fun sendNotification(data: Map<String, String>) {
        // Implement your method to show the notification
    }
}

