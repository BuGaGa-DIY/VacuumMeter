package com.bugaga.barmetr

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.adapret_layout.view.*
import kotlinx.android.synthetic.main.main_canvas.*
import kotlinx.android.synthetic.main.menu_list.view.*

class mainCanvasActivity : AppCompatActivity() {
    private var myHandler = Handler()
    var _isGoing = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_canvas)

        fabOptions.setOnClickListener {
            ShowOptionsMenu()
        }

        myHandler = @SuppressLint("HandlerLeak")
            object : Handler(){
                override fun handleMessage(msg: Message) {
                    when(msg.what){
                        0 -> {
                            /*val data = msg.obj.toString()
                            val slittedData = data.split(";")
                            val m1 = slittedData[0].toInt()
                            val m2 = slittedData[2].toInt()
                            val m3 = slittedData[3].toInt()
                            val m4 = slittedData[4].toInt()*/

                            drawFrame(msg.arg1,50,35,101 - msg.arg1)
                        }
                    }
                }
            }
        var tmp = myTasker(myHandler)
        tmp.execute()

    }

    fun ShowOptionsMenu(){
        val builder = AlertDialog.Builder(this)
        var inflater = LayoutInflater.from(applicationContext)
            .inflate(R.layout.menu_list,null)
        inflater.mainMenuList.adapter = myAdapter(applicationContext, listOf("Plotter","BT Device","Track Line"))
        inflater.mainMenuList.setOnItemClickListener { parent, view, position, id ->
            when(position){
                0 -> startIntent(PlotterActivity::class.java)
                1 -> startIntent(BTDevices::class.java)
                2 -> showLineTracerDialogMenu()
            }

        }
        builder.setView(inflater)
        builder.setPositiveButton("ok"){dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }
    fun showLineTracerDialogMenu(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("line picker")

        builder.setPositiveButton("ok"){dialog, which ->
            dialog.dismiss()
        }
        builder.show()

    }
    fun drawFrame(prog1 : Int,prog2 : Int,prog3 : Int,prog4 : Int){

        val bitmap = Bitmap.createBitmap(mainCanvas.width, mainCanvas.height, Bitmap.Config.ARGB_8888)
        val canvas = prepareScreen(bitmap)
        myProgressBar(canvas,0,prog1)
        myProgressBar(canvas,1,prog2)
        myProgressBar(canvas,2,prog3)
        myProgressBar(canvas,3,prog4)

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

