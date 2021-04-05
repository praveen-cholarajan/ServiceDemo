package com.example.serivcedemo


import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity(), View.OnClickListener, RegisterListener {

    var countDownTimer: CountDownTimer? = null
    private var b1: Button? = null

    var time = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        b1 = findViewById(R.id.startButton)
        b1?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val receiver = Receiver()
        receiver.registerListener(this)

        val intentFilter = IntentFilter()
        intentFilter.addAction("restartService")
        registerReceiver(receiver, intentFilter)

        startService(Intent(this, TaskService::class.java))

    }

    override fun onDestroy() {
        Log.d("TAG", "Service Process")
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartService"
        broadcastIntent.setClass(this, TaskService::class.java)
        this.sendBroadcast(broadcastIntent)
        super.onDestroy()

    }

}