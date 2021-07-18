package com.example.theproductivityapp.Service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.airbnb.lottie.utils.Utils
import com.example.theproductivityapp.Reciever.ReminderReceiver
import com.example.theproductivityapp.Utils.Common
import java.security.Timestamp

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
                    action = Common.remindAction
                    putExtra("TIME", timeInMillis)
                    putExtra("TITLE", title)
                    putExtra("timeStamp", timestamp)
                }
            )
        )
    }

    private fun getPendingIntent(intent: Intent) =
        PendingIntent.getBroadcast(
            context,
            100,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


    private fun setAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
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

    private fun getIntent() = Intent(context, ReminderReceiver::class.java)
}