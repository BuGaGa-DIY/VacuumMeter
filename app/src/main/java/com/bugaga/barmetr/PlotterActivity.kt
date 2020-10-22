package com.bugaga.barmetr

import android.annotation.SuppressLint
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.plotter_layout.*
import java.util.*
import kotlin.random.Random.Default.nextInt


class PlotterActivity : AppCompatActivity() {
    private var plotterBt : myBluetooth? = null
    var plotterHandler = Handler()
    val paint = Paint()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.plotter_layout)

        paint.color = Color.GREEN
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5F
        paint.isAntiAlias = true

        plotterHandler = @SuppressLint("HandlerLeak")
            object : Handler(){
                override fun handleMessage(msg: Message) {
                    when(msg.what){
                        11->{
                            var myData = msg.obj.toString()
                            var cnt = 0;
                            var ptxArr = floatArrayOf()
                            while (myData.indexOf(";") >= 0){
                                val ind = myData.indexOf(";")
                                ptxArr.set(cnt++,myData.substring(ind).toFloat())
                                myData = myData.substring(ind+1)
                            }
                            drawFrame(ptxArr)
                        }
                    }
                }
            }

        startBt.setOnClickListener {
            val bitmap = Bitmap.createBitmap(myImageView.width, myImageView.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.TRANSPARENT)
            val x = Random().nextInt(myImageView.width).toFloat()
            val y = Random().nextInt(myImageView.height).toFloat()
            canvas.drawLine(0f,0f,x,y,paint)
            myImageView.setImageBitmap(bitmap)
        }
    }

    private fun drawFrame(ptxArr: FloatArray) {
        val xStep = myImageView.width.toFloat() / ptxArr.size
        val fullPtxArr = floatArrayOf()
        var cnt = 0
        for (i in 1..ptxArr.size){
            fullPtxArr[cnt++] = xStep*(i-1)
            fullPtxArr[cnt++] = myImageView.height - ptxArr[i-1]
            fullPtxArr[cnt++] = xStep*i
            fullPtxArr[cnt++] = myImageView.height - ptxArr[i]
        }
        val bitmap = Bitmap.createBitmap(myImageView.width, myImageView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        canvas.drawLines(fullPtxArr,paint)
        myImageView.setImageBitmap(bitmap)
    }


    override fun onResume() {
        super.onResume()
        plotterBt = myBluetooth(applicationContext, plotterHandler,1)
    }

    override fun onDestroy() {
        super.onDestroy()
        plotterBt?.close()
    }
}