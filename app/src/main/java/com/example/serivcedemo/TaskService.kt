package com.example.serivcedemo

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.concurrent.TimeUnit


class TaskService : Service() {

    var countDownTimer: CountDownTimer? = null

    var time = ""

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) addNotification("Start Service")
        else startForeground(1, Notification())

    }


    private fun initStartTime() {
        Thread().apply {
            countDownTimer = object : CountDownTimer(20000, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    time = getSecondsForTimer(millisUntilFinished)
                    addNotification("Service Running..!")
                    Log.d("TAG", "Timer Running:$time")
                }

                override fun onFinish() {
                    Log.d("TAG", "Timer Stopping:$time")
                    stopSelf()
                    addNotification("Service Stopping")
                }
            }
            countDownTimer?.start()
        }.start()

    }

    fun getSecondsForTimer(milliseconds: Long): String {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        return "00:${
            if (seconds.toInt() < 10) "0$seconds" else seconds
        }"
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        initStartTime()
    }

    private fun addNotification(service: String) {
        val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_baseline_insert_link_24)
                .setContentTitle(service)
                .setContentText("Notification Time " + if ("Service Stopping" == service) "Stopped" else time)
        val notificationIntent = Intent(this, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(contentIntent)

        // Add as notification
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, builder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        Intent().apply {
            action = "restartService"
            setClass(this@TaskService, Receiver::class.java)
        }.also {
            sendBroadcast(it)
        }

    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d("testing 12", "onTaskRemoved")
        val restartServiceTask = Intent(applicationContext, TaskService::class.java)
        restartServiceTask.setPackage(packageName)
        val restartPendingIntent = PendingIntent.getService(applicationContext, 1, restartServiceTask, PendingIntent.FLAG_ONE_SHOT)

        super.onTaskRemoved(rootIntent)
    }

    override fun onStartCommand(intent: Intent?, flag: Int, startId: Int): Int {

        Log.d("TAG", "onStartCommand called")
        Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }.also { i ->
            PendingIntent.getActivity(this, 0, i, 0).also {
                addForegroundNotification("Service Restarting", it)
            }
        }
        super.onStartCommand(intent, flag, startId)

        return START_NOT_STICKY
    }



    private fun addForegroundNotification(content: String, pendingIntent: PendingIntent) {
        val builder = NotificationCompat.Builder(this)
                .setOngoing(true)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setSmallIcon(R.drawable.ic_baseline_insert_link_24)
                .setContentTitle(content)
                .setContentText("Notification Time " + if ("Service Stopping" == content) "Stopped" else time)
        builder.setContentIntent(pendingIntent)

        startForeground(2, builder.build())

    }

}