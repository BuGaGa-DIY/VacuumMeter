package com.bugaga.barmetr

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    var mySeekBar:SeekBar? = null
    private var myBt : myBluetooth? = null
    var myHandler = Handler()
    private var MetraUnits = 1
    private var Voltage = 1
    private var lastReadedTime:Long = 0
    private var _firstSample = true
    var delayTime = 10


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Vacuum meter"
        onOffSwitch1.setOnClickListener {
            if (onOffSwitch1.isChecked){
                myProgress.visibility = View.VISIBLE
                TextValue.visibility = View.VISIBLE
                onOffSwitch4.isEnabled = true
                onOffSwitch2.isEnabled = true
            }
            else{
                myProgress.visibility = View.INVISIBLE
                TextValue.visibility = View.INVISIBLE
                if (!onOffSwitch2.isChecked) onOffSwitch4.isEnabled = false
                else if (!onOffSwitch4.isChecked) onOffSwitch2.isEnabled = false
            }

        }
        onOffSwitch2.setOnClickListener {
            if (onOffSwitch2.isChecked){
                myProgress2.visibility = View.VISIBLE
                TextValue2.visibility = View.VISIBLE
                onOffSwitch1.isEnabled = true
                onOffSwitch4.isEnabled = true
            }
            else{
                myProgress2.visibility = View.INVISIBLE
                TextValue2.visibility = View.INVISIBLE
                if (!onOffSwitch1.isChecked) onOffSwitch4.isEnabled = false
                else if (!onOffSwitch4.isChecked) onOffSwitch1.isEnabled = false
            }
        }
        onOffSwitch4.setOnClickListener {
            if (onOffSwitch4.isChecked){
                myProgress4.visibility = View.VISIBLE
                TextValue4.visibility = View.VISIBLE
                onOffSwitch1.isEnabled = true
                onOffSwitch2.isEnabled = true
            }
            else{
                myProgress4.visibility = View.INVISIBLE
                TextValue4.visibility = View.INVISIBLE
                if (!onOffSwitch1.isChecked) onOffSwitch2.isEnabled = false
                else if (!onOffSwitch2.isChecked) onOffSwitch1.isEnabled = false
            }
        }


        myHandler = @SuppressLint("HandlerLeak")
            object : Handler(){
            override fun handleMessage(msg: Message) {
                when(msg.what){
                    10->{
                        try {
                            var str = msg.obj.toString()
                            val value1 = str.substring(0,str.indexOf(";")).toInt()
                            str = str.substring(str.indexOf(";") + 1)
                            val value2 = str.substring(0,str.indexOf(";")).toInt()
                            str = str.substring(str.indexOf(";") + 1)
                            val value3 = str.substring(0,str.indexOf(";")).toInt()
                            str = str.substring(str.indexOf(";") + 1)
                            val value4 = str.toInt()
                            myProgress.progress = value1
                            TextValue.text = (value1 - value3).toString()
                            myProgress2.progress = value2
                            TextValue2.text = (value2 - value3).toString()
                            myProgress3.progress = value3
                            TextValue3.text = value3.toString()
                            myProgress4.progress = value4
                            TextValue4.text = (value4 - value3).toString()
                            val prefs = getSharedPreferences("myLocalPrefs",Context.MODE_PRIVATE)
                            when(prefs.getInt("MatchProgressNr",0)){
                                1->{
                                    if (value1<value3+5 && value1>value3-5) mainLayout.background = getDrawable(R.drawable.lines_g)
                                    else mainLayout.background = getDrawable(R.drawable.lines)
                                }
                                2->{
                                    if (value2<value3+5 && value2>value3-5) mainLayout.background = getDrawable(R.drawable.lines_g)
                                    else mainLayout.background = getDrawable(R.drawable.lines)
                                }
                                4->{
                                    if (value4<value3+5 && value4>value3-5) mainLayout.background = getDrawable(R.drawable.lines_g)
                                    else mainLayout.background = getDrawable(R.drawable.lines)
                                }
                            }
                        }catch (e :Exception){
                            Output().WriteLine("fail pars msg to int, msg: ${msg.obj} msg length: ${msg.obj.toString().length}")
                        }
                    }
                }
            }
        }
    }

    private fun convertUnit(data : String):String{
        when(MetraUnits){
            1 -> return data
            2 -> {
                val rowData = data.toDouble()
                var voltage : Double
                if (Voltage == 2) {
                    voltage = rowData * 3300 / 4095
                    voltage *= 2
                }else
                {
                    voltage = rowData * 5000 / 1023
                }
                var resulr = (voltage - 250) / 45 + 17
                resulr /= 100.0
                resulr -= 1
                return String.format("%.3f",resulr)
            }
            3 -> {
                val rowData = data.toDouble()
                var voltage : Double
                if (Voltage == 2) {
                    voltage = rowData * 3300 / 4095
                    voltage *= 2
                }else
                {
                    voltage = rowData * 5000 / 1023
                }
                val resulr = (voltage - 250) / 45 + 17
                return String.format("%.3f",resulr)
            }
            else -> return data
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.deviceMenuItem->{
                startIntent(BTDevices::class.java)
            }
            R.id.matchPickerMenuItem->{
                myAlertDialogs().showMatchDialog(this)
            }
            R.id.plotterMenuItem->{
                startIntent(PlotterActivity::class.java)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun startIntent(intent_class : Class<*>){
        val startIntent = Intent(applicationContext,intent_class)

        startActivity(startIntent)
    }

    override fun onResume() {
        super.onResume()
        myBt = myBluetooth(applicationContext, myHandler)
    }

    override fun onPause() {
        super.onPause()
        myBt?.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        myBt?.close()
    }
}