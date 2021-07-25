package com.example.theproductivityapp.Service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.text.DateFormat
import android.os.Build
import com.example.theproductivityapp.Reciever.ReminderReceiver
import com.example.theproductivityapp.Utils.Common
import timber.log.Timber

class ReminderService(
    private val context: Context,
    private val timestamp: Long = 0L,
    private val title: String = "",
) {
    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?


    fun setExactAlarm(timeInMillis: Long) {
        Timber.d("REMINDER: FOR SETTING")
        setAlarm(
            timeInMillis,
            getPendingIntent(
                getIntent().apply {
                    action = Common.remindAction
                    putExtra("TIME", timeInMillis)
                    putExtra("TITLE", title)
                    putExtra("timeStamp", timestamp)
                },
                timestamp
            )
        )
    }

    private fun getPendingIntent(intent: Intent, timestamp_: Long): PendingIntent{
        Timber.d("REMINDER REQUEST CODE: ${timestamp_.toInt()}")
        return PendingIntent.getBroadcast(
            context,
            timestamp_.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun setAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {

        Timber.d("REMINDER FOR: ${timeInMillis}")
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

    fun cancelReminder(requestCodeTimeStamp: Long){
        Timber.d("REMINDER CANCELLING")
        if(alarmManager == null){
            Timber.d("REMINDER NULL")
        }
        alarmManager?.cancel(
            getPendingIntent(
                getIntent(),
                requestCodeTimeStamp
            )
        )
    }

    private fun getIntent(): Intent {
        return Intent(context, ReminderReceiver::class.java)
    }

//    private fun convertDate(timeInMillis: Long): String =
//        DateFormat.format("dd/MM hh:mm", timeInMillis).toString()
}