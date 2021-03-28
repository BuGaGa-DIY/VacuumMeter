package com.bugaga.barmetr

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import android.widget.Toast
import java.io.IOException
import java.io.Serializable
import java.lang.Exception
import java.util.*

class myBluetooth(var context: Context, var handler: Handler,var readMode : Int = 0,val lateCommand : String = "") : AsyncTask<Void,Void,Void>(), Serializable {

    val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    var btAdapter : BluetoothAdapter? = null
    var btSocket : BluetoothSocket? = null

    var _isReading = false;
    init {
        val bluetoothIntentFilter = IntentFilter()
        bluetoothIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        bluetoothIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)

        val brReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null) {
                    val action = intent.action
                    when(action) {
                        BluetoothDevice.ACTION_ACL_CONNECTED -> {
                            Toast.makeText(context, "Device connected", Toast.LENGTH_SHORT).show()
                            Output().WriteLine("Device Connected in Receiver")
                            handler.sendMessage(Message.obtain(handler,3))
                        }
                        BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                            //Toast.makeText(context, "Device disconnected", Toast.LENGTH_SHORT).show()
                            Output().WriteLine("Device Disconnected in Receiver")
                            handler.sendMessage(Message.obtain(handler,5))
                        }
                    }
                }
            }
        }
        context.registerReceiver(brReceiver,bluetoothIntentFilter)
        execute()
    }

    fun setMyHandler(newHandler: Handler){
        this.handler = newHandler
    }
    fun readBT(){
        while (_isReading){
            var result = ""
            var oneChar: Char
            try {
                if (btSocket!!.inputStream.available()>0){
                    do {
                        oneChar =  btSocket!!.inputStream.read().toChar()
                        if (oneChar != '\n' && oneChar != '\r') result += oneChar
                    }while (oneChar != '\n')
                    handler.sendMessage(Message.obtain(handler, 10, result))
                }
            }catch (ex: IOException){
                Output().WriteLine("Read Bluetooth fail")
            }
        }
    }

    fun readPlotter(){
        while (_isReading){
            val input = btSocket!!.inputStream
            var result = ""
            var ch : Char
            try {
                if(input.available() > 0){
                    do {
                        ch = input.read().toChar()
                        if (ch != '\n' && ch != '\r') result += ch
                    }while (ch != '\n')
                    handler.sendMessage(Message.obtain(handler,11,result))
                }
            }catch (ioe : IOException){
                Output().WriteLine("read plotter fail")
            }
        }
    }


    @Suppress("UNREACHABLE_CODE")
    override fun doInBackground(vararg params: Void?): Void? {
        connect()
        if(lateCommand != "") sendData(lateCommand)
        when(readMode){
            0->readBT()
            1->readPlotter()
        }
        return null
    }

    fun connect():Boolean{
        btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter == null){
            handler.sendMessage(Message.obtain(handler,0))
            return false
        }
        Output().WriteLine("BT adapter not null")
        if (!btAdapter!!.isEnabled) {
            handler.sendMessage(Message.obtain(handler, 1))
            return false
        }
        Output().WriteLine("BT is enabled")
        val deviceMac = context.getSharedPreferences("DevicePrefs",Context.MODE_PRIVATE).getString("MainDeviceMac","")
        Output().WriteLine("device mac: $deviceMac")
        if (deviceMac == "") {
            handler.sendMessage(Message.obtain(handler,2))
            return false
        }
        val device = btAdapter!!.getRemoteDevice(deviceMac)
        btAdapter!!.cancelDiscovery()
        try {
            btSocket = device.createInsecureRfcommSocketToServiceRecord(uuid)
            Output().WriteLine("Socket created")
            btSocket!!.connect()
            Output().WriteLine("Socket connected")
            //sendData("GO")
            _isReading = true
        }catch (ex : IOException){
            Output().WriteLine("socket opening fail: ${ex.message}")
            return false
        }
        return true
    }

    fun sendData(data : String){
        if (btSocket == null){
            handler.sendMessage(Message.obtain(handler,4))
            return
        }
        else{
            try {
                btSocket?.outputStream?.write(data.toByteArray())
            }catch (ex :IOException){
                Output().WriteLine("Data sending fail: ${ex.message}")
            }

        }
    }

    fun isReady():Int{
        if (btSocket != null ) {
            //return btSocket!!.isConnected
            return btAdapter!!.state//not informative
        }
        else return 0
    }

    fun close(){
        _isReading = false

        try {
            btSocket?.close()
            Output().WriteLine("Socket closed")
        }catch (e : Exception){
            Output().WriteLine("Socket closing fail: ${e.message}")
        }
    }
}