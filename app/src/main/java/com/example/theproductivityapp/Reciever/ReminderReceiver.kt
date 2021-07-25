package com.example.theproductivityapp.Reciever

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Build
import android.text.format.DateFormat
import android.widget.Toast
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.room.util.ViewInfo
import com.example.theproductivityapp.R
import com.example.theproductivityapp.Repository.MainRepository
import com.example.theproductivityapp.db.TodoDao
import com.example.theproductivityapp.ui.Layouts.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject


class ReminderReceiver: BroadcastReceiver() {
    val CHANNEL_ID = "CHANNEL_ID"
    val CHANNEL_NAME = "CHANNEL_NAME"

    override fun onReceive(context: Context, intent: Intent) {
        val timeInMillis = intent.getLongExtra("TIME", 0L)
        val title = intent.getStringExtra("TITLE")!!
        val id = intent.getIntExtra("ID", 1)
        val todo_timestamp = intent.getLongExtra("timeStamp", 0L)

        Timber.d("REALME $title | $id | $todo_timestamp")
//        val job = Job()
//        val coroutineScope = CoroutineScope(job + Dispatchers.Main)
//        coroutineScope.launch {
//            todoDao.deleteReminderByTimeStamp(timeInMillis)
//        }

        Toast.makeText(context, "TITILE ||||||||||||||", Toast.LENGTH_SHORT).show()

        val returnIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("SOURCE", title)
            putExtra("ID", id)
            putExtra("timeStamp", intent.getLongExtra("timeStamp", 0L))
        }
        val pendingIntent = TaskStackBuilder.create(context).run{
            addNextIntentWithParentStack(returnIntent)
            Timber.d("Realme ### ${returnIntent.getStringExtra("SOURCE")} | ${returnIntent.getLongExtra("timeStamp", 0L)}")
            getPendingIntent(todo_timestamp.toInt(), PendingIntent.FLAG_UPDATE_CURRENT)
        }

//        createNotification(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(convertDate(timeInMillis))
            .setSmallIcon(R.drawable.home)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setFullScreenIntent(pendingIntent, true)
            .setChannelId(CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setVibrate(longArrayOf(0))
            .build()

//        if (Build.VERSION.SDK_INT >= 21) notification.(new long[0]);

        val notificationManager = NotificationManagerCompat.from(context)
        Timber.d("Realme is shown?")
        notificationManager.notify(todo_timestamp.toInt(), notification)
    }

    private fun convertDate(timeInMillis: Long): String =
        DateFormat.format("dd/MM/yyyy hh:mm:ss", timeInMillis).toString()
}