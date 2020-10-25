package com.bugaga.barmetr

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.plotter_config_alert.view.*
import kotlinx.android.synthetic.main.plotter_layout.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random.Default.nextInt


class PlotterActivity : AppCompatActivity() {
    private var plotterBt : myBluetooth? = null
    var plotterHandler = Handler()
    val paint = Paint()

    var configPtxDalaySelextor = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.plotter_layout)

        paint.color = Color.GREEN
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5F
        paint.isAntiAlias = true

        settingsPlotterBT.setOnClickListener{
            floatingBT()
        }
        ptxSeekBarMainLayout.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (configPtxDalaySelextor == 0) {
                    plotterBt?.sendData("cmd;p:${progress};" +
                            "d:${getSharedPreferences("myLocalPrefs", Context.MODE_PRIVATE).getInt("delayCounter",0)};" +
                            "GO")
                    ptxLabelMainLayout.text = "Samples: $progress"
                }else if (configPtxDalaySelextor == 1){
                    plotterBt?.sendData("cmd;" +
                            "p:${getSharedPreferences("myLocalPrefs", Context.MODE_PRIVATE).getInt("ptxCounter",0)}" +
                            "d:${progress};" +
                            "GO")
                    ptxLabelMainLayout.text = "Delay: $progress"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        plotterHandler = @SuppressLint("HandlerLeak")
            object : Handler(){
                override fun handleMessage(msg: Message) {
                    when(msg.what){
                        11->{
                            val koef = myImageView.height.toFloat() / 4096f
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
        val dif = (myImageView.height.toFloat() - ptxArr.max()!!.toFloat()) / 2f
        for (i in 1 until ptxArr.size-1){
            fullPtxArr.add(xStep*(i-1))
            fullPtxArr.add(myImageView.height - ptxArr[i-1] - dif)
            fullPtxArr.add(xStep*i)
            fullPtxArr.add(myImageView.height - ptxArr[i] - dif)
        }
        val bitmap = Bitmap.createBitmap(myImageView.width, myImageView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        canvas.drawLines(fullPtxArr.toFloatArray(),paint)
        myImageView.setImageBitmap(bitmap)
        plotterBt?.sendData("GO")
    }

    private fun floatingBT(){
        //myAlertDialogs().plotterConfigAlert(this,plotterBt)
        if(ptxSeekBarMainLayout.visibility == View.INVISIBLE) {
            ptxSeekBarMainLayout.visibility = View.VISIBLE
            ptxLabelMainLayout.visibility = View.VISIBLE
            val ptx = getSharedPreferences("myLocalPrefs", Context.MODE_PRIVATE).getInt("ptxCounter",500)
            ptxLabelMainLayout.text = "Samples: $ptx"
            ptxSeekBarMainLayout.min = 200
            ptxSeekBarMainLayout.max = 700
            ptxSeekBarMainLayout.progress = ptx
            configPtxDalaySelextor = 0
        }else if(ptxLabelMainLayout.text.indexOf("Samples") > -1){
            val edit = applicationContext.getSharedPreferences("myLocalPrefs", Context.MODE_PRIVATE).edit()
            edit.putInt("ptxCounter",ptxSeekBarMainLayout.progress)
            edit.apply()
            configPtxDalaySelextor = -1
            val dalay = getSharedPreferences("myLocalPrefs", Context.MODE_PRIVATE).getInt("delayCounter",500)
            ptxLabelMainLayout.text = "Delay: $dalay"
            ptxSeekBarMainLayout.min = 0
            ptxSeekBarMainLayout.max = 200
            ptxSeekBarMainLayout.progress = dalay
            configPtxDalaySelextor = 1
        }else{
            val edit = applicationContext.getSharedPreferences("myLocalPrefs", Context.MODE_PRIVATE).edit()
            edit.putInt("delayCounter",ptxSeekBarMainLayout.progress)
            edit.apply()
            ptxSeekBarMainLayout.visibility = View.INVISIBLE
            ptxLabelMainLayout.visibility = View.INVISIBLE
            configPtxDalaySelextor = -1
        }
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