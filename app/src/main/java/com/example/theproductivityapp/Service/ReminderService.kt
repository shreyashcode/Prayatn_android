package com.example.theproductivityapp.Service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.theproductivityapp.Reciever.ReminderReceiver
import com.example.theproductivityapp.Utils.Common
import timber.log.Timber

class ReminderService(
    private val context: Context,
    private val timestamp: Long,
    private val title: String,
) {
    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?


    fun setExactAlarm(timeInMillis: Long) {
        setAlarm(
            timeInMillis,
            getPendingIntent(
                getIntent().apply {
                    Timber.d("Realme: SERVICE ----->   $timeInMillis | $title | $timestamp")
                    action = Common.remindAction
                    putExtra("TIME", timeInMillis)
                    putExtra("TITLE", title)
                    putExtra("timeStamp", timestamp)
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        context.startForegroundService(this);
//                    } else {
//                        context.startService(this);
//                    }
                }
            )
        )
    }

    private fun getPendingIntent(intent: Intent) =
        PendingIntent.getBroadcast(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


    private fun setAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
//        alarmManager.set
        alarmManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            }
        }
    }

    private fun getIntent(): Intent {
        val intent = Intent(context, ReminderReceiver::class.java)
//        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        return intent
    }
}