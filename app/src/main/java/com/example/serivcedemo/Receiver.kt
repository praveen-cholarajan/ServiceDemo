package com.example.serivcedemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

import android.widget.Toast


class Receiver : BroadcastReceiver() {

    private var listener: RegisterListener? = null

    fun registerListener(listener: RegisterListener) {
        this.listener = listener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Receiver", "onReceive Called")
        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context!!.startForegroundService(Intent(context,
                    TaskService::class.java))
        } else {
            context!!.startService(Intent(context, TaskService::class.java))
        }
    }
}