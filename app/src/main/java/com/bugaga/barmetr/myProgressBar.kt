package com.bugaga.barmetr

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlinx.android.synthetic.main.main_canvas.*

class myProgressBar(canvas : Canvas,number : Int, progress : Int) {
    var max = 100f
    var min = 0f
    var Progress = progress
    private var mainCanvas : Canvas = canvas
    private  var BarNumber = number
    init {
        var paint = Paint()
        paint.color = Color.WHITE

        val botX = mainCanvas.height.toFloat() - 110f
        val colWight = mainCanvas.width/6f
        val space = colWight*2/5f
        val step =  (mainCanvas.height.toFloat() - 220f) / (max - min)//!!This part is important
        val top = mainCanvas.height.toFloat() - 110f - step* Progress
        canvas.drawRect(space*(BarNumber+1) + colWight*BarNumber +10f,110f,space*(BarNumber+1)+colWight*(BarNumber+1)-10f,botX,paint)
        when(BarNumber){
            0->{
                paint.color = Color.BLUE
            }
            1->{
                paint.color = Color.RED
            }
            2->{
                paint.color = Color.GREEN
            }
            3->{
                paint.color = Color.MAGENTA
            }
        }
        canvas.drawRect(space*(BarNumber+1) + colWight*BarNumber +10f,top,space*(BarNumber+1)+colWight*(BarNumber+1)-10f,botX,paint)
    }



}