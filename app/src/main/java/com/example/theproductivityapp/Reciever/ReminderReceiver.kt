package com.example.theproductivityapp.Reciever

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.format.DateFormat
import android.widget.Toast
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.theproductivityapp.R
import com.example.theproductivityapp.ui.Layouts.MainActivity
import timber.log.Timber

class ReminderReceiver: BroadcastReceiver() {
    val CHANNEL_ID = "CHANNEL_ID"
    val CHANNEL_NAME = "CHANNEL_NAME"

    override fun onReceive(context: Context, intent: Intent) {
        val timeInMillis = intent.getLongExtra("TIME", 0L)
        val title = intent.getStringExtra("TITLE")!!
        val id = intent.getIntExtra("ID", 1)

        Toast.makeText(context, "TITILE ||||||||||||||", Toast.LENGTH_SHORT).show()
        Timber.d("REALME $title | $id")

        val returnIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("SOURCE", title)
            putExtra("ID", id)
            putExtra("timeStamp", intent.getLongExtra("timeStamp", 0L))
        }
        val pendingIntent = TaskStackBuilder.create(context).run{
            addNextIntentWithParentStack(returnIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        createNotification(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(convertDate(timeInMillis))
            .setSmallIcon(R.drawable.home)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, notification)
    }

    private fun createNotification(context: Context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun convertDate(timeInMillis: Long): String =
        DateFormat.format("dd/MM/yyyy hh:mm:ss", timeInMillis).toString()
}