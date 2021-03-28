package com.bugaga.barmetr

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.leinardi.android.speeddial.SpeedDialView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_canvas.*
import kotlinx.android.synthetic.main.menu_list.view.*
import kotlinx.android.synthetic.main.trace_line_layout.view.*
import java.lang.Exception

class mainCanvasActivity : AppCompatActivity() {
    //private var myHandler = Handler()
    var _isGoing = true
    var traceLineIndex = -1

    private var myBt : myBluetooth? = null
    var myHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_canvas)

        val speedDialView = findViewById<SpeedDialView>(R.id.speedDial)
        speedDialView.inflate(R.menu.speed_dial_menu)
        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.plotterSpeedDialMenu -> {
                    speedDialView.close() // To close the Speed Dial with animation
                    startIntent(PlotterActivity::class.java)
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.deviceSpeedDialMenu -> {
                    speedDialView.close() // To close the Speed Dial with animation
                    startIntent(BTDevices::class.java)
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.optionsSpeedDialMenu -> {
                    speedDialView.close() // To close the Speed Dial with animation
                    //showLineTracerDialogMenu()
                    DialogOptionsMenu(this)
                    return@OnActionSelectedListener true // false will close it without animation
                }
            }
            false
        })

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
                            drawFrame(value1,value2,value3,value4)
                        }catch (e :Exception){
                            Output().WriteLine("fail pars msg to int, msg: ${msg.obj} msg length: ${msg.obj.toString().length}")
                        }
                        myBt?.sendData("GO")
                    }
                }
            }
        }
    }

    fun drawFrame(prog1 : Int,prog2 : Int,prog3 : Int,prog4 : Int){
        val prefs = getSharedPreferences("OptionsPrefs",Context.MODE_PRIVATE)
        val bitmap = Bitmap.createBitmap(mainCanvas.width, mainCanvas.height, Bitmap.Config.ARGB_8888)
        var canvas = prepareScreen(bitmap)
        val progressBars = myProgressBar(canvas)
        progressBars.max = prefs.getFloat("MaxProgress",4096f)
        progressBars.min = prefs.getFloat("MinProgress",0f)
        progressBars.drawBars(prog1,prog2,prog3,prog4)
        progressBars.drawTraceLine(prefs.getInt("traceLineIndex",-1))
        mainCanvas.setImageBitmap(bitmap)

    }

    private fun prepareScreen(bitmap: Bitmap):Canvas{
        var canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.WHITE
        canvas.drawRect(0f,0f,mainCanvas.width.toFloat(),mainCanvas.height.toFloat(),paint)
        paint.color = Color.BLACK
        val kof = mainCanvas.height / 100f
        for (i in 0..99){
            canvas.drawLine(0f,i*kof,mainCanvas.width.toFloat(),i*kof,paint)
        }
        val botX = mainCanvas.height.toFloat() - 100f
        val colWight = mainCanvas.width/6f
        val space = colWight*2/5f
        paint.strokeWidth = 7f
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        canvas.drawRect(space,100f,space+colWight,botX,paint)
        canvas.drawRect(space*2 + colWight,100f,space*2+colWight*2,botX,paint)
        canvas.drawRect(space*3 + colWight*2,100f,space*3+colWight*3,botX,paint)
        canvas.drawRect(space*4 + colWight*3,100f,space*4+colWight*4,botX,paint)
        return canvas
    }
    fun startIntent(intent_class : Class<*>, putBT : Boolean = false){
        val startIntent = Intent(applicationContext,intent_class)
        //if (putBT) startIntent.putExtra("BluetoothClass",myBt)
        startActivity(startIntent)
    }
    override fun onResume() {
        super.onResume()
        myBt = myBluetooth(applicationContext, myHandler, lateCommand = "cmd;p:4;d:10;GO")
    }

    override fun onPause() {
        super.onPause()
        myBt?.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        myBt?.close()
    }
    inner class myTasker(val handler: Handler): AsyncTask<Void?,Void?,Void?>(){
        override fun doInBackground(vararg params: Void?): Void? {
            var n = 0
            while (_isGoing) {
                handler.sendMessage(Message.obtain(handler,0,n++,0,"$n;50;10;${100-n};"))
                if (n == 101) {
                    n = 0
                    Thread.sleep(500)
                }
                Thread.sleep(20)

            }
            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: Void?) {

            super.onPostExecute(result)
        }


   }
}

