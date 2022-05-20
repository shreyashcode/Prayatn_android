package com.example.theproductivityapp.Service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.theproductivityapp.Reciever.ReminderReceiver
import com.example.theproductivityapp.Utils.Common
import com.example.theproductivityapp.di.BaseApplication
import timber.log.Timber

class ReminderService(
    private val context: Context,
    private val TodoTimeStamp: Long = 0L,
    private val title: String = "",
) {
    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

    fun setExactAlarm(remindTime: Long) {
        Timber.d("REMINDER: FOR SETTING")
        setAlarm(
            remindTime,
            getPendingIntent(
                getIntent(remindTime),
                TodoTimeStamp
            )
        )
    }

    private fun getPendingIntent(intent: Intent, requestCodeTimeStamp: Long): PendingIntent{
        return PendingIntent.getBroadcast(
            BaseApplication.applicationContext(),
            requestCodeTimeStamp.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

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
//        Timber.d("REMINDER__: Alarm canceled")
//        cancelReminder(TodoTimeStamp, timeInMillis)
    }

    fun cancelReminder(requestCodeTimeStamp: Long, timeInMillis: Long){
        Timber.d("REMINDER__ CANCELLING $timeInMillis")
        if(alarmManager == null){
            Timber.d("REMINDER_ NULL")
        }
        alarmManager!!.cancel(
            getPendingIntent(
                getIntent(timeInMillis),
                requestCodeTimeStamp
            )
        )
    }

    private fun getIntent(timeInMillis: Long): Intent {
        return Intent(context, ReminderReceiver::class.java).apply {
            putExtra("NULL", TodoTimeStamp)
            action = Common.remindAction
            putExtra("TIME", timeInMillis)
            putExtra("TITLE", title)
            putExtra("timeStamp", TodoTimeStamp)
        }
    }

//    private fun convertDate(timeInMillis: Long): String =
//        DateFormat.format("dd/MM hh:mm", timeInMillis).toString()
}