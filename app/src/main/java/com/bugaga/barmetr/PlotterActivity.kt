package com.bugaga.barmetr

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.plotter_layout.*
import java.lang.Exception
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class PlotterActivity : AppCompatActivity() {
    private var plotterBt : myBluetooth? = null
    var plotterHandler = Handler()
    val paint = Paint()
    var configPtxDalaySelextor = -1
    var autoSet = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.plotter_layout)

        paint.color = Color.GREEN
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5F
        paint.isAntiAlias = true

        setAuto.setOnCheckedChangeListener { buttonView, isChecked ->
            autoSet = isChecked
        }

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
                            try {

                                val koef = myImageView.height.toFloat() / 4096f//
                                var myData = msg.obj.toString()
                                var ptxArrA = ArrayList<Float>()
                                var ptxArrB = ArrayList<Float>()
                                //var ptxArrB = ArrayList<Float>()
                                if (myData.indexOf(";") == 0) myData = myData.substring(1)
                                val splitedData = myData.split(";")
                                for (i in 0..splitedData.size/2) ptxArrA.add(splitedData[i].toFloat()*koef)
                                for (i in splitedData.size/2 until splitedData.size) if(splitedData[i] != "") ptxArrB.add(splitedData[i].toFloat()*koef)
                                //splitedData.forEach { if(it != "") ptxArr.add(it.toFloat()*koef) }
                                if (ptxArrA.size > 5) drawFrame(ptxArrA,ptxArrB)
                            }catch (ex : Exception){Output().WriteLine("Parsing data fail: $ex")}
                        }
                    }
                }
            }
    }

    private fun drawFrame(ptxArrA: ArrayList<Float>,ptxArrB: ArrayList<Float>) {
        var startIndex = 1
        if (autoSet){
            var max = 0f
            var cnt = 1
            var got = 0
            do {
                if (ptxArrA[cnt] > max) {
                    max = ptxArrA[cnt]
                    startIndex = cnt
                    got = 0
                    cnt++
                }else{
                    got++
                    cnt++
                }
            }while (cnt<ptxArrA.size && got < 15)
            //Output().WriteLine("Custom startIndex: $startIndex")
        }
        val xStep = myImageView.width.toFloat() / (ptxArrA.size)
        val fullPtxArrA = ArrayList<Float>()
        val fullPtxArrB = ArrayList<Float>()
        val dif = (myImageView.height.toFloat() - ptxArrA.max()!!.toFloat()) / 2f
        val avarg = ptxArrB.max()!!.minus(ptxArrB.min()!!)
        //Original for 1 array;
        for (i in startIndex until (ptxArrA.size-1)){
            fullPtxArrA.add(xStep*(i-startIndex))
            fullPtxArrA.add(myImageView.height - ptxArrA[i-1] - dif)
            fullPtxArrA.add(xStep*(i-startIndex+1))
            fullPtxArrA.add(myImageView.height - ptxArrA[i] - dif)
        }
        for (i in startIndex until (ptxArrB.size-1)){
            fullPtxArrB.add(xStep*(i-startIndex))
            fullPtxArrB.add(myImageView.height - ptxArrB[i-1] - dif)
            fullPtxArrB.add(xStep*(i-startIndex+1))
            fullPtxArrB.add(myImageView.height - ptxArrB[i] - dif)
        }
        /*startIndex = (ptxArr.size-1)/2+1
        for (i in (ptxArr.size-1)/2+1 until ptxArr.size-1){
            fullPtxArrB.add(xStep*(i-startIndex))
            fullPtxArrB.add(myImageView.height - ptxArr[i-1] - dif)
            fullPtxArrB.add(xStep*(i-startIndex+1))
            fullPtxArrB.add(myImageView.height - ptxArr[i] - dif)
        }*/
        val bitmap = Bitmap.createBitmap(myImageView.width, myImageView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        paint.color = Color.BLUE
        canvas.drawLines(fullPtxArrA.toFloatArray(),paint)
        paint.color = Color.YELLOW
        canvas.drawLines(fullPtxArrB.toFloatArray(),paint)
        canvas.drawLine(50f,avarg-dif,myImageView.width.toFloat(),avarg-dif,paint)
        paint.strokeWidth = 0.5f
        paint.textSize = 45f
        canvas.drawText("${avarg.roundToInt()}",0f,avarg,paint)
        paint.strokeWidth = 3f
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
        plotterBt = myBluetooth(applicationContext, plotterHandler,1,"cmd;p:400;d:100;GO")
    }

    override fun onDestroy() {
        super.onDestroy()
        plotterBt?.close()
    }
}