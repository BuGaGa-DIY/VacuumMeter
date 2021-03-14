package com.bugaga.barmetr

import android.annotation.SuppressLint
import android.graphics.*
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import kotlinx.android.synthetic.main.main_canvas.*

class mainCanvasActivity : AppCompatActivity() {
    private var myHandler = Handler()
    var _isGoing = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_canvas)

        myHandler = @SuppressLint("HandlerLeak")
            object : Handler(){
                override fun handleMessage(msg: Message) {
                    drawFrame(msg.arg1)
                }
            }
        var tmp = myTasker(myHandler)
        tmp.execute()
        //drawFraim()

    }

    fun drawFrame(prog : Int){
        var x = mainCanvas.width.toFloat() / 2f
        var y = mainCanvas.height.toFloat() / 2f

        val bitmap = Bitmap.createBitmap(mainCanvas.width, mainCanvas.height, Bitmap.Config.ARGB_8888)
        val canvas = prepareScreen(bitmap)
        val paint = Paint()

        paint.color = Color.GREEN
        val botX = mainCanvas.height.toFloat() - 110f
        val colWight = mainCanvas.width/6f
        val space = colWight*2/5f
        //var tmp = (mainCanvas.height.toFloat() - 220f) / 100f * (100f - prog.toFloat())
        val step =  (mainCanvas.height.toFloat() - 220f) / 100f
        val tmp = mainCanvas.height.toFloat() - 110f - step* prog
        canvas.drawRect(space+10f,tmp,space+colWight-10f,botX,paint)
        paint.color = Color.RED
        canvas.drawRect(space*2 + colWight +10f,tmp,space*2+colWight*2-10f,botX,paint)
        paint.color = Color.YELLOW
        canvas.drawRect(space*3 + colWight*2 +10f,tmp,space*3+colWight*3-10f,botX,paint)
        paint.color = Color.BLUE
        canvas.drawRect(space*4 + colWight*3 +10f,tmp,space*4+colWight*4-10f,botX,paint)
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
    inner class myTasker(val handler: Handler): AsyncTask<Void?,Void?,Void?>(){
        override fun doInBackground(vararg params: Void?): Void? {
            var n = 0
            while (_isGoing) {
                handler.sendMessage(Message.obtain(handler,0,n++,0))
                if (n == 101) {
                    n = 0
                    Thread.sleep(500)
                }
                Thread.sleep(10)

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

