package com.example.todo_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val todoTitle = inputData.getString("todoTitle")
        showNotification(todoTitle)
        return Result.success()
    }

    private fun showNotification(todoTitle: String?) {
        val context = applicationContext

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "ReminderChannel"
            val channelDescription = "Channel for Todo Reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("REMINDER_CHANNEL", channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, "REMINDER_CHANNEL")
            .setSmallIcon(R.drawable.baseline_notification_important_24)
            .setContentTitle("Reminder")
            .setContentText("Don't forget: $todoTitle")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }
}
