package br.ufpe.cin.if710.rss

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat

class NewItemObserverReceiver: BroadcastReceiver() {

    override fun onReceive(ctx: Context?, intent: Intent?) {
        if (isInForeground(ctx!!)) return
        val notificationIntent = Intent(ctx, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(
                ctx,
                0,
                notificationIntent,
                0
        )
        val svc = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        svc.notify(1313, buildNotification(ctx, contentIntent))
    }

    fun buildNotification(ctx: Context, intent: PendingIntent): Notification {
        return NotificationCompat.Builder(ctx!!.applicationContext, "xablau")
            .setContentIntent(intent)
            .setContentTitle("Don't miss the news!")
            .setContentText("You've got news on your feed")
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    fun isInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return false
        return runningAppProcesses.any { it.processName == context.packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND }
    }

}