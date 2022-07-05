package com.example.theproductivityapp.Reciever

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.theproductivityapp.R
import com.example.theproductivityapp.ui.ActivityScreens.MainActivity
import timber.log.Timber


class ReminderReceiver: BroadcastReceiver() {
    val CHANNEL_ID = "CHANNEL_ID"
    val CHANNEL_NAME = "CHANNEL_NAME"
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var pendingIntent: PendingIntent
    private lateinit var returnIntent: Intent

    // TODO: do some case work here, to send the standup intent.
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("TYPE")
        if(type == "STANDUP"){
            standupReminder(context, intent)
        } else {
            todoReminder(context, intent)
        }
        returnIntent.putExtra("NavigateStatus", false)
        pendingIntent = TaskStackBuilder.create(context).run{
            addNextIntentWithParentStack(returnIntent)
            getPendingIntent(System.currentTimeMillis().toInt(), PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.home)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setFullScreenIntent(pendingIntent, true)
            .setChannelId(CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setVibrate(longArrayOf(0))
            .build()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun standupReminder(context: Context, intent: Intent){
        title = "SELF-STANDUP time, ready? \uD83D\uDD0D"
        description = "A half truth is a whole lie. Be true to yourself!"
        returnIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("TYPE", "Standup")
        }
    }

    private fun todoReminder(context: Context, intent: Intent){
        description = convertDate(intent.getLongExtra("TIME", 0L))
        title = intent.getStringExtra("TITLE")!!
        val id = intent.getIntExtra("ID", 1)
        returnIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("TYPE", "Todo")
            putExtra("SOURCE", title)
            putExtra("ID", id)
            putExtra("timeStamp", intent.getLongExtra("timeStamp", 0L))
        }
    }

    private fun convertDate(timeInMillis: Long): String =
        DateFormat.format("dd/MM/yyyy hh:mm:ss", timeInMillis).toString()
}