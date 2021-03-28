package com.bugaga.barmetr

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class myProgressBar(canvas : Canvas) {
    var max = 4096f
    var min = 0f
    var Progress = 0
    var Prograss1 = 0
    var Prograss2 = 0
    var Prograss3 = 0
    var Prograss4 = 0
    private var mainCanvas = canvas
    private  var BarNumber = -1

    var botX = mainCanvas.height.toFloat() - 110f
    val colWight = mainCanvas.width/6f
    val space = colWight*2/5f
    var step = 0f

    private var paint = Paint()

    fun drawBars(p1:Int,p2:Int,p3:Int,p4:Int){
        step =  (mainCanvas.height.toFloat() - 220f) / (max - min)//!!This part is important
        Prograss1 = p1
        Prograss2 = p2
        Prograss3 = p3
        Prograss4 = p4
        drawBar(0,p1)
        drawBar(1,p2)
        drawBar(2,p3)
        drawBar(3,p4)
    }

    fun drawBar(number : Int, progress : Int):Canvas{
        BarNumber = number
        Progress = progress
        val top = mainCanvas.height.toFloat() - 110f - step* (Progress-min)
        paint.color = Color.WHITE

        mainCanvas.drawRect(space*(BarNumber+1) + colWight*BarNumber +10f,110f,space*(BarNumber+1)+colWight*(BarNumber+1)-10f,botX,paint)
        getPaintColor(number)
        mainCanvas.drawRect(space*(BarNumber+1) + colWight*BarNumber +10f,top,space*(BarNumber+1)+colWight*(BarNumber+1)-10f,botX,paint)
        return mainCanvas
    }

    fun drawTraceLine(index : Int){
        Progress = when(index){
            0-> Prograss1
            1-> Prograss2
            2-> Prograss3
            3-> Prograss4
            else->return
        }
        val top = mainCanvas.height.toFloat() - 110f - step* (Progress-min)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5F
        paint.isAntiAlias = true
        getPaintColor(index)

        mainCanvas.drawLine(0f,top,mainCanvas.width.toFloat(),top,paint)

    }
    private fun getPaintColor(index: Int){
        when(index){
            0->paint.color = Color.BLUE
            1->paint.color = Color.RED
            2->paint.color = Color.GREEN
            3->paint.color = Color.MAGENTA
        }

    }

}