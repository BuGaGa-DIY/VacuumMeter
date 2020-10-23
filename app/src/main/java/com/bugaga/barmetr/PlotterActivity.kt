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
import kotlin.collections.ArrayList
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
                            val koef = myImageView.height.toFloat() / 1024f
                            var myData = msg.obj.toString()
                            //Output().WriteLine("koef: ${koef}")
                            var cnt = 0;
                            //var ptxArr = floatArrayOf()
                            var ptxArr = ArrayList<Float>()
                            if (myData.indexOf(";") == 0) myData = myData.substring(1)
                            while (myData.indexOf(";") >= 0){
                                val ind = myData.indexOf(";")
                                //Output().WriteLine("Data added: ${myData.substring(0,ind).toFloat()}")
                                //ptxArr[cnt++] = myData.substring(0,ind).toFloat()
                                var tmp = myData.substring(0,ind).toFloat()
                                tmp *= koef
                                ptxArr.add( tmp)
                                myData = myData.substring(ind+1)
                            }

                            if (ptxArr.size > 5) drawFrame(ptxArr)
                        }
                    }
                }
            }
    }

    private fun drawFrame(ptxArr: ArrayList<Float>) {
        val xStep = myImageView.width.toFloat() / ptxArr.size
        //val fullPtxArr = floatArrayOf()
        val fullPtxArr = ArrayList<Float>()
        var cnt = 0
        //Output().WriteLine("Creating line array")
        for (i in 1 until ptxArr.size-1){
            fullPtxArr.add(xStep*(i-1))
            fullPtxArr.add(myImageView.height - ptxArr[i-1])
            fullPtxArr.add(xStep*i)
            fullPtxArr.add(myImageView.height - ptxArr[i])
        }
        //Output().WriteLine("Preparing bitmap")
        val bitmap = Bitmap.createBitmap(myImageView.width, myImageView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        canvas.drawLines(fullPtxArr.toFloatArray(),paint)
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