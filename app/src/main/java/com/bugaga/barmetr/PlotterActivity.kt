package com.bugaga.barmetr

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.plotter_layout.*
import java.util.*
import kotlin.random.Random.Default.nextInt


class PlotterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.plotter_layout)


        startBt.setOnClickListener {
            val bitmap = Bitmap.createBitmap(myImageView.width, myImageView.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.TRANSPARENT)
            val paint = Paint()
            paint.color = Color.GREEN
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 5F
            paint.isAntiAlias = true
            val offset = 50
            /*canvas.drawLine(
                offset.toFloat(), (canvas.height / 2).toFloat(), (canvas.width - offset).toFloat(),
                (canvas.height /
                        2).toFloat(), paint)
            val ptx = floatArrayOf(10f,20f,20f,10f,50f,1f,3f,2f)*/
            canvas.drawLine(0f,0f,bitmap.width.toFloat(),bitmap.height.toFloat(),paint)
            //canvas.drawLines(ptx,paint)

            myImageView.setImageBitmap(bitmap)
            Toast.makeText(applicationContext,"${bitmap.width};${bitmap.height}",Toast.LENGTH_LONG).show()
        }

    }
}