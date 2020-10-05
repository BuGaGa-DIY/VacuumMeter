package com.bugaga.barmetr

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuItem
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

        MetraGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.MetraRow -> MetraUnits = 1
                R.id.MetraBar -> MetraUnits = 2
                R.id.MetraPascal -> MetraUnits = 3
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
                            //if(value1 > 500) myProgress.set
                            TextValue.text = convertUnit(value1.toString())
                            myProgress2.progress = value2
                            TextValue2.text = convertUnit(value2.toString())
                            myProgress3.progress = value3
                            TextValue3.text = convertUnit(value3.toString())
                            myProgress4.progress = value4
                            TextValue4.text = convertUnit(value4.toString())
                            /*if(sampleArray.size == 0){
                                sampleArray.add(value1)
                                lastReadedTime = System.currentTimeMillis()
                            }
                            else if (System.currentTimeMillis() - lastReadedTime > delayTime){
                                sampleArray.add(value1)
                            }

                            if (sampleArray.size == 10){
                                var tmp:Long = 0
                                sampleArray.forEach { it->tmp+=it }
                                sampleArray.clear()
                                tmp = tmp /10
                                myProgress.progress = value1
                                TextValue.text = convertUnit(value1.toString())
                            }*/
                            /*
                            myProgress.progress = value1
                            myProgress2.progress = value2
                            TextValue.text = convertUnit(value1.toString())
                            TextValue2.text = convertUnit(value2.toString())
                             */
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
                //Toast.makeText(this,"menu item cliced",Toast.LENGTH_SHORT).show()
                val startIntent = Intent(applicationContext,BTDevices::class.java)
                startActivity(startIntent)
            }
        }
        return super.onOptionsItemSelected(item)
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