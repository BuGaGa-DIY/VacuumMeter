package com.bugaga.barmetr

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var myBt : myBluetooth? = null
    var myHandler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btDeviceBT.setOnClickListener {
            val startIntent = Intent(applicationContext,BTDevices::class.java)
            startActivity(startIntent)
        }


        myHandler = @SuppressLint("HandlerLeak")
            object : Handler(){
            override fun handleMessage(msg: Message) {
                when(msg.what){
                    10->{
                        try {
                            val value = msg.obj.toString().toInt()
                            mainProgress.progress = value
                            TextValue.text = value.toString()
                        }catch (e :Exception){
                            Output().WriteLine("fail pars msg to int")
                        }
                    }
                }
            }
        }
        myBt = myBluetooth(applicationContext, myHandler)

    }


}