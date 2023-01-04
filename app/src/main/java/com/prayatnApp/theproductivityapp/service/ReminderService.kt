package com.prayatnApp.theproductivityapp.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.prayatnApp.theproductivityapp.receiver.ReminderReceiver
import com.prayatnApp.theproductivityapp.utils.Common
import com.prayatnApp.theproductivityapp.db.tables.Category
import com.prayatnApp.theproductivityapp.di.BaseApplication
import java.util.*

class ReminderService(
    private val context: Context,
    private val TodoTimeStamp: Long = 0L,
    private val title: String = "",
) {
    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
    private val MORNING_REQUESTCODE = 100
    private val EVENING_REQUESTCODE = 101

    fun setExactAlarm(remindTime: Long) {
        setAlarm(
            remindTime,
            getPendingIntent(
                getIntent(remindTime),
                TodoTimeStamp
            )
        )
    }

    fun setRepeatingAlarm(hr: Int, min: Int, category: Category){
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hr)
            set(Calendar.MINUTE, min)
            set(Calendar.SECOND, 0)
        }
        val requestCode = if(category == Category.MORNING) MORNING_REQUESTCODE else EVENING_REQUESTCODE
        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            24*60*60*1000,
            getPendingIntent(
                getIntent(category.toString()),
                requestCode
            )
        )
    }

    fun cancelRepeatingAlarm(){
        alarmManager!!.cancel(getPendingIntent(
                getIntent(Category.MORNING.toString()),
                MORNING_REQUESTCODE
            )
        )
        alarmManager.cancel(
            getPendingIntent(
                getIntent(Category.EVENING.toString()),
                EVENING_REQUESTCODE
            )
        )
    }

    private fun getPendingIntent(intent: Intent, requestCodeTimeStamp: Long): PendingIntent{
        return PendingIntent.getBroadcast(
            BaseApplication.applicationContext(),
            requestCodeTimeStamp.toInt(),
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    private fun getPendingIntent(intent: Intent, requestCode: Int): PendingIntent{
        return PendingIntent.getBroadcast(
            BaseApplication.applicationContext(),
            requestCode,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
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
    }

    fun cancelReminder(requestCodeTimeStamp: Long, timeInMillis: Long){
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
            putExtra("TYPE", "ToDo")
        }
    }
    private fun getIntent(category: String): Intent  = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("TYPE", "STANDUP")
        putExtra("TITLE", category)
        action = category
    }
}